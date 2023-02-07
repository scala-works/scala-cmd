package works.scala.cmd

import scala.scalajs.js


trait PlatformSpecificEntry:
  lazy val cmd: Cmd

  final def main(args: Array[String]): Unit =
    if HelpFlag.isPresent(args) then
      cmd.printHelp()
      js.Dynamic.global.process.exitCode = 0
      return ()

    if Flag.hasUnrecognizedFlag(args, cmd.flags) then
      println("An unrecognized flag was passed.")
      cmd.printHelp()
      js.Dynamic.global.process.exitCode = 1
      return ()

    cmd.command(args)
