package samples

import org.junit.runner.RunWith
import org.specs._
import org.specs.matcher._
import org.specs.runner.{ JUnitSuiteRunner, JUnit }
import net_alchim31_reabuilder.impl0.{FileSystemChangesEmpty, BuildersChainRunner, RunMonitor0, FilePath0}

//import org.scalacheck.Gen

/**
 * Sample specification.
 * 
 * This specification can be executed with: scala -cp <your classpath=""> ${package}.SpecsTest
 * Or using maven: mvn test
 *
 * For more information on how to write or run specifications, please visit: http://code.google.com/p/specs.
 *
 */
@RunWith(classOf[JUnitSuiteRunner])
class Impl0SpecTest extends Specification with JUnit /*with ScalaCheck*/ {

  val chainFactory = newBuildersChainFactory4Test()

  "Empty ChainBuilders" should {
    "not throw errors" in {
      import net_alchim31_reabuilder.api.{BuildersChain, RunMonitor, BuildersChainFactory, Builder}
      
      val monitor = new RunMonitor0("t0")
      val chain = chainFactory.newBuilderChain("empty", monitor)
      val runner = new BuildersChainRunner(chain)
      //runner.start

      runner ! BuildersChainRunner.SrcChange(FileSystemChangesEmpty)
      //0
    }
    "deny " in {
      //0
    }
  }
  
  "A List" should {
    "have a size method returning the number of elements in the list" in {
      List(1, 2, 3).size must_== 3
    }
    // add more examples here
    // ...
  }

  def newBuildersChainFactory4Test() = {
    import net_alchim31_reabuilder.api.{BuildersChain, RunMonitor, BuildersChainFactory, Builder}
    import net_alchim31_reabuilder.helpers.FilePathImplicits._

    // if you use explicit call to create FilePath, FilePathFilter
    import net_alchim31_reabuilder.helpers.FilePathImplicits
    import net_alchim31_reabuilder.impl0.FilePath0

    new BuildersChainFactory() {

      implicit val basedir = new FilePath0("/tmp/fake/")

      def newBuilderChain(label : String, monitor : RunMonitor) : Option[BuildersChain] = label match {
        case "empty" => Some(new BuildersChain(){
          def label = "empty"
          def srcDirs = List(
            ("src", "main/x/**/*.x") // use FilePathImplicits._
            ,(new FilePath0("src", basedir), FilePathImplicits.glob("main/resources/**/*.z")) // use explicit
          )
          def chain : List[Builder] = Nil
        })
        case "empty" => Some(new BuildersChain(){
          def label = "empty"
          def srcDirs = Nil // useless as Trigger are explicitly push by test
          def chain : List[Builder] = Nil
        })
        case _ => None
      }
    }

  }
}

object Impl0SpecMain {
  def main(args: Array[String]) {
    new Impl0SpecTest().main(args)
  }
}
