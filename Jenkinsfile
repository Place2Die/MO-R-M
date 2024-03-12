pipeline {
    agent any
    environment {
        QODANA_TOKEN=credentials('qodana-token')
    }
    parameters {
        string(name: 'NEW_VERSION', defaultValue: null, description: 'New version to deploy')
    }
    stages {
        stage('🔑 Checkout') {
            steps {
                // Clean before checkout
                cleanWs()

                // Clone the repository
                checkout scm

                // Setup .m2/settings.xml with secret file
                sh 'mkdir -p .m2'
                withCredentials([file(credentialsId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
                    sh 'cp $MAVEN_SETTINGS .m2/settings.xml'
                }
            }
        }
        stage('🕵️ Lint') {
            agent {
                docker {
                    args '''
                      -v "${WORKSPACE}":/data/project
                      --entrypoint=""
                      '''
                    image 'jetbrains/qodana-jvm-community'
                }
            }
            steps {
                sh '''qodana --fail-threshold 70'''
            }
        }
        stage('🏗️ Build') {
            steps {
                ansiColor('xterm') {
                    // Build the project
                    sh 'mvn --settings .m2/settings.xml clean compile'
                }
                // Archive the build artifacts
                archiveArtifacts artifacts: 'target/*.jar', onlyIfSuccessful: true
            }
        }
        stage ('🧪 Tests') {
            steps {
                ansiColor('xterm') {
                    // Run tests
                    sh 'mvn --settings .m2/settings.xml test'
                }

                // Publish test results
                junit 'target/surefire-reports/*.xml'

                // Publish code coverage using JaCoCo
                jacoco(execPattern: 'target/jacoco.exec')
            }
        }
        stage('🏷️ Release') {
            when {
                expression {
                    return params.NEW_VERSION != null
                }
            }
            steps {
                ansiColor('xterm') {
                    // Update the version
                    sh "mvn --settings .m2/settings.xml versions:set -DnewVersion=${params.NEW_VERSION}"
                }

                // Commit the changes
                sh 'git commit -am ":bookmark: Release ${params.NEW_VERSION}"'

                // Tag the release
                sh "git tag -a ${params.NEW_VERSION} -m 'Release ${params.NEW_VERSION}'"

                // Push the changes
                sh 'git push origin master --tags'
            }
        }
        stage('📦 Deploy') {
            when {
                branch 'master'
            }
            steps {
                ansiColor('xterm') {
                    // Deploy the application
                    sh 'mvn --settings .m2/settings.xml deploy'
                }
            }
        }
    }
    post {
        // Clean after build
        always {
            cleanWs(cleanWhenNotBuilt: true,
                    deleteDirs: true,
                    disableDeferredWipeout: true,
                    notFailBuild: true,
                    patterns: [[pattern: '.gitignore', type: 'INCLUDE'],
                               [pattern: '.propsfile', type: 'EXCLUDE']])
        }
    }
}
