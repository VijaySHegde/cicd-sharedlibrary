def cal(body) {
    def config = [:]
    body.resolveStrategy = clousre.DELEGATE_FIRST
    body.delegate = config
    body()

    def junitResultLocation = config.junitResultLocation
    def classPattern = config.classPattern
    def execPattern = config.execPattern
    def sourceInclusionPattern = config.sourceInclusionPattern
    def sourcePattern = config.sourcePattern

    jacoco classPattern: "${classPattern}", execPattern: "${execPattern}", sourceInclusionPattern: "${sourceInclusionPattern}", sourcePattern: "${sourcePattern}"
}
