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
            }
        }
        stage('🕵️ Lint') {
            agent {
                docker {
                    args '''
                      -v "${WORKSPACE}":/data/project
                      --entrypoint=""
                      '''
                    image 'jetbrains/qodana-<linter>'
                }
            }
            steps {
                sh '''qodana --fail-threshold 0.7'''
            }
        }
        stage('🏗️ Build') {
            steps {
                // Build the project
                sh 'mvn clean install'

                // Archive the build artifacts
                archiveArtifacts artifacts: 'target/*.jar', onlyIfSuccessful: true
            }
        }
        stage ('🧪 Tests') {
            steps {
                // Run tests
                sh 'mvn test'

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
                // Update the version
                sh "mvn versions:set -DnewVersion=${params.NEW_VERSION}"

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
                // Deploy the application
                sh 'mvn deploy'
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
