// sonarPipeline.groovy

def call(config = [:]) {
    pipeline {
        agent any
        
        environment {
            // Ensure that variables are quoted properly
            NARQUBE_ENV = config.sonarEnv ?: 'SonarQ'
            SONARQUBE_TOKEN = credentials('sonar-token')
        }

        stages {
            stage('Checkout') {
                steps {
                    checkout scm
                }
            }

            stage('SonarQube Analysis') {
                steps {
                    script {
                        // Call your SonarQube scanning tool here
                        sh """
                            sonar-scanner \
                                -Dsonar.projectKey=${config.sonarProjectKey} \
                                -Dsonar.sources=src \
                                -Dsonar.host.url=${config.sonarUrl} \
                                -Dsonar.login=${SONARQUBE_TOKEN}
                        """
                    }
                }
            }
        }

        post {
            success {
                echo 'SonarQube analysis completed successfully!'
            }
            failure {
                echo 'SonarQube analysis failed.'
            }
        }
    }
}
