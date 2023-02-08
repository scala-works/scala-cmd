package works.scala.cmd

import munit.*

object EnvFlag extends Flag[String]:
  override val name: String                                   = "env"
  override val shortKey: String                               = "e"
  override val description: String                            = "A flag to pass in an environment"
  override def parseArgument: PartialFunction[String, String] = str =>
    str.toLowerCase()

object ForceFlag extends BooleanFlag:
  override val name: String        = "force"
  override val shortKey: String    = "f"
  override val description: String =
    "A flag to indicate we should force an operation"

class FlagSpec extends FunSuite:

  val testFlags: Seq[Flag[?]] = Seq(
    HelpFlag,
    EnvFlag,
    ForceFlag,
  )

  val goodArgs: List[String]  = "-e dev -f arg1 arg2".split(" ").toList
  val goodArgs2: List[String] = "arg1 arg2 --env dev -f ".split(" ").toList
  val goodArgs3: List[String] = "-e dev arg1 arg2 --force ".split(" ").toList

  test("check if preset") {
    assertEquals(
      HelpFlag.isPresent(goodArgs),
      false,
    )
    assertEquals(
      ForceFlag.isPresent(goodArgs),
      true,
    )
  }

  test("boolean flag strips argument") {
    val args = List("arg1", "arg2")
    assertEquals(HelpFlag.hasArgument, false)
    assertEquals(
      HelpFlag.stripArgs(args),
      args.toSeq,
    )
    assertEquals(
      HelpFlag.stripArgs("-h" +: "--help" +: args),
      args.toSeq,
    )
  }

  test("argument flag strips arguments") {
    val args = List("arg1", "arg2")
    assertEquals(EnvFlag.hasArgument, true)
    assertEquals(
      EnvFlag.stripArgs(args).toSeq,
      args.toSeq,
    )
    assertEquals(
      EnvFlag.stripArgs("-e" +: "dev" +: args).toSeq,
      args.toSeq,
    )
  }

  test("argument flag parses argument") {
    val args1 = List("--env", "DEV", "arg1", "arg2")
    val args2 = List("arg1", "arg2", "-e", "DEV")
    val args3 = List("--env", "DEV", "arg1", "arg2", "-e", "DEV")
    assertEquals(
      EnvFlag.parseFirstFlagArg(args1),
      Some("dev"),
    )
    assertEquals(
      EnvFlag.parseFirstFlagArg(args2),
      Some("dev"),
    )
    assertEquals(
      EnvFlag.parseFlagArgs(List("arg1", "arg2")),
      Seq.empty[String],
    )
    assertEquals(
      EnvFlag.parseFlagArgs(args3).toSeq,
      Seq("dev", "dev"),
    )
  }

  test("check for recognized flags") {
    assert(!Flag.hasUnrecognizedFlag(goodArgs, testFlags))
    assert(!Flag.hasUnrecognizedFlag(goodArgs2, testFlags))
    assert(!Flag.hasUnrecognizedFlag(goodArgs3, testFlags))
  }

  test("check for unrecognized flags") {
    val unrecognizedArgs: List[String] = "-e dev -f arg1 -z arg2".split(" ").toList
    assert(Flag.hasUnrecognizedFlag(unrecognizedArgs, testFlags))
  }

  test("strip all flags from an argument list") {
    val finalArgs = List("arg1", "arg2")
    assertEquals(
      Flag.stripFlags(goodArgs, testFlags),
      finalArgs,
    )
    assertEquals(
      Flag.stripFlags(goodArgs2, testFlags),
      finalArgs,
    )
    assertEquals(
      Flag.stripFlags(goodArgs3, testFlags),
      finalArgs,
    )
  }
