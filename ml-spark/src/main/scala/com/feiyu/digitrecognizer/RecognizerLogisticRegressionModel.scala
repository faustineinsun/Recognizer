package com.feiyu.digitrecognizer

import org.apache.spark.SparkContext
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.ml.tuning.{ParamGridBuilder, TrainValidationSplit}
import org.apache.spark.ml.classification.LogisticRegression

/**
  * Created by feiyu on 5/9/16.
  */
class RecognizerLogisticRegressionModel(sparkContext: SparkContext) extends RecognizerModel(sparkContext){

  /**
    * Using Spark's LogisticRegressionWithLBFGS to train a classification model for Multinomial Logistic Regression
    * using Limited-memory BFGS.
    * Standard feature scaling and L2 regularization are used by default.
    * This is a 10 classes multi-label classification problem, labels from 0 to 9
    */
  def classificationModelLBFGS(trainFilePath: String, needEvaluation: Boolean, modelPath: String,
                               splitTrain: Double, splitTest: Double) {
    // get labeledPoint from raw data
    val labeledPoint = loadTrainingDataToLabeledPoint(trainFilePath)

    // get training and testing data
    var training = labeledPoint
    var test = labeledPoint
    if (needEvaluation) {
      val splits = labeledPoint.randomSplit(Array(splitTrain, splitTest), seed = 123)
      training = splits(0).cache()
      test = splits(1)
    }

    // build the model
    val model = new LogisticRegressionWithLBFGS()
      .setNumClasses(10) // 0 -> 9
      .run(training)

    // save model
    // val modelPath = "src/main/resources/model/lr"
    model.save(sc, modelPath)

    // do evaluation
    if (needEvaluation) {
      doEvaluation(test, model)
    }
  }

  /**
    * Currently, LogisticRegression with ElasticNet in ML package only supports binary classification.
    * but this is 10 classes classification problem
    * therefore, this method is deprecated,
    * will modify this later if MLlib's LogisticRegression with ElasticNet supports multiclass classification.
    */
  @deprecated
  def modelSelectionViaTrainValidationSplit(trainFilePath: String, splitTrain: Double, splitTest: Double) {
    val train = loadTrainingDataToDataFrame(trainFilePath)
    val Array(training, test) = train.randomSplit(Array(splitTrain, splitTest), seed = 123)

    val lr = new LogisticRegression()

    /*
    construct a grid of parameters using ParamGridBuilder
    TrainValidationSplit will try all combinations of values and determine best model using the evaluator.
     */
    val paramMap = new ParamGridBuilder()
      .addGrid(lr.regParam, Array(0.001, 1000.0))
      .addGrid(lr.maxIter, Array(10, 50, 100))
      //.addGrid(lr.elasticNetParam, Array(0.0, 0.5, 1.0))
      //.addGrid(lr.threshold, Array(0.55))
      //.addGrid(lr.fitIntercept)
      //.addGrid(lr.elasticNetParam, Array(0.0, 0.5, 1.0))
      .build()

    /*
    get trainValidationSplit
    80% of the data will be used for training and the remaining 20% for validation.
    */
    val trainValidationSplit = new TrainValidationSplit()
      .setEstimator(lr)
      .setEvaluator(new RegressionEvaluator)
      .setEstimatorParamMaps(paramMap)
      .setTrainRatio(0.8)

    // choose the best set of parameters and get the best model
    val model = trainValidationSplit.fit(training)

    // make prediction
    model.transform(test)
      .select("features", "label", "prediction")
      .show()
  }
}
