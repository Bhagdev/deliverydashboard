#!/bin/bash

ELASTICSEARCH_HOST=172.30.174.24
ELASTICSEARCH_PORT=9300
MONGODB_URI=mongodb://172.30.46.38:27017/deliverydashboard
export ELASTICSEARCH_HOST
export ELASTICSEARCH_PORT
export MONGODB_URI

echo $MONGODB_URI

java -jar target/ddapi-0.0.1-SNAPSHOT.jar > sys.log &

