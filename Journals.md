Pull and run Jenkins container from dockerhub

```
docker run -p 8080:8080 -p 50000:50000 -d -v jenkins_home:/var/jenkins_home jenkins/jenkins:lts
```

- install node and npm inside the shell of the jenkins container
- install maven as plug-in in jenkins
- create a new job

create jenkins container with mounted docker

```
docker run -p 8080:8080 -p 50000:50000 -d -v jenkins_home:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock -v $(which docker):/usr/bin/docker jenkins/jenkins:lts
```

- give jenkins user in your docker container read and write permission of /var/run/docker.sock from the root user shell
  i.e chmod 666 /var/run/docker.sock
- configure the build pipeline to not trigger a build when the user "jenkins' push to git
- create a keypair in aws and save the private key as a ssh crendential on jenkins

### jenkins plug in used

- GitHub Integration Plugin
- Kubernetes CLI Plugin Version1.12.0
- Maven Integration plugin Version3.21
- SSH Agent
