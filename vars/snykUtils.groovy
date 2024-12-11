// vars/snykUtils.groovy
def runSnykTest(String repoDir) {
    dir(repoDir) {
        sh """
        snyk test --token=${env.SNYK_TOKEN}
        """
    }
}

def runSnykMonitor(String repoDir) {
    dir(repoDir) {
        sh """
        snyk monitor --token=${env.SNYK_TOKEN}
        """
    }
}
