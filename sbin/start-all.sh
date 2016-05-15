#! /bin/bash

${SPARK_HOME}sbin/stop-all.sh
${SPARK_HOME}sbin/start-all.sh

cd ${RECOGNIZER_PRJ_DIR}web-nodejs
npm install

# build runnable jar
cd ${RECOGNIZER_PRJ_DIR}ml-spark
mvn clean compile assembly:single

${MONGODB_HOME}bin/mongod

