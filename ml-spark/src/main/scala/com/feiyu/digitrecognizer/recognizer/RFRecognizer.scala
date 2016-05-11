package com.feiyu.digitrecognizer.recognizer

import com.feiyu.digitrecognizer.RecognizerRandomForestModel
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by feiyu on 5/10/16.
  */
object RFRecognizer {
  def main(args: Array[String]) {
    val conf = new SparkConf()
      .setAppName("DigitRecognizerRandomForest")
      .setMaster("local")
    val sc = new SparkContext(conf)

    val rfRecognizer= new RecognizerRandomForestModel(sc)
    val trainFilePath = "src/main/resources/data/train.csv"

    //rfRecognizer.modelSelectionViaTrainValidationSplit(trainFilePath, 0.8, 0.2)
    rfRecognizer.modelSelectionViaCrossValidation(trainFilePath, 0.8, 0.2)

    sc.stop()
  }
}
