def buildjar() {
    echo "building the application..."
    sh 'mvn clean package'
}

def incrementVersion() {
    echo 'increasing the app version...'
    sh 'mvn build-helper:parse-version versions:set \
        -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
        versions:commit'
    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
    def version = matcher[0][1]
    env.IMAGE_VERSION = "$version-$BUILD_NUMBER"
}

def buildImage() {
    echo "building docker image..."
    withCredentials([usernamePassword(credentialsId: 'docker-hub', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        sh "docker build -t ${IMAGE_NAME} ."
        sh "echo $PASSWORD | docker login -u $USERNAME --password-stdin"
        sh "docker push ${IMAGE_NAME}"
    }
}

def deployAppOnEC2() {
   def shellCmd="bash ./shell-cmd.sh ${IMAGE_NAME}"
    // def dockercmd = "docker run -p 8080:8080 -d ${IMAGE_NAME}"
    sshagent(['appServerCredential']) {
        sh "scp -o StrictHostKeyChecking=no shell-cmd.sh ec2-user@${EC2_PUBLIC_IP}:/home/ec2-user"
        sh "scp -o StrictHostKeyChecking=no docker-compose.yaml ec2-user@${EC2_PUBLIC_IP}:/home/ec2-user"
        sh "ssh -o StrictHostKeyChecking=no ec2-user@${EC2_PUBLIC_IP} ${shellCmd}"
        // sh "ssh -o StrictHostKeyChecking=no ec2-user@${EC2_PUBLIC_IP} ${dockercmd}"
    }
}


def commitVersion() {
     withCredentials([usernamePassword(credentialsId: 'GitHub', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        sh 'git config --global user.email "jenkins@example.com"'
        sh 'git config --global user.name "jenkins"'

        sh "git remote set-url origin https://${USERNAME}:${PASSWORD}@github.com/olu-salem/Jenkins-Project.git"
        sh 'git add .'
        sh 'git commit -m "ci: version bump"'
        sh 'git push origin HEAD:main'
     }
}


return this
