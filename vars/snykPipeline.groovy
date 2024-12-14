def call(Map config = [:]) {
    pipeline {
        agent any

        environment {
            SNYK_TOKEN = credentials('snyk')
        }

        stages {
            stage('Clone Repository') {
                steps {
                    script {
                        sh "git clone ${config.repoUrl}"
                    }
                }
            }

            stage('Snyk Test') {
                steps {
                    script {
                        sh 'ls'
                        sh "cd ${config.repoName}"
                        sh "snyk code test --token=${SNYK_TOKEN}"
                    }
                }
            }

            stage('Clean Workspace') {
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
                    echo "Build completed with status: ${currentBuild.currentResult}"
                }
            }
        }
    }
}
