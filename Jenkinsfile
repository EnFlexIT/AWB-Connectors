pipeline {
  agent any
  stages {
    stage('Snapshot Build & Deploy for Java 21') {
      steps {
        echo 'Start Snapshot Build and Deployment of AWB Connector Features ...'
        sh 'mvn --version'
        sh 'mvn clean install -P p2Deploy -f eclipseProjects/de.enflexit.connector -Dtycho.localArtifacts=ignore -Dtycho.p2.transport.min-cache-minutes=0'
        echo 'Build & Deployment of AWB Connector Features!'
      }
    }

  }
}