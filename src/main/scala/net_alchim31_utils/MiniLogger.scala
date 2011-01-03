package net_alchim31_utils

import java.io.PrintWriter
import java.io.StringWriter
import java.util.logging.LogRecord
import java.util.logging.Formatter
import java.util.logging.ConsoleHandler
import java.util.logging.LogManager
import java.util.logging.Level
import java.util.logging.Logger

class MiniLogger(val name : String) {
  val logger = {
    val l = Logger.getLogger(name)
    l.setLevel(Level.CONFIG)
    l.setUseParentHandlers(false)
    l.getHandlers().foreach(x => l.removeHandler(x))
    val handler = new ConsoleHandler()
    handler.setFormatter(new LogFormatter())
    l.addHandler(handler)
    l
  }

  def setLevel(v : String) = v.toUpperCase match {
    case "quiet" => quiet()
    case "normal" | "default" => normal()
    case "verbose" => verbose()
    case "debug" => debug()
    case x => logger.setLevel(Level.parse(x))
  }

  def quiet() = logger.setLevel(Level.WARNING)
  def verbose() = logger.setLevel(Level.FINE)
  def normal() = logger.setLevel(Level.CONFIG)
  def debug() = logger.setLevel(Level.FINEST)

  def trace(m : String, args : Any*) : Unit = trace(None, m, args)
  def trace(t : Option[Throwable], m : String, args : Any*) : Unit = log(Level.FINER, None, m, args)

  def debug(m : String, args : Any*) : Unit = debug(None, m, args)
  def debug(t : Option[Throwable], m : String, args : Any*) : Unit  = log(Level.FINE, t, m, args)

  def info(m : String, args : Any*) : Unit = info(None, m, args)
  def info(t : Option[Throwable], m : String, args : Any*) : Unit  = log(Level.INFO, t, m, args)

  def warn(m : String, args : Any*) : Unit = warn(None, m, args)
  def warn(t : Option[Throwable], m : String, args : Any*) : Unit  = log(Level.WARNING, t, m, args)

  def error(m : String, args : Any*) : Unit = error(None, m, args)
  def error(t : Option[Throwable], m : String, args : Any*) : Unit  = log(Level.SEVERE, t, m, args)

  def log(level : Level, t : Option[Throwable], msg : String, params : Seq[Any]) {
    val levelValue = logger.getLevel.intValue
    if (level.intValue < levelValue || levelValue == Level.OFF.intValue) {
        return
    }
    val lr = new LogRecord(level, msg)
    lr.setLoggerName(logger.getName)
    if (params.length > 0) {
      lr.setParameters(params.map(_.asInstanceOf[AnyRef]).toArray)
    }
    t.foreach(lr.setThrown)
    logger.log(lr)
  }

}

class LogFormatter extends Formatter {

  val LINE_SEPARATOR = System.getProperty("line.separator");
  val t0 = System.currentTimeMillis

  override def format(record : LogRecord) = {
    val sb = new StringBuilder();

    val msg = if (record.getParameters() != null && record.getParameters().length > 0) {
      String.format(record.getMessage, record.getParameters : _*)
    } else {
      record.getMessage
    }

    sb.append(String.format("%10d : %6s : ", (record.getMillis() - t0).asInstanceOf[AnyRef],  record.getLevel().getLocalizedName())).
        append(msg).
        append(LINE_SEPARATOR)

    if (record.getThrown() != null) {
      try {
        val sw = new StringWriter()
        val pw = new PrintWriter(sw)
        record.getThrown().printStackTrace(pw)
        pw.close()
        sb.append(sw.toString())
      } catch {
        case _ => () //ignore
      }
    }

    sb.toString
  }
}