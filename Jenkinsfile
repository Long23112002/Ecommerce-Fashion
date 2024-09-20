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
             script {
                 // Tạo thông điệp
                 def message = "[DEV] ecommerce-fashion - Build number ${env.BUILD_NUMBER} - ${currentBuild.currentResult}!"
                 // Mã hóa thông điệp
                 def encodedMessage = message.replace(' ', '%20').replace('–', '-')

                 // Gọi lệnh curl
                 sh """curl -s 'https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage?chat_id=${CHAT_ID}&text=${encodedMessage}'"""
             }
         }
     }
}
