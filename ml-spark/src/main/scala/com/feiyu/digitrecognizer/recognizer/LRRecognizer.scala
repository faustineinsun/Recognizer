package com.feiyu.digitrecognizer.recognizer

import com.feiyu.digitrecognizer.RecognizerLogisticRegressionModel
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by feiyu on 5/9/16.
  */
object LRRecognizer{

  def main(args: Array[String]) {
    val conf = new SparkConf()
      .setAppName("DigitRecognizerLogisticRegression")
      .setMaster("local")
    val sc = new SparkContext(conf)

    val lrLBFGS = new RecognizerLogisticRegressionModel(sc)
    val trainFilePath = "src/main/resources/data/train.csv"
    val testFilePath = "src/main/resources/data/test.csv"
    val resultFilePath = "src/main/resources/data/lroutput";

    val lrLBFGSModelPath = "src/main/resources/model/lrLBFGS"
    lrLBFGS.getClassificationModelLBFGS(trainFilePath, testFilePath, resultFilePath, lrLBFGSModelPath, true, 0.8, 0.2)

    //lrLBFGSModel.modelSelectionViaTrainValidationSplit(trainFilePath, 0.8, 0.2)

    sc.stop()
  }
}
