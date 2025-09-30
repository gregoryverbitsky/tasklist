pipeline {
    environment {
        def REGISTRY_URL = "registry.stx"
        def MVN_SETTINGS_XML = 'maven-settings.xml'
        def SERVICE = 'tasklist'
        def IMAGE_TAG = '0.0.1'
        def serviceBranch = 'develop'
        def stage = "Test"

    }
    agent {
        kubernetes {
            inheritFrom 'deployment-pod-service'
            idleMinutes 30
            yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: maven
    image: docker.stx/maven:3.9.11-eclipse-temurin-21-alpine
    imagePullPolicy: IfNotPresent
    command:
    - cat
    tty: true
    volumeMounts:
    - name: maven
      mountPath: /root/.m2/repository
  - name: docker
    image: docker.stx/docker:24.0.6-git
    imagePullPolicy: IfNotPresent
    command:
    - cat
    tty: true
    volumeMounts:
      - name: docker
        mountPath: /var/run/docker.sock
  - name: helm-kubectl
    image: docker.stx/alpine/k8s:1.24.16
    imagePullPolicy: IfNotPresent
    command:
    - cat
    tty: true
  volumes:
  - name: maven
    persistentVolumeClaim:
      claimName: maven-repo-storage
  - name: docker
    hostPath:
      path: /var/run/docker.sock
  imagePullSecrets:
  - name: docker-proxy-credentials
"""
        }
    }
    stages {
        stage('maven package') {
            steps {
                container('maven') {
                    configFileProvider([
                        configFile(fileId: 'MVN-SETTINGS-XML', 'targetLocation': MVN_SETTINGS_XML) ]) {}
    sh ('pwd')
    sh ('ls -a')
    sh ('java -version')
    sh ('mvn -v')
    sh ('mvn clean package -s ${MVN_SETTINGS_XML} -f pom.xml')
                }
            }
        }
        stage('sonar:sonar') {
            steps {
                container('maven') {
                    withCredentials([
                            string(credentialsId: 'sonar-jenkins-token', variable: 'SONAR_TOKEN') ]) {
    sh ('pwd')
    sh ('ls -a')
    sh ('java -version')
    sh ('mvn -v')
    sh ('mvn sonar:sonar -s ${MVN_SETTINGS_XML} -f pom.xml -Dsonar.projectKey=${SERVICE} -Dsonar.projectName=${SERVICE} -Dsonar.token=${SONAR_TOKEN}')
                    }
                }
            }
        }
        stage('docker build') {
            steps {
                container('docker') {
                    withCredentials([usernamePassword(credentialsId: "REGISTRY_CREDENTIALS",
                                    usernameVariable: 'REGISTRY_USER', passwordVariable: 'REGISTRY_PASS')]) {
    sh ('pwd')
    sh ('ls -a')
    sh ('docker info')
    sh ('echo ${REGISTRY_PASS} | docker login ${REGISTRY_URL} -u ${REGISTRY_USER} --password-stdin')
    sh ('docker build -t ${REGISTRY_URL}/${SERVICE}:${IMAGE_TAG} . ')
                    }
                }
            }
        }
        stage('trivy security scanning') {
            steps {
                container('docker') {
    sh ('pwd')
    sh ('ls -a')
    sh """
        docker run --rm \
          -v /var/run/docker.sock:/var/run/docker.sock \
          -v \$HOME/.trivy-cache:/root/.cache/ \
          docker.stx/aquasec/trivy:0.66.0 image \
          --scanners vuln \
          --severity MEDIUM,HIGH,CRITICAL \
          --exit-code 0 \
          --format table \
          ${REGISTRY_URL}/${SERVICE}:${IMAGE_TAG}  > trivy-output.txt
       """
    sh ('cat trivy-output.txt')
               }
           }
        }
        stage('docker push') {
            steps {
                container('docker') {
    sh ('docker push ${REGISTRY_URL}/${SERVICE}:${IMAGE_TAG} ')
                        }
                    }
                }
        stage('helm & kubectl') {
            steps {
                container('helm-kubectl') {
                    withCredentials([file(credentialsId: "KUBECONFIG", variable: 'KUBECONFIG')]) {
    sh ('pwd')
    sh ('ls -a')
    sh ('helm version')
    sh ('kubectl version --short')
                    }
                }
            }
        }
    }
}
