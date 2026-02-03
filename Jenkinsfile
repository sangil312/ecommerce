pipeline {
  agent { label 'ci' }

  options {
    skipDefaultCheckout(true)
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        sh 'echo "CHANGE_ID=${CHANGE_ID}"'
        // 여기에 실제 빌드/테스트 명령
        sh 'chmod +x ./gradlew'
        sh './gradlew clean test'
      }
    }
  }

  post {
    always {
      // JUnit 쓰면 결과 수집(경로는 프로젝트에 맞게)
      // junit 'build/test-results/test/*.xml'
      echo 'Done'
    }
  }
}
