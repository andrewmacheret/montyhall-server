# montyhall-server

[![Build Status](https://travis-ci.org/andrewmacheret/montyhall-server.svg?branch=master)](https://travis-ci.org/andrewmacheret/montyhall-server) [![Docker Stars](https://img.shields.io/docker/stars/andrewmacheret/montyhall-server.svg)](https://hub.docker.com/r/andrewmacheret/montyhall-server/) [![Docker Pulls](https://img.shields.io/docker/pulls/andrewmacheret/montyhall-server.svg)](https://hub.docker.com/r/andrewmacheret/montyhall-server/) [![License](https://img.shields.io/badge/license-MIT-lightgray.svg)](https://github.com/andrewmacheret/montyhall-server/blob/master/LICENSE.md)

A java springboot microservice that serves an api for a [monty hall](https://en.wikipedia.org/wiki/Monty_Hall_problem) game simulator and statistics engine.

Meant to be consumed by [montyhall-client](https://github.com/andrewmacheret/montyhall-client).

See it running at [https://montyhall.andrewmacheret.com](https://montyhall.andrewmacheret.com).

## Docker usage:

Prereqs:

* [Docker](https://www.docker.com/products/docker)

Usage:

```bash
# build it
mvn clean package docker:build

# run it
docker run -d \
  --name montyhall-server \
  -p 80:80 \
  andrewmacheret/montyhall-server
```

## Manual usage:

Prereqs:

* A [JDK](http://openjdk.java.net/projects/jdk8) - tested with Oracle JDK 8 and OpenJDK 8

Usage:

```bash
# run it
mvn spring-boot:run
```

## Test it:

* `curl 'http://localhost'` - you should get back something like:

  ```json
  {"version":"1.1.2","apis":["/game","/stats"]}
  ```

