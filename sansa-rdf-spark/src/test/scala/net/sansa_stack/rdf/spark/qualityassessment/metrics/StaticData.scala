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
import org.apache.spark.sql.SparkSession


class StaticData  extends FunSuite with DataFrameSuiteBase  {
  
  
 
  val path="/home/m/Desktop/data/persondata_en.nt"
    val lang: Lang = Lang.NTRIPLES
    
    val t1 = System.nanoTime
    
    val triples = spark.rdf(lang)(path)
    
    
    val value = triples.assessInterlinkingCompleteness()
    val duration = (System.nanoTime - t1) 
    println("Duration")
   
}