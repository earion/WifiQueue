# WifiQueue [![Build Status](https://travis-ci.org/earion/WifiQueue.svg?branch=master)](https://travis-ci.org/earion/WifiQueue) [![codecov](https://codecov.io/gh/earion/WifiQueue/branch/master/graph/badge.svg)](https://codecov.io/gh/earion/WifiQueue)

Project developed in Orange Polska S.A.
Purpose of the project is management of wifi queing mechanism and ONT provisioning on NOKIA 7330 DSLAM (GPON card)

## How to run App:

    mkdir images
    cd images
    Download file https://github.com/earion/WifiQueue/blob/master/docker/Dockerfile
    docker build -t hostsqueue .
    docker run -p 4000:8080  hostsqueue

Go to: http://localhost:4000/HostsQueue/state/ - this is it !





Requirements to run
docker 
