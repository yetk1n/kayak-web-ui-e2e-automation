pipeline {
    agent any
    tools {
        maven 'Maven' // Use the Maven tool configured in Jenkins
    }
    environment {
        BROWSER = 'chrome'
        REMOTE_URL = 'http://selenoid:4444/wd/hub'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        stage('Run Tests') {
            steps {
                sh 'mvn test -Dbrowser=$BROWSER -DremoteUrl=$REMOTE_URL'
            }
        }
        stage('Generate Report') {
            steps {
                sh 'mvn allure:report'
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: '**/target/*.xml', allowEmptyArchive: true
            junit '**/target/surefire-reports/*.xml'
        }
    }
}