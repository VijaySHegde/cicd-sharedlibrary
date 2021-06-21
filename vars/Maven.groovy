def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    def mavenGoals = config.mavenGoals
    def branchName = config.branchName
    def pomLocationName = config.pomLocation

  /*  docker.image('https://hub.docker.com/_/maven').inside('--net=host -v /app/maven:/app/maven')
    {
sh 'mvn ${mavenGoals} -f ${WORKSPACE}/${pomLocationName}'
    }
    */
    println(mavenGoals)
    println(pomLocationName)
   sh """
   mvn ${mavenGoals} -f ${WORKSPACE}/${pomLocationName}
   """
}
