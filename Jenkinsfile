pipeline {
  agent { label "master" }
  stages {
    stage("Build") {
      steps {
        sh """
          docker-compose -f docker-compose.yml up -d --build
        """
      }
    }
  }
  post{
    always{
      sh """curl 'https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage?chat_id=${CHAT_ID}&text=[${env.ENVIRONMENT}] ${env.JOB_NAME} – Build number ${env.BUILD_NUMBER} – ${currentBuild.currentResult}!'"""
    }
  }

}


