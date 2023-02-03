package works.scala.cmd

/** Arg are positional arguments passed to the command, and can be parsed to
  * type A
  */
trait Arg[A]:

  /** Name of the flag, to be printed with help
    */
  val name: String

  /** Description of the purpose of this Argument
    */
  val description: String

  /** A partial function that will take the String value of the passed argument,
    * and convert it to type A.
    *
    * Typical usage might be for parsing String to Int/Float/Custom Domain, e.g.
    * for a Argument[Int], parse = str => str.toInt.
    *
    * Advanced usage could be used to do more targeted work as well, such as
    * processing the argument directly, versus simply obtaining the value for it
    * to be parsed later, e.g. for a Argument[String], parse = str =>
    * str.toUpperCase
    *
    * @return
    *   the evaluation of String => A
    */
  def parse: PartialFunction[String, A]
