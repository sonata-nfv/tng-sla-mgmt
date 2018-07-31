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
            sh 'docker build -t registry.sonata-nfv.eu:5000/tng-sla-mgmt:v4.0 -f sla-template-generator/Dockerfile .'
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
            sh 'mvn clean test -f sla-template-generator'
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
             sh 'mvn site -f sla-template-generator'
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
            sh 'docker push registry.sonata-nfv.eu:5000/tng-sla-mgmt:v4.0'
          }
		}
      }
    }	
	
	stage('Deployment in Staging Env') {
          parallel {
            stage('Deployment in Staging Env') {
              steps {
                echo 'Deploying in Staging Env...'
              }
            }
            stage('Deploying') {
              steps {
                sh 'rm -rf tng-devops || true'
                sh 'git clone https://github.com/sonata-nfv/tng-devops.git'
                dir(path: 'tng-devops') {
                  sh 'ansible-playbook roles/sp.yml -i environments -e "target=sta-sp-v4.0 host_key_checking=False component=sla-management"'
                }
              }
            }
          }
	}
	
  }
  
  post {
    always {
	  junit(allowEmptyResults: true, testResults: 'sla-template-generator/target/surefire-reports/*.xml')
	  junit(allowEmptyResults: true, testResults: 'sla-template-generator/target/checkstyle-result.xml')
    }  
	}
}
