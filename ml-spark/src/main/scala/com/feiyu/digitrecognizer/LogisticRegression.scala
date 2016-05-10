package com.feiyu.digitrecognizer

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.{LogisticRegressionWithLBFGS, LogisticRegressionModel}
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint

/**
  * Created by feiyu on 5/9/16.
  */
object LogisticRegression {

  def main(args: Array[String]) = {
    val conf = new SparkConf()
      .setAppName("DigitRecognizerLogisticRegression")
      .setMaster("local")
    val sc = new SparkContext(conf)

    // Load training data to LabeledPoint format.
    val rawData = sc.textFile("src/main/resources/train/train.csv")
    val labeledPoint = rawData.mapPartitionsWithIndex((i, iterator) => {
      if (i == 0 && iterator.hasNext) {
        iterator.next
      }
      iterator
    }).map(line =>{
      val pixels = line.split(',')
      val label = pixels(0)
      val features:Array[Double] = new Array[Double](pixels.length-1)
      for ( i <- 0 to (features.length - 1)) {
        features(i) = pixels(i+1).toDouble
      }
      LabeledPoint(label.toDouble, Vectors.dense(features))
    }).cache()
    val count = labeledPoint.count()
    println(count)

    // Split data into training (80%) and test (20%).
    val splits = labeledPoint.randomSplit(Array(0.8, 0.2), seed = 11L)
    val training = splits(0).cache()
    val test = splits(1)

    // Run training algorithm to build the model
    val model = new LogisticRegressionWithLBFGS()
      .setNumClasses(10)
      .run(training)

    // Compute raw scores on the test set.
    val predictionAndLabels = test.map { case LabeledPoint(label, features) =>
      val prediction = model.predict(features)
      (prediction, label)
    }

    // Get evaluation metrics.
    val metrics = new MulticlassMetrics(predictionAndLabels)
    val precision = metrics.precision
    println("Precision = " + precision)

    // Save and load model
    model.save(sc, "src/main/resources/model/lr")
    val sameModel = LogisticRegressionModel.load(sc, "src/main/resources/model/lr")
  }
}
