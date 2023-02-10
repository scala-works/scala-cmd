package works.scala.cmd

import cats.effect.{ ExitCode, IO, IOApp }
import cats.syntax.all.*
import Errors.EarlyExitException

/** A trait for your multi-command CLI to extend.
  */
trait MultiCmdApp extends IOApp:

  /** A list of Cmd that can be triggered by name.
    *
    * @return
    */
  def cmdList: Seq[Cmd]

  /** The help string to print.
    */
  final private val helpString: String =
    val sb = new java.lang.StringBuilder()
    sb.append("Commands:" + System.lineSeparator())
    cmdList.sortBy(_.name).foreach { c =>
      val msg = s"""
                   | ${ c.name } : ${ c.description }
      """.stripMargin
      sb.append(msg)
    }
    sb.toString()

  /** Checks if the help flag was passed as the first argument, and prints the
    * helpString if so.
    */
  final private def checkHelp(args: List[String]): IO[Unit] = IO {
    HelpFlag.isPresent(args.take(1))
  }.ifM(
    ifTrue = IO.println(helpString) *> IO.raiseError(
      EarlyExitException(ExitCode.Success),
    ),
    ifFalse = IO.unit,
  )

  /** Checks if no arguments were passed, and prints the helpString if so.
    */
  final private def checkEmpty(args: List[String]): IO[Unit] = IO {
    args.isEmpty
  }.ifM(
    ifTrue = IO.println(helpString) *> IO.raiseError(
      EarlyExitException(ExitCode.Success),
    ),
    ifFalse = IO.unit,
  )

  /** Checks if the first argument passed is a valid name to trigger an item in
    * the cmdList. If not, will print the helpString
    */
  final private def checkUnrecognized(args: List[String]): IO[Unit] = IO {
    !cmdList.map(_.name).contains(args.head)
  }.ifM(
    ifTrue = IO.println("An unrecognized command was passed") *> IO.println(
      helpString,
    ) *> IO.raiseError(EarlyExitException(ExitCode.Error)),
    ifFalse = IO.unit,
  )

  final override def run(args: List[String]): IO[ExitCode] =
    (
      for
        _    <- checkEmpty(args)
        _    <- checkHelp(args)
        _    <- checkUnrecognized(args)
        exit <- IO.fromOption(cmdList.find(_.name == args.head))(
                  EarlyExitException(ExitCode.Error),
                ).flatMap(_.io(args.drop(1)))
      yield exit
    ).handleError {
      case exit: EarlyExitException => exit.code
      case _                        => ExitCode.Error
    }
