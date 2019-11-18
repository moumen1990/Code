package net.sansa_stack.rdf.spark.streaming

import scala.io.Source
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.io.PrintWriter;

class HDFSFileStreamer(dataSetPath: String, HDFSLocation: String, outputFileName: String) extends Runnable {

  //val bufferedSource = Source.fromFile(dataSetPath)

  def run() {
    println("Trying to write to HDFS...")
    val conf = new Configuration()
    
     val bufferedSource = Source.fromFile(dataSetPath)
    conf.set("fs.defaultFS", "hdfs://localhost:54310")
    val fs = FileSystem.get(conf)
    val output = fs.create(new Path("/mysample.nt"))
    val writer = new PrintWriter(output)
    try {
      for (line <- bufferedSource.getLines){
        writer.write(line)
        Thread.sleep(1000)
      }
      
    } finally {
      writer.close()
      println("Closed!")
    }
    println("Done!")
  }

}