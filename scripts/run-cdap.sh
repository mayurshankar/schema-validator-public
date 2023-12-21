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

# Run CDAP CLI
# Make sure to run sandbox beforehand
cdap-cli () {
  cd /Users/mayur.shankar/Documents/cdap-sandbox-6.9.2/ && ./bin/cdap cli
}

if [ "$1" = "sandbox" ]; then
  echo "Running CDAP sandbox"
  cdap-sandbox

elif [ "$1" = "docker" ]; then
  echo "Running Docker"
  docker-container

else
  echo "Invalid option"
  echo "Usage: ./run-cdap [sandbox] [docker]"
fi