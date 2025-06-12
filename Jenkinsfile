pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK'
    }

    environment {
        SONAR_SCANNER_HOME = tool 'SonarQubeScanner'
        SONAR_TOKEN = credentials('sonar-token')
    }

    stages {

        // Étape 1 : Vérification de l'environnement
        stage('Vérification Environnement') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn --version'
                        sh 'java --version'
                    } else {
                        bat 'mvn --version'
                        bat 'java --version'
                    }
                }
            }
        }

        // 2- Build
        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn clean install "-Dspring-boot.run.profiles=test"'
                    } else {
                        bat 'mvn clean install "-Dspring-boot.run.profiles=test"'
                    }
                }
            }
        }

        // Étape 3 : Exécution des tests unitaires
        stage('Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn test'
                    } else {
                        bat 'mvn test'
                    }
                }
            }
        }


        // Étape 4 : Scan des vulnérabilités des dépendances (OWASP)
        stage('Dependency Check') {
            steps {
                dependencyCheck additionalArguments: '--scan target/', odcInstallation: 'owasp-dependancy-check'
                    dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
            }
        }

        // Étape 5 : Analyse statique avec SonarQube
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    script {
                        def scannerHome = tool 'SonarQubeScanner'
                        withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN_SECURE')]) {
                            if (isUnix()) {
                                sh """
                                    ${scannerHome}/bin/sonar-scanner \
                                    -Dsonar.projectKey=admin-management \
                                    -Dsonar.projectName='admin-management' \
                                    -Dsonar.java.binaries=target/classes \
                                    -Dsonar.sources=src/main/java \
                                    -Dsonar.tests=src/test/java \
                                    -Dsonar.junit.reportPaths=target/surefire-reports \
                                    -Dsonar.jacoco.reportPaths=target/jacoco.exec \
                                    -Dsonar.token=$SONAR_TOKEN_SECURE \
                                    -Dsonar.host.url=http://host.docker.internal:9000
                                """
                            } else {
                                bat """
                                    ${scannerHome}/bin/sonar-scanner \
                                    -Dsonar.projectKey=admin-management \
                                    -Dsonar.projectName='admin-management' \
                                    -Dsonar.java.binaries=target/classes \
                                    -Dsonar.sources=src/main/java \
                                    -Dsonar.tests=src/test/java \
                                    -Dsonar.junit.reportPaths=target/surefire-reports \
                                    -Dsonar.jacoco.reportPaths=target/jacoco.exec \
                                    -Dsonar.token=$SONAR_TOKEN_SECURE \
                                    -Dsonar.host.url=http://host.docker.internal:9000
                                """
                            }
                        }
                    }
                }
            }
        }

    }

    // Post-actions : Notification en cas d'échec
    post {
        failure {
            emailext (
                subject: '[URGENT] Échec du Pipeline ${JOB_NAME} - Build #${BUILD_NUMBER}',
                body: '''
                    <h2>❌ Pipeline en échec</h2>
                    <p><b>Projet</b>: ${JOB_NAME}</p>
                    <p><b>Build</b>: <a href="${BUILD_URL}">#${BUILD_NUMBER}</a></p>
                    <p><b>Cause</b>: ${BUILD_CAUSE}</p>
                    <p><b>Logs</b>: <a href="${BUILD_URL}console">Consulter les logs</a></p>
                ''',
                to: 'kangnigabiam720@gmail.com',
                recipientProviders: [[$class: 'DevelopersRecipientProvider']],
                mimeType: 'text/html'
            )
        }
    }
}