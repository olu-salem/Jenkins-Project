#!/usr/bin/env groovy

def gv

pipeline {
    agent any
    tools {
        maven 'Maven'
    }
    environment {
        IMAGE_NAME = "oso007/maven-app:${IMAGE_VERSION}"
        EC2_PUBLIC_IP = "ec2-3-218-141-63.compute-1.amazonaws.com"
    }
    stages {
        stage("init") {
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }

        stage("increament version") {
            steps {
                script {
                    gv.incrementVersion()
                }
            }
        }

        stage('build jar') {
            steps {
               script {
                  echo 'building application jar...'
                  gv.buildjar()
               }
            }
        }
        stage('build image') {
            steps {
                script {
                   gv.buildImage()
                }
            }
        }
        stage('deploy') {
            steps {
                script {
                  gv.deployAppOnEC2()
                }
            }
        }
    }
}
