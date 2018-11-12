#!groovy
import com.orgecc.jpl.AndroidLibraryPipeline
import com.orgecc.jpl.StarCityPipeline
import com.orgecc.jpl.GitPipeline
import com.orgecc.jpl.NotificationPipeline
import com.orgecc.jpl.Base


def base          = new Base()
def android       = new AndroidLibraryPipeline()
def metrics       = new StarCityPipeline()
def git           = new GitPipeline()
def notification  = new NotificationPipeline()

pipeline {

    options {
      skipDefaultCheckout()
      disableConcurrentBuilds()
      timeout(time: 1, unit: 'HOURS')
      timestamps()
    }

    agent { node { label 'android' } }

    stages {

        stage("Checkout") {
            steps {
              script {
                base.dvcsCheckout()
                sh 'printenv'
              }
            }
        }

        stage("Clean") {
            steps {
              script {
                android.clean()
              }
            }
        }

        stage("Tests") {
            steps {
                script {
                    android.runTests()
                }
            }
        }

        stage("Code Scan") {
            steps {
                script {
                    android.codeStyleCheck()
                }
            }
        }

        stage("Build") {
            steps {
                script {
                    android.build()
                }
            }
        }

        stage("Publish") {
            steps {
                script {
                    android.publish()
                }
            }
        }

        stage("Create Tag") {
           when {
               branch 'master'
           }
            steps {
                script {
                    git.createGitTagOnAndroidLibraryByGradleTaskConvention()
                }
            }
        }
    }

    post {
        success {
            cleanWs()
        }
        always {
            script {
                notification.send_message_for_libs()
            }
        }
    }

}

