import java.text.SimpleDateFormat
import hudson.tasks.test.AbstractTestResultAction
import hudson.model.Actionable
import jenkins.model.*
jenkins = Jenkins.instance

def call(body){
    def config = [:]
    body.resolveStrategy = clousre.DELEGATE_FIRST
    body.delegate = config
    body()

    def applicationName = config.applicationName ?: 'petclinic'
    def buildNode = config.buildNode ?: ''
    def mvnGoals = config.mvnGoals ?: ''
    def pomFileLocation = config.pomFileLocation ?: 'pom.xml'
    
    def executeUnitTest = config.executeUnitTest ?: 'NO'
    def junitReportLocation = config.junitReportLocation ?: ''

    def executeSonar = config.executeSonar ?: 'yes'
    
    def executeNexusIQ = config.executeNexusIQ ?: 'yes'
    def branch = ''
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

                    def settingsfileCreate = libraryResource('')
                    writeFile file : 'settingsfile.xml', text: file
                    def file = readFile ("${WORKSPACE}/settingfile.xml").replace('NEXUS_SNAPSHOT_URL', "")
                    writeFile file : "settings.xml", text: file

                }//end of checkout stage

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

                        }
                        else {
                            println("no unit test cases in application")

                        }
                        
                    }
                }//end of build stage

                if("${executeUnitTest}".toUpperCase() == 'YES') {
                    stage("Unit test with coverage") {
                        pipelineStage = "${STAGE_NAME}"
                        Coverage {
                            junitResultLocation = "${junitReportlocation}"
                            classPattern = "${jacocoClassPattern}"
                        }


                    } //end of unit test
                }
                if("${executeSonar}".toUpperCase() == 'YES') {
                    stage("sonar") {
                        pipelineStage = "${STAGE_NAME}"
                        Sonar {

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

                 
            }//end of node

        }//end of try
        catch(Exception err) {
            failedStage = "${pipelineStage}"
            echo "Build failed at ${pipelineStage} with ${err}"
        }

    }//end of timestamps


    }// main body ends
