package net_alchim31_reabuilder
package cli

import net_alchim31_reabuilder.impl0.RunMonitor0
import java.io.File
import net_alchim31_reabuilder.impl0.BuildersChainRunner
import net_alchim31_reabuilder.api.{BuildersChainFactory, BuildersChain}

/**
 * @author ${user.name}
 */
object Main{
  
  def main(args : Array[String]) {
    val chainAndLastBuilder = args(0)
    val (chainName, lastBuilderName) = chainAndLastBuilder.indexOf(":") match {
      case -1 => (chainAndLastBuilder, None : Option[String])
      case x => (chainAndLastBuilder.substring(0, x), Some(chainAndLastBuilder.substring(x +1)))
    }
    val chainFactoryDefFile : Option[File] = None
    for(
      f <- chainFactoryDefFile;
      chainFactory <- loadBuildersChainFactory(f);
      runners <- start(chainFactory, chainName, lastBuilderName)
    ) {
      // crappy wait for Ctrl+C
      Thread.currentThread.wait()
      //TODO make a clean stop (stopping watch, wainting end of current build run,...)
      stop(runners)
    }
  }

  case class Runners(runner : BuildersChainRunner , watcher : BuildersChainFSWatcher)

  def start(chainFactory : BuildersChainFactory, chainName : String, lastBuilderName : Option[String] = None) : Option[Runners] = {
    val monitor = new RunMonitor0("t0")
    chainFactory.newBuilderChain(chainName, monitor) match {
      case Some(bsc : BuildersChain) => start(bsc, lastBuilderName)
      case None => println("BuildersChain not found :" + chainName) ; None
    }
  }

  def start(bsc : BuildersChain, lastBuilderName : Option[String]) : Option[Runners] = {
    val runner = new BuildersChainRunner(bsc, lastBuilderName)
    //runner.start
    val watcher = new BuildersChainFSWatcher(runner)
    watcher.start()
    Some(Runners(runner, watcher))
  }

  def stop(runners : Runners) {
    try { runners.watcher.stop() } catch {
      case t => //ignore // TODO log
    }
    try { runners.runner.stop  } catch {
      case t => //ignore // TODO log
    }
  }
  def loadBuildersChainFactory(chainFactoryDefFile :File) : Option[BuildersChainFactory] = None

}

class BuildersChainFSWatcher(runner : BuildersChainRunner) {
  import net.contentobjects.jnotify.{JNotify, JNotifyListener}
  import net_alchim31_reabuilder.impl0.{FilePath0, FileSystemChanges0}
  import net_alchim31_reabuilder.api.{FilePath, FileSystemChanges}
  
  private val _watchMask = JNotify.FILE_ANY //JNotify.FILE_CREATED  | JNotify.FILE_DELETED  | JNotify.FILE_MODIFIED |  JNotify.FILE_RENAMED
  private val _watchSubTree = true

  private var _srcDirWids : List[Int] = Nil

  def start() {
    if (_srcDirWids.isEmpty) {
      val srcListener = new Listener(runner)
      _srcDirWids = runner.buildersChain.srcDirs.map { loc =>
        val path = loc._1.fullpath
        JNotify.addWatch(path, _watchMask, _watchSubTree, srcListener)
      }
    } else {
      println("already started") //TODO use logger
    }
  }
  
  def stop() {
    val wids = _srcDirWids
    _srcDirWids = Nil
    for (wid <- wids) {
      if (!JNotify.removeWatch(wid)) {
        // invalid watch ID specified.
      }
    }
  }
  
  class Listener(runner : BuildersChainRunner) extends JNotifyListener {
    def fileRenamed(wid : Int, rootPath : String,  oldName : String, newName : String) {
      print("renamed " + rootPath + " : " + oldName + " -> " + newName)
      send(new FileSystemChanges0(renamed=List((toFilePath(rootPath, oldName), toFilePath(rootPath, newName)))))
    }
    def fileModified(wid : Int, rootPath : String, name : String) {
      print("modified " + rootPath + " : " + name)
      send(new FileSystemChanges0(modifiedOrCreated = List(toFilePath(rootPath, name))))
    }
    def fileDeleted(wid : Int, rootPath : String, name : String) {
      print("deleted " + rootPath + " : " + name)
      send(new FileSystemChanges0(deleted = List(toFilePath(rootPath, name))))
    }
    def fileCreated(wid : Int, rootPath : String, name : String) {
      print("created " + rootPath + " : " + name)
      send(new FileSystemChanges0(modifiedOrCreated = List(toFilePath(rootPath, name))))
    }
    
    private def toFilePath(rootPath : String, name : String) : FilePath = {
      new FilePath0(name, Some(new FilePath0(rootPath)))
    }
    private def send(n : FileSystemChanges) {
      runner ! BuildersChainRunner.SrcChange(n)
    }
  }
}