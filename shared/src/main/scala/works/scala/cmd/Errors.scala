package works.scala.cmd

import cats.effect.ExitCode

private[cmd] object Errors:
  /** An error to allow us to cleanly exit our application early, with the given
    * code.
    */
  final private[cmd] case class EarlyExitException(code: ExitCode)
      extends Throwable
