import java.text.SimpleDateFormat
import hudson.tasks.test.AbstractTestResultAction
import hudson.model.Actionable
//import jenkins.model.*
//jenkins = Jenkins.instance

def call(body){
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def applicationName = config.applicationName ?: 'petclinic'
    def buildNode = config.buildNode ?: ''
    def mvnGoals = config.mvnGoals ?: ''
    def pomFileLocation = config.pomFileLocation ?: 'pom.xml'
    def executeMavenGoal = config.executeMavenGoal
    def executeCoverage = config.executeCoverage ?: 'YES'
    
    def executeUnitTest = config.executeUnitTest ?: 'NO'
    def junitReportLocation = config.junitReportLocation ?: ''
    def jacocoClassPattern =config.jacocoClassPattern ?: '**/classes'
    def jacocoExecPattern = config.jacocoExecPattern ?: '**/**.exec'
    def jacocoSourceInclusionPattern = config.jacocoSourceInclusionPattern ?: '**/*.java'
    def jacocoSourcePattern = config.jacocoSourcePattern ?: '**/src/main/java'

    def executeSonar = config.executeSonar ?: 'yes'
    
    def executeNexusIQ = config.executeNexusIQ ?: 'yes'
    def nexusAppName = config.nexusAppName
    def packagePath = config.packagePath
    def branch = ''
    def deployApprover = 'vijay'
    def failedStage = 'None'
    branch = "${BRANCH_NAME}"

    timestamps {
        try {
            node("${buildNode}") {
                stage("checkout") {
                    pipelineStage = "${STAGE_NAME}"
                    step([$class: 'WsCleanup'])
                    checkout scm
                    pom = readMavenPom file: "${pomFileLocation}"
/*
                    def settingsfileCreate = libraryResource('')
                    writeFile file : 'settingsfile.xml', text: file
                    def file = readFile ("${WORKSPACE}/settingfile.xml").replace('NEXUS_SNAPSHOT_URL', "")
                    writeFile file : "settings.xml", text: file
*/
                }//end of checkout stage
		    if("${executeMavenGoal}".toUpperCase()=='YES') {
                stage("Build") {
                    pipelineStage = "${STAGE_NAME}"
                    Maven {
                        mavenGoals = "${mvnGoals}"
                        branchName = "${branch}"
                        pomLocation = "${pomFileLocation}"
                    } 
                    if("${executeUnitTest}".toUpperCase()=='YES') {
                        junit "${junitReportLocation}"
                        AbstractTestResultAction testResultAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
                        if(testResultAction != null)
                        {
				println("found test cases")

                        }
                        else {
                            println("no unit test cases in application")

                        }
                        
                    }
                }//end of build stage
		    }

                if("${executeCoverage}".toUpperCase() == 'YES') {
                    stage("Unit test with coverage") {
                        pipelineStage = "${STAGE_NAME}"
                        Coverage {
                            junitResultLocation = "${junitReportlocation}"
                            classPattern = "${jacocoClassPattern}"
			    execPattern="${jacocoExecPattern}"
			    sourceInclusionPattern ="${jacocoSourceInclusionPattern}"
			    sourcePattern  = "${jacocoSourcePattern}"
                        }


                    } //end of unit test
			
		  stage("build docker image and push") {
                    pipelineStage = "${STAGE_NAME}"
                 /*   sh """
                    docker build -t vijayshegde/spring-petclinic-2.4.5.jar:$BUILD_NUMBER .
                    """
                   */ 
                     docker.withRegistry('', 'dockerhub') {
                         def customImage = docker.build("vijayshegde/spring-petclinic-2.4.5.jar:${BUILD_NUMBER}")
                         customImage.push()
                     }
                }//end  of build docker 
                }
                if("${executeSonar}".toUpperCase() == 'YES') {
                    stage("sonar") {
                        pipelineStage = "${STAGE_NAME}"
                        Sonar {
			 
                            applicationName = config.applicationName
                            projectName = config.sonarProjectName
                            projectKey = config.sonarProjectKey
                            projectVersion = config.sonarProjectVersion
                            sonarLanguage = config.sonarProjectLanguage
                            sonarSources = config.sonarSources
                            
                        }
                    }//end of sonar stage

                    stage("sonar quality gate") {
                        pipelineStage = "${STAGE_NAME}"

                        try {
                            timeout(time:1, unit: 'HOURS') {
                                def qualityGate = waitForQualityGate()
                                if(qualityGate.status != 'OK') {
                                    error "pipeline aborted due to qulaity gate failure"
                                }
                            }//timeout end
                        }//end of try in sonar gate
                        catch(Exception err) {
                            error "sonar gate failed"
                        }
                    }// end of gate stage
                    
                } //end of sonar if statement
		     if("${executeNexusIQ}".toUpperCase() == 'YES') {
			    stage('NexusIQ Scan') {
                            pipelineStage = "${STAGE_NAME}"
                            nexusIQAppName = "${nexusAppName }"
                            packagePath_nexusIQ = "${packagePath}"
                            nexusIQJSONInfo = nexusPolicyEvaluation advancedProperties: '', failBuildOnNetworkError: false,
                                    iqApplication: selectedApplication("${nexusIQAppName}"),
                                    iqScanPatterns: [[scanPattern: "${packagePath_nexusIQ}"]],
                                    iqStage: 'build', jobCredentialsId: "${credential}"
                            criticalComponentCount = "${nexusIQJSONInfo.criticalComponentCount}"

                            manager.build.@result = (("${pipelineStage}" == 'NexusIQ Scan') && ("${currentBuild.result}" == 'UNSTABLE')) ? hudson.model.Result.SUCCESS : manager.build.@result
                            
                        }
		     }
		    
		    
		    stage("Approval") {
			    pipelineStage = "${STAGE_NAME}"
			    echo "Taking approval for deployment"
			    timeout(time: 7, unit:'DAYS') {
			    input message: 'Do you want to deploy', submitter: 'vijay'
			    }
		    }//end of approval
		    stage("DB deployment-mysql conatiner") {
			    pipelineStage = "${STAGE_NAME}"
			    sh """
			    docker container ls
			   
			    """
			    echo "App is deployed using mysql profile as separate db conntainer"
			   /* sh """
			    docker rm --force spring-petclinic_mysql_1
			    docker rmi --force mysql:5.7
			    timeout 60s docker-compose up
			    timeout 60s mvn spring-boot:run -Dspring-boot.run.profiles=mysql
			    """
			    */
		    }//end of db deploy
		    stage("Docker deploy") {
			    sh """
			    
			    docker run -d -p 8090:8080 --name petclinic vijayshegde/spring-petclinic-2.4.5.jar:${BUILD_NUMBER}
			    """
			    
		    }//END OF DOCKER DEPLOY
                
                /* stage("push docker image") {
//                     docker.withRegistry('', 'dockerhub') {
                        
//                     }
                        
             
                     withCredentials([string(credentialsId: 'dockerhub', variable: 'dockerHubPwd')]) {
                     sh """
                     docker login -u vijayshegde -p ${dockerHubPwd}
                     
                     """
                     }
                     sh 'docker push vijayshegde/spring-petclinic-2.4.5.jar:$BUILD_NUMBER'
                     
                    
                }//end of push docker image
*/
                stage("Deploy") {
			 pipelineStage = "${STAGE_NAME}"
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', 
	                    accessKeyVariable: 'AWS_ACCESS_KEY_ID', 
	                    credentialsId: 'AWS_Credentials', 
	                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
	                        withCredentials([kubeconfigFile(credentialsId: 'kubernetes_config', 
	                        variable: 'KUBECONFIG')]) {
                            //sh "sudo ansible-playbook playbook.yml --extra-vars image_id=vijayshegde/spring-petclinic-2.4.5.jar:${BUILD_NUMBER}"
			     // sh "sudo ansible-playbook playbook.yml --extra-vars image_id=vijayshegde/spring-petclinic-2.4.5.jar:latest"
					//sh 'kubectl create -f kubernetes-configmap.yml'
					try{
						sh 'kubectl apply -f kubernetes-configmap.yml'
					}
					catch(error) {
						sh 'kubectl create -f kubernetes-configmap.yml'
					}
							}
							}
			//using k8s deploy plugin-2nd way				
			/*kubernetesDeploy(
				configs:'kubernetes-configmap.yml',
				kubeconfigid:'kubernetes_config',
				enableConfigSubstitution:true
				)
				*/
                }//end of deploy stage
                 
            }//end of node

        }//end of try
        catch(Exception err) {
            failedStage = "${pipelineStage}"
            echo "Build failed at ${pipelineStage} with ${err}"
	    currentBuild.result = 'FAILURE'
        }

    }//end of timestamps


    }// main body ends
