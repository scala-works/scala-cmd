package works.scala.cmd

import works.scala.cmd.Cmd
object Example extends Cmd:
    override def command(args: Array[String]): Unit = println("farts")
