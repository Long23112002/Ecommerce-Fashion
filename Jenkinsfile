pipeline {
    agent { label "master" }
    environment {
        PATH = "/usr/bin:${PATH}"
    }
    stages {
        stage("Build") {
            steps {
                sh "docker-compose -f docker-compose.yml up -d --build"
            }
        }
    }
    post {
        always {
            def message = "[${ENVIRONMENT}] ${env.JOB_NAME} - Build number ${env.BUILD_NUMBER} - ${currentBuild.currentResult}!"
                   def encodedMessage = message.replace(' ', '%20').replace('–', '-')

                   sh """curl -s 'https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage?chat_id=${CHAT_ID}&text=${encodedMessage}'"""
        }
    }
}
