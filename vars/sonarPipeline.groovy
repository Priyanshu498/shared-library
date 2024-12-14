def call(Map config = [:]) {
    pipeline {
        agent any

        stages {
            stage('Setup Environment') {
                steps {
                    script {
                        env.SONARQUBE_ENV = config.sonarEnv ?: 'SonarQube'
                    }
                }
            }

            stage('Clone Repository') {
                steps {
                    script {
                        sh "git clone ${config.repoUrl}"
                    }
                }
            }

            stage('SonarQube Analysis') {
                steps {
                    script {
                        def scannerHome = tool(name: 'sonar', type: 'hudson.plugins.sonar.SonarRunnerInstallation')
                        withSonarQubeEnv(env.SONARQUBE_ENV) {
                            sh """
                            ${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=${config.projectKey} \
                            -Dsonar.sources=${config.sources ?: '.'} \
                            -Dsonar.host.url=${SONAR_HOST_URL} \
                            -Dsonar.login=${SONAR_AUTH_TOKEN}
                            """
                        }
                    }
                }
            }

            stage('Build Status') {
                steps {
                    script {
                        if (currentBuild.currentResult == 'SUCCESS') {
                            echo 'SonarQube analysis completed successfully!'
                        } else {
                            echo 'SonarQube analysis encountered issues.'
                        }
                    }
                }
            }

            stage('Cleanup Workspace') {
                steps {
                    cleanWs()
                }
            }
        }

        post {
            success {
                script {
                    slackSend(
                        channel: '#jenkinnotify',
                        message: "Build SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                        color: "good"
                    )
                }
            }
            failure {
                script {
                    slackSend(
                        channel: '#jenkinnotify',
                        message: "Build FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                        color: "danger"
                    )
                }
            }
            always {
                script {
                    echo "Build finished with status: ${currentBuild.currentResult}"
                }
            }
        }
    }
}
