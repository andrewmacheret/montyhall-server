# montyhall-server

[![Build Status](https://travis-ci.org/andrewmacheret/montyhall-server.svg?branch=master)](https://travis-ci.org/andrewmacheret/montyhall-server) [![Docker Stars](https://img.shields.io/docker/stars/andrewmacheret/montyhall-server.svg)](https://hub.docker.com/r/andrewmacheret/montyhall-server/) [![Docker Pulls](https://img.shields.io/docker/pulls/andrewmacheret/montyhall-server.svg)](https://hub.docker.com/r/andrewmacheret/montyhall-server/) [![License](https://img.shields.io/badge/license-MIT-lightgray.svg)](https://github.com/andrewmacheret/montyhall-server/blob/master/LICENSE.md)

A java springboot microservice that serves an api for a [monty hall](https://en.wikipedia.org/wiki/Monty_Hall_problem) game simulator and statistics engine.

Meant to be consumed by [montyhall-client](https://github.com/andrewmacheret/montyhall-client).

## Docker usage:

Prereqs:

* [Docker](https://www.docker.com/products/docker)

Usage:

```bash
# install node prereqs
npm install

# optional: build it from source
docker build -t andrewmacheret/remote-apis .

# run it
docker run -d \
  --name remote-apis \
  -p 80:80 \
  andrewmacheret/remote-apis
```

## Manual usage:

Prereqs:

* A [JDK](http://openjdk.java.net/projects/jdk8/) - tested with Oracle JDK 8 and OpenJDK 8

Usage:

```bash
# install node prereqs
npm install

# run it
node remote-apis.js
```

Modify `remoteApis` and `port` in [settings.js](settings.js) as needed.

## Test it:

* `node remote-apis.js`

* `curl 'http://localhost`

  * You should get back something like:

    ```json
    {
      "apis": [
          "/remote-apis/highmaps/worlds/custom/world",
          "/remote-apis/worldbank/indicators",
          "/remote-apis/worldbank/indicators/:_id",
          "/remote-apis/worldbank/countries"
      ]
    }
    ```
* `curl 'http://localhost/remote-apis/worldbank/countries`

  * You should get the same result as if you went to [http://api.worldbank.org/countries?format=json&per_page=32767](http://api.worldbank.org/countries?format=json&per_page=32767)

