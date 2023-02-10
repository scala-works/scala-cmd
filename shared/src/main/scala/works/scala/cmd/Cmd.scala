package works.scala.cmd

import cats.effect.{ ExitCode, IO, IOApp }
import cats.syntax.all.*
import Errors.EarlyExitException

/** A command to run as part of a CLI app.
  */
trait Cmd:

  /** The name of the command. Used to run if part of a MultiCmdApp
    */
  val name: String

  /** A short description of what this command does.
    */
  val description: String

  /** The Flags to parse for this app.
    * @return
    */
  def flags: Seq[Flag[?]] = Seq.empty

  /** A collection of built-in flags provided for pre-processing
    */
  private val builtIns: Seq[Flag[?]] = Seq(
    HelpFlag,
  )

  /** The Args to parse for this app.
    * @return
    */
  def args: Seq[Arg[?]] = Seq.empty

  /** The main logic of your app
    * @param args
    */
  def command(args: List[String]): Unit

  /** Helper to print expected Flag/Args
    */
  final private lazy val helpString: String =
    val sb     = new java.lang.StringBuilder()
    sb.append(s"Command: ${ name } : ${ description }" + System.lineSeparator())
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

  final private def checkHelp(args: List[String]): IO[Unit] = IO {
    HelpFlag.isPresent(args)
  }.ifM(
    ifTrue = IO.println(helpString) *> IO.raiseError(
      EarlyExitException(ExitCode.Success),
    ),
    ifFalse = IO.unit,
  )

  final private def checkUnrecognized(args: List[String]): IO[Unit] = IO {
    Flag.hasUnrecognizedFlag(args, builtIns ++ flags)
  }.ifM(
    ifTrue = IO.println("An unrecognized flag was passed") *> IO.println(
      helpString,
    ) *> IO.raiseError(EarlyExitException(ExitCode.Error)),
    ifFalse = IO.unit,
  )

  final def io(args: List[String]): IO[ExitCode] =
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
