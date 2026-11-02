pipeline {
    agent any
    tools { maven 'Maven' }
    triggers { pollSCM('H/2 * * * *') }
    stages {
        stage('Checkout') { steps { checkout scm } }
        stage('Test') { steps { sh 'mvn -B test' } }
        stage('Build') { steps { sh 'mvn -B package' } }
    }
    post {
        success { archiveArtifacts artifacts: 'target/*.jar', fingerprint: true }
    }
}
