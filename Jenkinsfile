pipeline {
    environment {
        def REGISTRY_URL = "docker.stx"
        def MVN_SETTINGS_PATH = 'maven-settings.xml'
        def SERVICE = 'tasklist'
        def serviceBranch = 'develop'
        def stage = "Test"

    }
    agent {
        kubernetes {
            inheritFrom 'deployment-pod-service'
            //idleMinutes 30
            yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: maven
    image: maven:3.9.11-eclipse-temurin-21-alpine
    imagePullPolicy: IfNotPresent
    command:
    - cat
    tty: true
  - name: docker
    image: docker:24.0.6-git
    imagePullPolicy: IfNotPresent
    command:
    - cat
    tty: true
    volumeMounts:
      - name: docker
        mountPath: /var/run/docker.sock
  - name: helm-kubectl
    image: alpine/k8s:1.24.16
    imagePullPolicy: IfNotPresent
    command:
    - cat
    tty: true
  volumes:
  - name: docker
    hostPath:
      path: /var/run/docker.sock
"""
        }
    }
    stages {
        stage('Init') {
            steps {
                script {
                    currentBuild.description = "Build deploy branch: " + serviceBranch + " stage: " + stage
                     configFileProvider([
                            configFile(fileId: 'MVN-SETTINGS-XML', 'targetLocation': MVN_SETTINGS_PATH)
                    ]) {
                    }
                    sh ('ls')
                }
            }
        }
        stage('maven & sonar') {
            steps {
                container('maven') {
                    withCredentials([
                            string(credentialsId: 'sonar-jenkins-token', variable: 'SONAR_TOKEN')
                    ]) {
    sh ('pwd')
    sh ('ls -a')
    sh ('java -version')
    sh ('mvn -v')
    sh ('mvn clean install sonar:sonar -s ${MVN_SETTINGS_PATH} -f pom.xml -Dsonar.projectKey=${SERVICE} -Dsonar.token=${SONAR_TOKEN}')
                    }
                }
            }
        }
        stage('docker') {
            steps {
                container('docker') {
                    withCredentials([usernamePassword(credentialsId: "REGISTRY_CREDENTIALS",
                                    usernameVariable: 'REGISTRY_USER', passwordVariable: 'REGISTRY_PASS')]) {
                        sh """
            docker info
            echo ${REGISTRY_PASS} | docker login ${REGISTRY_URL} -u ${REGISTRY_USER} --password-stdin"""
                    }
                }
            }
        }
        stage('helm & kubectl') {
            steps {
                container('helm-kubectl') {
                    withCredentials([file(credentialsId: "KUBECONFIG", variable: 'KUBECONFIG')]) {
                        sh 'helm version'
                        sh 'kubectl version'
                    }
                }
            }
        }
    }
}
