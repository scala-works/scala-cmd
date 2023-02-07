package works.scala.cmd

/** A trait to extend for your CLI app
  */
trait PlatformSpecificEntry:
  lazy val cmd: Cmd


  final def main(args: Array[String]): Unit =
    if HelpFlag.isPresent(args) then
      cmd.printHelp()
      sys.exit(0)

    if Flag.hasUnrecognizedFlag(args, cmd.flags) then
      println("An unrecognized flag was passed.")
      cmd.printHelp()
      sys.exit(1)

    cmd.command(args)
