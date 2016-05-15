#! /bin/bash

JAR_FILE=${RECOGNIZER_PRJ_DIR}ml-spark/target/ml-spark-0.0.1-SNAPSHOT-jar-with-dependencies.jar
MODEL_PATH=${RECOGNIZER_PRJ_DIR}ml-spark/src/main/resources/model/lrLBFGS
INPUT_FILE=${RECOGNIZER_PRJ_DIR}web-nodejs/public/img/out.png
OUTPUT_FILE=${RECOGNIZER_PRJ_DIR}web-nodejs/public/assets/prediction.json

# run jar file on Spark
${SPARK_HOME}bin/spark-submit $JAR_FILE $MODEL_PATH $INPUT_FILE $OUTPUT_FILE  


