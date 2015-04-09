#!/bin/bash

function log_msg() {
    date +"[%Y-%m-%d %T +0000] $1"
}

## Populate teams
log_msg "1) Populate Team collection on MongoDB"
java -jar teamsPopulator-jar-with-dependencies.jar
log_msg "Teams Populated"

## Run crawler on background mode
log_msg "2) Run crawler on background mode"
java -jar sentiment-soccer-jar-with-dependencies.jar
log_msg "Finished crawler"