pipeline {
  agent any
  stages {
    stage('Container Build') {
      parallel {
        stage('Container Build') {
          steps {
            echo 'Building..'
          }
        }
		stage('Building tng-sla-mgmt') {
          steps {
            sh 'docker build -t registry.sonata-nfv.eu:5000/tng-sla-mgmt -f sla-template-generator/Dockerfile .'
          }
		}
      }
    }
    stage('Unit Tests') {
      parallel {
        stage('Unit Tests') {
          steps {			
            echo 'Unit Testing..'
          }
        }
        stage('Unit tests for tng-sla-mgmt') {
          steps {
            sh 'mvn test'
          }
        }
      }
    }
    stage('Code Style check') {
      parallel {
        stage('Code Style check') {
          steps {
            echo 'Code Style check....'
          }
        }
        stage('Code check for tng-sla-mgmt') {
          steps {
             sh 'mvn site'
          }
        }
      }
    }
    stage('Containers Publication') {
      parallel {
        stage('Containers Publication') {
          steps {
            echo 'Publication of containers in local registry....'
          }
        }
		stage('Publishing tng-sla-mgmt') {
          steps {
            sh 'docker push registry.sonata-nfv.eu:5000/tng-sla-mgmt'
          }
		}
      }
    }	
  }
  post {
    always {
      junit 'target/surefire-reports/*.xml'
	  check 'target/checkstyle-result.xml'
    }  
	}
}