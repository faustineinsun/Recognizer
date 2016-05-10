package com.feiyu.digitrecognizer

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.LogisticRegressionModel

/**
  * Created by feiyu on 5/9/16.
  */
object LRRecognizer{

  def main(args: Array[String]) {
    val conf = new SparkConf()
      .setAppName("DigitRecognizerLogisticRegression")
      .setMaster("local")
    val sc = new SparkContext(conf)

    val trainFilePath = "src/main/resources/data/train.csv"
    val modelPath = "src/main/resources/model/lr"

    val lrModel = new RecognizerLogisticRegressionModel(sc)
    lrModel.modelSelectionViaTrainValidationSplit(trainFilePath, true, modelPath)

    val sameModel = LogisticRegressionModel.load(sc, modelPath)
  }
}
