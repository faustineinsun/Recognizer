package com.feiyu.digitrecognizer

import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.classification.ClassificationModel
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame
import java.io.FileWriter

/**
  * Created by feiyu on 5/10/16.
  */
class RecognizerModel(sparkContext: SparkContext) {
  var sc: SparkContext = sparkContext
  val sqlContext = new org.apache.spark.sql.SQLContext(sc)

  /**
    * Load training data to LabeledPoint format
    */
  protected def loadTrainingDataToLabeledPoint(trainFilePath: String): RDD[LabeledPoint] = {
    //val trainFilePath = "src/main/resources/data/train.csv"
    val rawData = sc.textFile(trainFilePath)
    val labeledPoint = rawData.mapPartitionsWithIndex((i, iterator) => {
      if (i == 0 && iterator.hasNext) {
        iterator.next
      }
      iterator
    }).map(line =>{
      val pixels = line.split(',')
      val label = pixels(0)
      val features:Array[Double] = new Array[Double](pixels.length-1)
      for (i <- 0 to (features.length - 1)) {
        features(i) = pixels(i+1).toDouble
      }
      LabeledPoint(label.toDouble, Vectors.dense(features))
    }).cache()
    val count = labeledPoint.count()
    println("num of lines in train: "+count)

    return labeledPoint
  }

  /**
    * Load training data to DataFrame format
    */
  protected def loadTrainingDataToDataFrame(trainFilePath: String): DataFrame = {
    //val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    val train = sqlContext.createDataFrame(loadTrainingDataToLabeledPoint(trainFilePath))//.toDF("label", "features")
    println(train.show())
    return train
  }

  /**
    * Load testing data to Vector
    */
  protected def loadTestingDataToVector(testFilePath: String): RDD[org.apache.spark.mllib.linalg.Vector]=  {
    //val testFilePath = "src/main/resources/data/test.csv"
    val rawData = sc.textFile(testFilePath)
    val test = rawData.mapPartitionsWithIndex((i, iterator) => {
      if (i == 0 && iterator.hasNext) {
        iterator.next
      }
      iterator
    }).map(line =>{
      Vectors.dense(line.split(',').map(_.toDouble))
    }).cache()
    val count = test.count()
    println("num of lines in test: "+count)

    return test
  }

  /**
    * calculate Precision, Recall, F-Measure, and etc.
    */
  protected def doEvaluation(test: RDD[LabeledPoint], model: ClassificationModel) {
    // Compute raw scores on the test set.
    val predictionAndLabels = test.map { case LabeledPoint(label, features) =>
      val prediction = model.predict(features)
      (prediction, label)
    }

    // Get evaluation metrics.
    val metrics = new MulticlassMetrics(predictionAndLabels)
    println("Precision = " + metrics.precision)
    println("Recall = " + metrics.recall)
    println("F-Measure = " + metrics.fMeasure)
  }

  /**
    * Save predicted labels of test digits to file
    */
  protected def saveResultsToFile(predictions: RDD[Double], resultFilePath: String) {
    //val resultFilePath = "src/main/resources/data/lroutput";
    val submissionOutput = new FileWriter(resultFilePath, true)
    submissionOutput.write("ImageId,Label\n") // head line

    val predAry = predictions
      .coalesce(1,true)
      .collect()

    var i = 1
    for (line <- predAry) {
      submissionOutput.write(i.toString+","+line.toInt+"\n")
      i += 1
    }
    System.out.println("i->len: "+i+","+predAry.size)
    submissionOutput.close()

    /*
    predictions
      .map{label => label.toInt}
      .coalesce(1,true)
      .saveAsTextFile("src/main/resources/data/lroutput")
    println("predictions count: "+predictions.count())
    val t = sqlContext.createDataFrame(
    )
    println(t.show())
      */
  }
}
