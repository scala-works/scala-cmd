package works.scala.cmd

import cats.effect.{ ExitCode, IO, IOApp }
import cats.syntax.all.*

/** A trait for your single-command CLI to extend.
  */
trait CmdApp extends Cmd with IOApp:

  override val name: String = ""

  override val description: String = ""

  final override def run(args: List[String]): IO[ExitCode] =
    io(args)
