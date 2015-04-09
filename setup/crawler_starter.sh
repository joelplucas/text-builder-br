#!/bin/bash

function log_msg() {
    date +"[%Y-%m-%d %T +0000] $1"
}

##point PATH to java7 (instead of the default java6)
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

## Populate teams
log_msg "1) Populate Team collection on MongoDB"
cd /home/ubuntu/sentiment-soccer/target/
java -jar teamsPopulator-jar-with-dependencies.jar
log_msg "Teams Populated"

## Run crawler on background mode
log_msg "2) Run crawler on background mode"
java -jar sentiment-soccer-jar-with-dependencies.jar
log_msg "Finished crawler"