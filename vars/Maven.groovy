def cal(body) {
    def config = [:]
    body.resolveStrategy = clousre.DELEGATE_FIRST
    body.delegate = config
    body()
    def mavenGoals = config.mavenGoals
    def branchName = config.branchName
    def pomLocationName = config.pomLocation

    /*docker.image('').inside('--net=host -v /app/maven:/app/maven')
    {

    }
    */
    sh 'mvn ${mavenGoals} -f ${WORKSPACE}/${pomLocationName}'
}
