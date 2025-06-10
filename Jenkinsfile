pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK'
    }

    environment {
        SONAR_SCANNER_HOME = tool 'SonarQubeScanner'
        SONAR_TOKEN = credentials('sonar-token')
        DOCKER_COMPOSE_FILE = 'src/main/docker/compose.yml'
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


        // Étape 2 : Environnement préparation
        stage('Setup Test Environnement') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'docker-compose -f ${DOCKER_COMPOSE_FILE} up -d keycloak_database keycloak'
                        sh 'sleep 30'
                    } else {
                        sh 'docker-compose -f ${DOCKER_COMPOSE_FILE} up -d keycloak_database keycloak'
                        sh 'sleep 30'
                    }
                }
            }
        }

        // 3- Build
        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn clean install -Ptest'
                    } else {
                        bat 'mvn clean install -Ptest'
                    }
                }
            }
        }

        // Étape 4 : Exécution des tests unitaires
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


        // Étape 5 : Scan des vulnérabilités des dépendances (OWASP)
        stage('Dependency Check') {
            steps {
                dependencyCheck additionalArguments: '--scan target/', odcInstallation: 'owasp-dependancy-check'
                    dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
            }
        }

        // Étape 6 : Analyse statique avec SonarQube
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
        always {
            // Nettoyer les conteneurs de test
            sh 'docker-compose -f ${DOCKER_COMPOSE_FILE} down'

            // Publier les résultats
            junit 'target/surefire-reports/*.xml'

            // Archiver les artifacts
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
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