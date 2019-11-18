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
import net.sansa_stack.rdf.spark.streaming._

class HDFSOperations extends FunSuite with DataFrameSuiteBase{
  
   test("assessing that HDFS Object can read file from hadoop"){
     val path = "/test.txt"
     var list= HDFS.readFromFile(path)
     println("List Size is "+ list.size)
     assert(list.size != 0)
   }
}