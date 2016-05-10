package com.feiyu.digitrecognizer

import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS

/**
  * Created by feiyu on 5/9/16.
  */
class RecognizerLogisticRegressionModel(sparkContext: SparkContext) extends RecognizerModel(sparkContext){

  def modelSelectionViaCrossValidation(trainFilePath: String, doEvaluation: Boolean) {
    val data = loadTrainingData(trainFilePath)

  }

  def modelSelectionViaTrainValidationSplit(trainFilePath: String,
                                            needEvaluation: Boolean, modelPath: String) {
    val labeledPoint = loadTrainingData(trainFilePath)

    var training = labeledPoint
    var test = labeledPoint
    if (needEvaluation) {
      val splits = labeledPoint.randomSplit(Array(0.8, 0.2), seed = 123)
      training = splits(0).cache()
      test = splits(1)
    }

    // Run training algorithm to build the model
    val model = new LogisticRegressionWithLBFGS()
      .setNumClasses(10)
      .run(training)

    // Save and load model
    // val modelPath = "src/main/resources/model/lr"
    model.save(sc, modelPath)

    if (needEvaluation) {
      doEvaluation(test, model)
    }
  }
}
