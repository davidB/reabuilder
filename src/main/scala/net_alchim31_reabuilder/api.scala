package net_alchim31_reabuilder
package object api {

  import java.io.File

  type FilePathFilter = (FilePath) => Boolean

  trait BuildersChainFactory {
    def newBuilderChain(label : String, monitor : RunMonitor) : Option[BuildersChain]
  }

  trait BuildersChain {
    def label : String
    def srcDirs : List[(FilePath, FilePathFilter)]
    def chain : List[Builder]
  }

  trait Builder {
    def label : String
    def pathAcceptor : FilePathFilter
    def build(fsChanges : FileSystemChanges, monitor : RunMonitor) : FileSystemChanges
  }

  /**
   * In path '/' is used as separator for every OS (Windows, Linux, MacOS)
   */
  trait FilePath {
    def root : Option[FilePath]
    def rpath : String
    def fullpath : String = toFile.getCanonicalPath.replace('\\', '/')

    /** @codeAsDoc */
    def toFile : File = root.map(p => new File(p.toFile, rpath)).getOrElse(new File(rpath))
  }

  trait FileSystemChanges {
    def modifiedOrCreated : Seq[FilePath]
    def deleted :  Seq[FilePath]
    def renamed : Seq[(FilePath,FilePath)]

    def isEmpty = modifiedOrCreated.isEmpty && deleted.isEmpty && renamed.isEmpty
  }

  /**
   * RunMonitor is a facade service used to monitor the build by providing :
   * * progression indicator callback
   * * logger
   * * errorHanlder (TBD)
   *
   * @see http://help.eclipse.org/helios/topic/org.eclipse.platform.doc.isv/reference/api/org/eclipse/core/runtime/IProgressMonitor.html
   */
  trait RunMonitor {
    def runId : String
    def taskName : String

    // logging
    def info(msg : String)
    def warn(t : Option[Throwable] = None, msg : String)
    def error(t : Option[Throwable] = None, msg : String)
    def hasError : Boolean

    // progression
    /**
     * Notifies that a given number of work unit of the main task has been completed. Note that this amount represents an installment, as opposed to a cumulative amount of work done to date.
     * @param work a non-negative number of work units just completed
     */
    def worked(work : Int = 1, total : Int = 1) : RunMonitor

    def begin(b : Builder) : RunMonitor
    def end(b : Builder) : RunMonitor
    def previousBuilder : List[Builder] = Nil
  }

}