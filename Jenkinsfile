pipeline {
  agent any
  tools {
    maven 'maven-3.8.7'
  }
  stages {
    stage ('Build') {
      steps {
        echo 'Build and run unit tests'
        sh 'mvn clean package'
      }
    }
    stage ('Beta') {
      steps {
        echo 'Deploy to Beta stage'
      }
    }
    stage ('Prod') {
      steps {
        echo 'Deploy to Prod stage'
      }
    }
  }
}