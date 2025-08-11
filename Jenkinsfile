pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "api-automation-tests"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t $DOCKER_IMAGE ."
                }
            }
        }

        stage('Run Tests with Docker Compose') {
            steps {
                script {
                    sh 'docker-compose down || true' // Clean up any previous runs
                    sh 'docker-compose up --abort-on-container-exit --build'
                }
            }
        }

        stage('Archive Reports') {
            steps {
                archiveArtifacts artifacts: 'target/**/*.*', allowEmptyArchive: true
                junit 'target/surefire-reports/*.xml'
                publishHTML(target: [
                  reportDir: 'target',
                  reportFiles: 'cucumber-report.html',
                  reportName: 'Cucumber HTML Report',
                  keepAll: true
                ])
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
