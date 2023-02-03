package works.scala.cmd

/** A trait to extend for your CLI app
  */
trait CLI:

  /** The Flags to parse for this app. [[HelpFlag]] will be automatically added.
    * @return
    */
  def flags: Seq[Flag[?]] = Seq.empty

  /** The Args to parse for this app.
    * @return
    */
  def args: Seq[Arg[?]] = Seq.empty

  private val _flags = HelpFlag +: flags

  /** The main logic of your app
    * @param args
    */
  def command(args: Array[String]): Unit

  /** Helper method to print expected Flag/Args
    */
  final private def printHelp(): Unit =
    if args.nonEmpty then
      println("Args:")
      println(
        args.sortBy(_.name).map { a =>
          s"""
             |  ${ a.name } : ${ a.description }
             |""".stripMargin
        },
      )

    println("Flags:")
    println(
      _flags
        .sortBy(_.name)
        .map { f =>
          s"""
             |  ${ f._sk } ${ f._lk } : ${ f.description }
             |""".stripMargin
        }
        .mkString,
    )

  final def main(args: Array[String]): Unit =
    if HelpFlag.isPresent(args) then
      printHelp()
      sys.exit(0)

    if Flag.hasUnrecognizedFlag(args, _flags) then
      println("An unrecognized flag was passed.")
      printHelp()
      sys.exit(1)

    command(args)
