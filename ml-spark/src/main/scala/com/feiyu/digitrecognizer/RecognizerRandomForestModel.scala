package com.feiyu.digitrecognizer

import org.apache.spark.SparkContext
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer}
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder, TrainValidationSplit}
import org.apache.spark.sql.DataFrame

/**
  * Created by feiyu on 5/10/16.
  */
class RecognizerRandomForestModel(sparkContext: SparkContext) extends RecognizerModel(sparkContext) {

  def createPipeline(train: DataFrame): (Pipeline, Array[ParamMap]) = {
    val labelIndexer = new StringIndexer()
      .setInputCol("label")
      .setOutputCol("indexedLabel")
      .fit(train)

    val rfClassifier = new RandomForestClassifier()
      .setLabelCol("indexedLabel")
      .setFeaturesCol("features")

    val predictionLabelConvertor = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("predictedLabel")
      .setLabels(labelIndexer.labels)

    val pipeline = new Pipeline()
      .setStages(Array(labelIndexer, rfClassifier, predictionLabelConvertor))

    val gridParam = new ParamGridBuilder()
      .addGrid(rfClassifier.numTrees, Array(100, 200))
      .addGrid(rfClassifier.maxDepth, Array(10, 20, 30))
      .addGrid(rfClassifier.minInstancesPerNode, Array(5, 10))
      .build()

    return (pipeline, gridParam)
  }

  def modelSelectionViaTrainValidationSplit(trainFilePath: String, splitTrain: Double, splitTest: Double) {
    val train = loadTrainingDataToDataFrame(trainFilePath)
    val (pipeline, gridParam) = createPipeline(train)
    val Array(training, test) = train.randomSplit(Array(splitTrain, splitTest), seed = 123)

    /*
    get trainValidationSplit
    80% of the data will be used for training and the remaining 20% for validation.
    */
    val trainValidationSplit = new TrainValidationSplit()
      .setEstimator(pipeline)
      .setEvaluator(new MulticlassClassificationEvaluator)
      .setEstimatorParamMaps(gridParam)
      .setTrainRatio(0.8)

    // choose the best set of parameters and get the best model
    val model = trainValidationSplit.fit(training)

    // make prediction
    model.transform(test)
      .select("features", "label", "prediction")
      .show()
  }

  def modelSelectionViaCrossValidation(trainFilePath: String, splitTrain: Double, splitTest: Double) {
    val train = loadTrainingDataToDataFrame(trainFilePath)
    val (pipeline, gridParam) = createPipeline(train)
    val Array(training, test) = train.randomSplit(Array(splitTrain, splitTest), seed = 123)

    /*
    model selection via Cross Validation
     */
    val cv = new CrossValidator()
      .setEstimator(pipeline)
      .setEvaluator(new MulticlassClassificationEvaluator)
      .setEstimatorParamMaps(gridParam)
      .setNumFolds(10)

    val model = cv.fit(training)

    model.save("src/main/resources/model/rf")
  }
}
