pipeline {
    agent any
    tools {
        maven 'maven3' // Use the Maven tool configured in Jenkins
    }
    environment {
        BROWSER = 'chrome'
        REMOTE_URL = 'http://selenoid:4444/wd/hub/'
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
        stage('Generate Allure Report') {
            steps {
                sh 'mvn allure:report'
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: '**/target/allure-results/**', allowEmptyArchive: true
            junit '**/target/surefire-reports/*.xml'
        }
        success {
            allure includeProperties: false, results: [[path: '**/target/allure-results']]
        }
    }
}