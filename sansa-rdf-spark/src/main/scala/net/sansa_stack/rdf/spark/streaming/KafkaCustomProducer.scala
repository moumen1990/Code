package net.sansa_stack.rdf.spark.streaming

import java.util.{ Date, Properties }
import scala.util.Random
import org.apache.kafka.clients.producer.{ KafkaProducer, ProducerRecord }
import java.util.{ Date, Properties }
import scala.io.Source
import scala.collection.parallel.ParIterableLike

/**
 * @param: prop: Kafka properties
 * @param topic: Name of the Topic to send data to in Kafka
 * @param filePath: path to the file to be read (Local or HDFS)
 * @param sleepInterval: number of milliseconds between sending each message
 */
class KafkaCustomProducer(props: Properties, topic: String, filePath: String, isLocal: Boolean, sleepInterval: Int) extends Runnable {

  val producer = new KafkaProducer[String, String](props)

  /**
   * Checks the storage location of the file (Local or HDFS)
   * Send each line in the file as a Message to the given kafka topic
   */
  def run() {
    
    var rowCount= 0;
    
    // check if the filepath is local or on HDFS
    if (!isLocal) {
      val lines = HDFS.readFromFile(filePath)

      for (line <- lines) {
          val msg = new ProducerRecord[String, String](topic, rowCount.toString(), line)
        producer.send(msg)
        

        // To provide a lag between each message if wanted
        Thread.sleep(sleepInterval)
      }

    } else {

      // Read from local file
      val bufferedSource = Source.fromFile(filePath)

      // loop on each line and send message to the given kafka topic
      for (line <- bufferedSource.getLines) {
        val msg = new ProducerRecord[String, String](topic, rowCount.toString(), line)
        producer.send(msg)
        

        // To provide a lag between each message if wanted
        Thread.sleep(sleepInterval)
      }
      // close the buffer Source
      bufferedSource.close
    }
    // close the producer
    producer.close()
  }

}