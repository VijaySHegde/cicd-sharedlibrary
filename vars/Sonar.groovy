def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    def applicationName = config.applicationName
    def projectName = config.projectName
    def projectKey = config.projectKey
    def projectVersion = config.projectVersion
    def sonarLanguage = config.sonarLanguage
    def sonarSources = config.sonarSources
    docker.image('').inside("--net=host") {
        withSonarQubeEnv('Sonarerver') {
        
    
            sh """
                        npm install -g typescript@latest
                        npm install typescript@latest
                        java -version                        
                        ${sonarscanner}/bin/sonar-scanner -Dsonar.projectKey=${projectKey} \
                                                          -Dsonar.projectName=${projectName} \
                                                          -Dsonar.projectVersion=${projectVersion} \
                                                          -Dsonar.sources=${sonarSources} \
                                                          -Dsonar.language=${sonarLanguage} 
                                                          

             """

             // add below command to get more log --> 
             //-Dsonar.verbose=true
        }        
    }
} //end of sonar
