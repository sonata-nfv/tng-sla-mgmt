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
        stage('Build tng-sla-mgmt') {
          steps {
            echo 'not implemented yet'
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
            echo 'not implemented yet'
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
            echo 'not implemented yet'
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
            echo 'not implemented yet'
          }
        }
      }
    }
    stage('Publish Results') {
      parallel {
        stage('Publish Results') {
          steps {
            echo 'Publish Results...'
          }
        }
        stage('Publication') {
          steps {
            echo 'not implemented yet'
          }
        }
      }
    }
  }
}