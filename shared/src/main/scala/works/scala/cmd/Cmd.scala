package works.scala.cmd

import cats.effect.{ ExitCode, IO, IOApp }
import cats.syntax.all.*

/** A command to run as part of a CLI app.
  */
trait Cmd:

  /** The Flags to parse for this app.
    * @return
    */
  def flags: Seq[Flag[?]] = Seq.empty

  /** The Args to parse for this app.
    * @return
    */
  def args: Seq[Arg[?]] = Seq.empty

  /** The main logic of your app
    * @param args
    */
  def command(args: List[String]): Unit

  /** Helper method to print expected Flag/Args
    */
  val helpString: String =
    val sb     = new java.lang.StringBuilder()
    val _flags = HelpFlag +: flags
    if args.nonEmpty then
      sb.append("Args:" + System.lineSeparator())
      args.sortBy(_.name).foreach { a =>
        val msg = s"""
                     |  ${ a.name } : ${ a.description }
                     |""".stripMargin
        sb.append(msg + System.lineSeparator())
      }
    sb.append("Flags:" + System.lineSeparator())
    _flags
      .sortBy(_.name)
      .foreach { f =>
        val msg = s"""
                     |  ${ f._sk } ${ f._lk } : ${ f.description }
                     |""".stripMargin
        sb.append(msg + System.lineSeparator())
      }
    sb.toString()

/** A trait for your single-command CLI to extend.
  */
trait CmdApp extends Cmd with IOApp:
  final private case class EarlyExitException(code: ExitCode) extends Throwable

  private val builtIns: Seq[Flag[?]] = Seq(
    HelpFlag,
  )

  def checkHelp(args: List[String]): IO[Unit] = IO {
    HelpFlag.isPresent(args)
  }.ifM(
    ifTrue = IO.println(helpString) *> IO.raiseError(
      EarlyExitException(ExitCode.Success),
    ),
    ifFalse = IO.unit,
  )

  def checkUnrecognized(args: List[String]): IO[Unit] = IO {
    Flag.hasUnrecognizedFlag(args, builtIns ++ flags)
  }.ifM(
    ifTrue = IO.println("An unrecognized flag was passed") *> IO.println(
      helpString,
    ) *> IO.raiseError(EarlyExitException(ExitCode.Error)),
    ifFalse = IO.unit,
  )

  override def run(args: List[String]): IO[ExitCode] =
    (
      for
        _ <- checkHelp(args)
        _ <- checkUnrecognized(args)
        _ <- IO {
               command(args)
             }
      yield ExitCode.Success
    ).handleError {
      case exit: EarlyExitException => exit.code
      case _                        => ExitCode.Error
    }
