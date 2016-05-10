package com.feiyu.digitrecognizer

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

/**
  * Created by feiyu on 5/9/16.
  */
object LRRecognizer{

  def main(args: Array[String]) {
    val conf = new SparkConf()
      .setAppName("DigitRecognizerLogisticRegression")
      .setMaster("local")
    val sc = new SparkContext(conf)

    val lrLBFGSModel = new RecognizerLogisticRegressionModel(sc)
    val trainFilePath = "src/main/resources/data/train.csv"

    val lrLBFGSModelPath = "src/main/resources/model/lr/lrLBFGS"
    lrLBFGSModel.classificationModelLBFGS(trainFilePath, true, lrLBFGSModelPath, 0.8, 0.2)

    //lrLBFGSModel.modelSelectionViaTrainValidationSplit(trainFilePath, 0.8, 0.2)

    //val sameModel = LogisticRegressionModel.load(sc, modelPath)
  }
}
