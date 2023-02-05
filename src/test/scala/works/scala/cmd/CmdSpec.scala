package works.scala.cmd

import munit.FunSuite

class CmdSpec extends FunSuite:

  test("basic app compiles and runs with no args") {
    object TestApp extends Cmd:
      override def command(args: Array[String]): Unit = ()
    TestApp.main(Array.empty[String])
  }

  // TODO need to test default of -h, and unknown args, 
  // but need to trap the exit code, or re-work main method
