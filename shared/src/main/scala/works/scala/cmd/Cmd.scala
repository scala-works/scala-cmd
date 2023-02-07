package works.scala.cmd

import scala.compiletime.*

/** A trait to extend for your CLI app
  */
trait Cmd extends PlatformSpecificEntry:
  override lazy val cmd: Cmd = this

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
  def command(args: Array[String]): Unit

  /** Helper method to print expected Flag/Args
    */
  final def printHelp(): Unit =
    val _flags = HelpFlag +: flags
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
