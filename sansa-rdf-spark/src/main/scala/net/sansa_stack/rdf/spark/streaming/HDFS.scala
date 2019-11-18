package net.sansa_stack.rdf.spark.streaming

import java.io.{ BufferedInputStream, OutputStreamWriter }

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{ FileSystem, Path }
import org.slf4j.{ Logger, LoggerFactory }

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.collection.immutable.List
import scala.collection.Iterator
import scala.collection.mutable.ArrayBuffer
import org.apache.kafka.clients.consumer.ConsumerRecord
import scala.collection.JavaConversions._
import org.apache.hadoop.fs.FSDataOutputStream
import java.io.OutputStream

object HDFS {

  def log: Logger = LoggerFactory.getLogger(HDFS.getClass)

  val hadoop: FileSystem = {
    val conf = new Configuration()
    conf.set("fs.defaultFS", "hdfs://localhost:54310")
    FileSystem.get(conf)
  }

  /**
   * @author Moumen Elteir
   * @param: path: path to hadoop directory(Path should contain "hdfs")
   * @return Returns ArrayBuffer[String] containing all the lines in the file;
   * Returns Null if file doesn't exist
   *
   *
   *
   */
  def readFromFile(path: String): ArrayBuffer[String] = {
    // create an Array buffer to store the lines read
    var result = new ArrayBuffer[String]();

    // check if file exists or not
    if (hadoop.exists(new Path(path))) {
      // create an input stream to read from hadoop
      val is = new BufferedInputStream(hadoop.open(new Path(path)))
      val lines = Source.fromInputStream(is).getLines()
      // add read lines to the array buffer
      lines.foreach(line => result.+=(line))
    } else {
      // TODO - error logic here
      result = null
      log.error("Thrown from HDFS Object: File doesn't Exist in Hadoop")
    }

    return result
  }

  def writeToFile(filename: String, content: Array[Pair[Long, String]]) = {
    val path = new Path(filename)
    
    if (hadoop.exists(path)) {
      val out = new OutputStreamWriter(hadoop.append(path))
      content.foreach(record => out.write(record._1 + " , " + record._2 + "\n"))
      out.flush()
      out.close()
    } else {
      val out = new OutputStreamWriter(hadoop.create(path, false))
      content.foreach(record => out.write(record._1 + " , " + record._2 + "\n"))
      out.flush()
      out.close()
    }
  }

  def ls(path: String, recursive:Boolean): List[String] = {
    val filePath= new Path(path)
    println("File Path ="+ filePath)
    
    val fileContents= ListBuffer[String]()
    
    // get List of folders
    val folders= hadoop.listStatus(filePath)
    folders.foreach(folder => fileContents+= folder.getPath.toString())
    
    // get list of files
    val files = hadoop.listFiles(filePath, recursive)
    val filenames = ListBuffer[String]()
    while (files.hasNext) fileContents += files.next().getPath().toString()
    
    // convert to a list
    fileContents.toList
   
  }

  def rm(path: String, recursive: Boolean): Unit = {
    if (hadoop.exists(new Path(path))) {
      println("deleting file : " + path)
      hadoop.delete(new Path(path), recursive)
      
    } else {
      println("File/Directory" + path + " does not exist")
      log.warn("File/Directory" + path + " does not exist")
    }
  }

  def cat(path: String) = Source.fromInputStream(hadoop.open(new Path(path))).getLines().foreach(println)

}