#!/bin/bash

# Installs all dependencies from remote
reload-remote-deps () {
  mvn dependecy:purge-local-repository
}

build-and-test () {
  mvn clean package
}

# Only uses remote snapshots
build-and-test-full () {
  mvn clean package -U
}

build-and-skip-test () {
  mvn clean package -DskipTests
}

build-and-test