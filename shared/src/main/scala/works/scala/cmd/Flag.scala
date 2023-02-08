package works.scala.cmd

import scala.annotation.tailrec

/** Flags are non-positional arguments passed to the app. Flags can be generally
  * used as either an argument flag, which expects an argument parsed as type F,
  * or boolean flags which do not.
  * @tparam F
  *   The type expected to parse the flag argument as.
  */
trait Flag[F]:

  /** The name of the flag, e.g. "help". This will be parsed as s"--\\$name",
    * e.g. "--help"
    */
  val name: String

  /** A short-key version of name, e.g. "h". This will be parsed as
    * s"-\\$shortKey", e.g. "-h"
    */
  val shortKey: String

  /** A description of the purpose of this flag, used in [[documentation]].
    */
  val description: String

  /** A partial function that will take the String value of the passed argument,
    * and convert it to type F.
    *
    * Typical usage might be for parsing String to Int/Float/Custom Domain, e.g.
    * for a Flag[Int], parse = str => str.toInt.
    *
    * Advanced usage could be used to do more targeted work as well, such as
    * processing the argument directly, versus simply obtaining the value for it
    * to be parsed later, e.g. for a Flag[String], parse = str =>
    * str.toUpperCase
    *
    * @return
    *   the evaluation of String => F
    */
  def parseArgument: PartialFunction[String, F]

  /** Indicates this Flag expects an argument. Defaults to true.
    */
  val hasArgument: Boolean = true

  /** Hyphenated shortKey used for parsing
    */
  final private[cmd] lazy val _sk: String = s"-$shortKey"

  /** Hyphenated name used for parsing.
    */
  final private[cmd] lazy val _lk: String = s"--$name"

  /** Checks if flags to trigger this Flag are present in the provided Command
    * arguments
    *
    * @param args
    *   The arguments passed to the command
    * @return
    *   true if present, otherwise false
    */
  final def isPresent(args: List[String]): Boolean =
    args.exists(a => a == _sk || a == _lk)

  /** A method that will find the first instance of an argument triggering this
    * Flag, if present, and evaluate the [[parseArgument]] partial function on
    * it.
    *
    * Most useful when this Flag is expected once.
    *
    * @param args
    *   The arguments passed to the command
    * @return
    * @see
    *   [[parseArgument]]
    */
  final def parseFirstFlagArg(args: List[String]): Option[F] =
    args
      .dropWhile(a => !(a == _sk || a == _lk))
      .drop(1)
      .headOption
      .map(parseArgument)

  /** Finds instances of arguments that trigger this Flag, and processes them
    * through [[parseArgument]]
    *
    * @param args
    *   The arguments passed to the command
    */
  final def parseFlagArgs(args: List[String]): Seq[F] =
    @tailrec
    def loop(a: List[String], accum: Seq[F]): Seq[F] =
      a.toList match
        case Nil                                  => accum
        case f :: Nil                             => accum
        case f :: fa :: _ if f == _sk || f == _lk =>
          loop(a.drop(2), accum :+ parseArgument(fa))
        case _                                    => loop(a.drop(1), accum)
    loop(args, Seq.empty)

  @tailrec
  final private def stripFlagArgs(
      args: List[String],
      accum: List[String],
  ): List[String] =
    args.toList match
      case Nil                            =>
        accum
      case f :: _ if f == _sk || f == _lk =>
        if hasArgument then stripFlagArgs(args.drop(2), accum)
        else stripFlagArgs(args.drop(1), accum)
      case f :: _                         =>
        stripFlagArgs(args.drop(1), accum :+ f)

  /** A method to remove the flag trigger, and any arguments, from the input
    * args, so that positional Args can then be processed
    *
    * @param args
    * @param accum
    * @return
    */
  final def stripArgs(args: List[String]): List[String] =
    if hasArgument then stripFlagArgs(args, List.empty)
    else args.filterNot(a => a == _sk || a == _lk)

object Flag:

  /** A helper method to check if the app input has unrecognized flags.
    *
    * @param args
    * @param flags
    * @return
    */
  def hasUnrecognizedFlag(args: List[String], flags: Seq[Flag[?]]): Boolean =
    val flagTriggers: Seq[String] = flags.flatMap(f => Seq(f._sk, f._lk))
    args
      .filter(_.startsWith("-"))
      .exists(t => !flagTriggers.contains(t))

  /** Strip the given flags from the given arguments.
    *
    * @param args
    * @param flags
    * @return
    */
  @tailrec
  def stripFlags(args: List[String], flags: Seq[Flag[?]]): List[String] =
    flags match
      case Nil      => args
      case h :: Nil => h.stripArgs(args)
      case h :: t   => stripFlags(h.stripArgs(args), t)

/** A helper trait that defaults parseArgument to a Unit value.
  */
trait UnitFlag extends Flag[Unit]:
  override def parseArgument: PartialFunction[String, Unit] = _ => ()

trait BooleanFlag extends UnitFlag:
  override val hasArgument: Boolean = false

/** A default Help flag, automatically provided to apps.
  */
case object HelpFlag extends BooleanFlag:
  override val name: String        = "help"
  override val shortKey: String    = "h"
  override val description: String =
    "Prints the description of all flags and args"
