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
            sh 'docker push registry.sonata-nfv.eu:5000/tng-sla-mgmt'
          }
		}
      }
    }	
	
	stage('Deployment in Pre-Integration') {
          parallel {
            stage('Deployment in Pre-Integration') {
              steps {
                echo 'Deploying in Pre-integration...'
              }
            }
            stage('Deploying') {
              steps {
                sh 'rm -rf tng-devops || true'
                sh 'git clone https://github.com/sonata-nfv/tng-devops.git'
                dir(path: 'tng-devops') {
                  sh 'ansible-playbook roles/sp.yml -i environments -e "target=pre-int-sp host_key_checking=False component=sla-management"'
                }
              }
            }
          }
	}
	
	stage('Promoting to integration') {
      when{
        branch 'master'
      }      
      steps {
        sh 'docker tag registry.sonata-nfv.eu:5000/tng-sla-mgmt:latest registry.sonata-nfv.eu:5000/tng-sla-mgmt:int'
        sh 'docker push registry.sonata-nfv.eu:5000/tng-sla-mgmt:int'
        sh 'rm -rf tng-devops || true'
        sh 'git clone https://github.com/sonata-nfv/tng-devops.git'
        dir(path: 'tng-devops') {
		  sh 'ansible-playbook roles/sp.yml -i environments -e "target=int-sp component=sla-management"'
        }
      }
    }	
	stage('Promoting release v5.0') {
        when {
            branch 'v5.0'
        }
        stages {
            stage('Generating release') {
                steps {
                    sh 'docker tag registry.sonata-nfv.eu:5000/tng-sla-mgmt:latest registry.sonata-nfv.eu:5000/tng-sla-mgmt:v5.0'
                    sh 'docker tag registry.sonata-nfv.eu:5000/tng-sla-mgmt:latest sonatanfv/tng-sla-mgmt:v5.0'
                    sh 'docker push registry.sonata-nfv.eu:5000/tng-sla-mgmt:v5.0'
                    sh 'docker push sonatanfv/tng-sla-mgmt:v5.0'
                }
            }
            stage('Deploying in v5.0 servers') {
                steps {
                    sh 'rm -rf tng-devops || true'
                    sh 'git clone https://github.com/sonata-nfv/tng-devops.git'
                    dir(path: 'tng-devops') {
                    sh 'ansible-playbook roles/sp.yml -i environments -e "target=sta-sp-v5-0 component=sla-management"'
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
