package com.feiyu.digitrecognizer.recognizer

import java.awt.image.{BufferedImage, Raster}
import java.io.File
import javax.imageio.ImageIO

import org.apache.spark.mllib.classification.LogisticRegressionModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by feiyu on 5/13/16.
  */
object Recognizer {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("DigitRecognizerJar")
      .setMaster("local")
    val sc = new SparkContext(conf)
    val image: BufferedImage = ImageIO.read(new File("src/main/resources/data/2.png"))
    val raster: Raster  = image.getRaster()
    val height = raster.getHeight
    val width = raster.getWidth
    println("Height: " + height, " Width: " + width)
    val features : Array[Double] = new Array[Double](height*width)
    for (j <- 0 to (height-1) ) {
      for(i <- 0 to (width-1)) {
        val curPixel = raster.getSample(i,j,0)
        print(Integer.toString(curPixel)+",")
        features(j*height + i) = curPixel.toDouble
      }
      println()
    }

    val lrLBFGSModelPath = "src/main/resources/model/lrLBFGS"
    val lrLBFGSModel = LogisticRegressionModel.load(sc, lrLBFGSModelPath)
    val testing = Vectors.dense(features)
    //println(testing.size)
    val predictions = lrLBFGSModel.predict(testing)
    println("The digit you wrote was: " + predictions.toInt)

    sc.stop()
  }
}
