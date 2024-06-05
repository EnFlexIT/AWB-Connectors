pipeline {
  agent any
  stages {
    stage('Snapshot Build & Deploy for Java 11') {
      steps {
        echo 'Start Snapshot Build and Deployment of AWB Connector Features ...'
        sh 'mvn --version'
        sh 'mvn clean install -P p2Deploy -f eclipseProjects/de.enflexit.connector -Dtycho.localArtifacts=ignore'
        echo 'Build & Deployment of AWB Connector Features!'
      }
    }

  }
}