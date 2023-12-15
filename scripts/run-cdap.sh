#!/bin/bash

pull_cdap () {
  docker pull caskdata/cdap-sandbox:6.9.2
}

# Run Docker container
docker-container () {
  docker run -it --name cdap-sb-latest-debug -p 11011:11011 -p 11015:11015 caskdata/cdap:latest cdap sandbox start --enable-debug --foreground
}

# Run CDAP sandbox standalone
cdap-sandbox () {
  cd /Users/mayur.shankar/Documents/cdap-sandbox-6.9.2/ && ./bin/cdap sandbox start --enable-debug --foreground
}

cdap-sandbox