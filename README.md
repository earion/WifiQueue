# WifiQueue [![Build Status](https://travis-ci.org/earion/WifiQueue.svg?branch=master)](https://travis-ci.org/earion/WifiQueue) [![codecov](https://codecov.io/gh/earion/WifiQueue/branch/master/graph/badge.svg)](https://codecov.io/gh/earion/WifiQueue)

Project developed in Orange Polska S.A.
Purpose of the project is management of wifi queing mechanism and ONT provisioning on NOKIA 7330 DSLAM (GPON card)

## How to run App:

    mkdir images
    cd images
    wget https://raw.githubusercontent.com/earion/WifiQueue/master/docker/Dockerfile
    docker build --no-cache -t hostsqueue .
    docker run -p 8080:8080 -h new_hostname --restart unless-stopped -d hostsqueue


Go to: http://localhost:8080/HostsQueue/state/ - this is it !





Requirements to run:

docker 
