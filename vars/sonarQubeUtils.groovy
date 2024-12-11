// vars/sonarQubeUtils.groovy
def runSonarQube(String projectKey, String sources = '.', String coveragePath = null) {
    def scannerHome = tool 'sonar'
    withSonarQubeEnv('SonarQube') {
        sh """
        ${scannerHome}/bin/sonar-scanner \
        -Dsonar.projectKey=${projectKey} \
        -Dsonar.sources=${sources} \
        -Dsonar.host.url=${env.SONAR_HOST_URL} \
        -Dsonar.login=${env.SONAR_AUTH_TOKEN} \
        ${coveragePath ? "-Dsonar.python.coverage.reportPaths=${coveragePath}" : ''}
        """
    }
}
