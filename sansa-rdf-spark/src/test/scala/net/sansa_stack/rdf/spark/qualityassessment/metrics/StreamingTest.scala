package net.sansa_stack.rdf.spark.qualityassessment.metrics

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import net.sansa_stack.rdf.spark.io._
import net.sansa_stack.rdf.spark.qualityassessment._
import net.sansa_stack.rdf.spark.streaming.FileReader
import org.apache.jena.riot.Lang
import org.scalatest.FunSuite
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.hadoop.io.{ LongWritable, Text }
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.spark.streaming.kafka010._
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.kafka.clients.producer.{ KafkaProducer, ProducerRecord }
import org.apache.kafka.clients.consumer._
import java.util.{ Date, Properties }
import net.sansa_stack.rdf.spark.streaming._
import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._

import org.apache.jena.graph.Triple
import org.apache.jena.riot.{ Lang, RDFDataMgr }
import org.apache.spark.rdd.RDD
import java.io.ByteArrayInputStream
import jxl.write.DateTime
import org.joda.time.DateTime
import java.util.ArrayList
import java.util.List
import scala.collection.mutable.ListBuffer
import net.sansa_stack.rdf.spark.qualityassessment.metrics.completeness.PropertyCompleteness

class StreamingTest extends FunSuite with DataFrameSuiteBase {

  val WINDOW_DURATON = 2

  val conft = new SparkConf().setMaster("local[2]").setAppName("SANSA")
  val ssc = new StreamingContext(conft, Seconds(WINDOW_DURATON))
  val sparkContext1 = SparkContext.getOrCreate(new SparkConf())

  val path = getClass.getResource("/rdf.nt").getPath
  //val path = "/home/m/Desktop/data/persondata_en.nt"

  // create producer to read from hdfs file and send it to kafka
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("client.id", "use_a_separate_group_id_for_each_stream")
  props.put("acks", "all");
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  //props.put("auto.offset.reset", "latest")

  
 val t1 = System.nanoTime
  //val producer = new KafkaCustomProducer(props, "raw_weather", path, false, 0)
  //new Thread(producer).start()

  
  // listen to directstream from kafka
  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> "localhost:9092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "use_a_separate_group_id_for_each_stream",
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean))
  val topics = Array("raw_weather")

  val stream = KafkaUtils.createDirectStream[String, String](
    ssc,
    PreferConsistent,
    Subscribe[String, String](topics, kafkaParams))
  stream.start()

  //clear old files

  val oldFilesList = HDFS.ls("hdfs://localhost:54310//sansa2//", true)
  oldFilesList.foreach(fileName => HDFS.rm(fileName, true))

  //val tribles = stream.map(record => RDFDataMgr.createIteratorTriples(new ByteArrayInputStream(record.value().getBytes), Lang.NTRIPLES, null).next())
  //tribles.saveAsTextFiles("hdfs://localhost:54310//sansa2//st", "")


 val duration = (System.nanoTime - t1) 


 println("Time Duration =" +duration)
 



  val list = HDFS.ls("hdfs://localhost:54310//sansa2//", true)
  println("ListSize = " + list.size)
  list.foreach(fileName => println(fileName))

  var bufferList = ListBuffer[String]()

  var first: Boolean = false
  list.foreach(fileName =>
    if (fileName.contains("part-0")) {
      if (fileName.contains("st-")) {
        val fileTimeStamp = fileName.split("st-").apply(1).split("/").apply(0)
        val timestamp = fileTimeStamp.toLong

        val lines = HDFS.readFromFile(fileName)
        if (lines.size > 0) {

          bufferList = bufferList ++ lines

        }
      }
    })

  println("Number of Elements =" + bufferList.size);
  val bufferListRDD=  sparkContext1.parallelize(bufferList, 2)
  val tripleRDD= bufferListRDD.map(record =>
    RDFDataMgr.createIteratorTriples(new ByteArrayInputStream(record.getBytes), Lang.NTRIPLES, null).next())

  
  val x= PropertyCompleteness.assessPropertyCompleteness(tripleRDD)
  
  stream.print()


  ssc.start() // Start the computation

  ssc.awaitTermination() // Wait for the computation to terminate

}