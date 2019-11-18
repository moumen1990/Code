package net.sansa_stack.rdf.spark.qualityassessment.metrics

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import net.sansa_stack.rdf.spark.io._
import net.sansa_stack.rdf.spark.qualityassessment._
import org.apache.jena.riot.Lang
import org.scalatest.FunSuite
import java.util.concurrent.TimeUnit

class CompletenessTests extends FunSuite with DataFrameSuiteBase {

  test("assessing the interlinking completeness should result in value 0") {

    val path = getClass.getResource("/rdf.nt").getPath
    //val path="/home/m/Desktop/data/persondata_en.nt"
    //val path="/home/m/Desktop/data/split1g"
    val lang: Lang = Lang.NTRIPLES
    
    val t1 = System.nanoTime
    
    val triples = spark.rdf(lang)(path)
    
    
    val value = triples.assessSchemaCompleteness()
    val duration = (System.nanoTime - t1) 
    val ms= TimeUnit.NANOSECONDS.toMillis(duration)
    assert(ms == 0)
  }


  

}
