package com.feiyu.digitrecognizer.recognizer

import java.awt.Image
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

    // resize original 280x280 image to 28x28
    val width = 28
    val height = 28
    val image: BufferedImage = ImageIO.read(new File("src/main/resources/data/out.png"))
    val scaleImage = image.getScaledInstance(width, height, Image.SCALE_DEFAULT)
    val bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    bufferedImage.getGraphics().drawImage(scaleImage, 0, 0 , null);
    val raster: Raster  = bufferedImage.getRaster()

    // save gray scale pixel into feature array
    val feature : Array[Double] = new Array[Double](height*width)
    for (j <- 0 to (height-1) ) {
      for(i <- 0 to (width-1)) {
        val curPixel = raster.getSample(i,j,0)
        print(Integer.toString(curPixel)+",")
        feature(j*height + i) = curPixel.toDouble
      }
      println()
    }

    // generate testing data's feature
    val testing = Vectors.dense(feature)

    // load trained model and do prediction
    val lrLBFGSModelPath = "src/main/resources/model/lrLBFGS"
    val lrLBFGSModel = LogisticRegressionModel.load(sc, lrLBFGSModelPath)
    val predictions = lrLBFGSModel.predict(testing)
    println("The digit you wrote was: " + predictions.toInt)

    sc.stop()
  }
}
