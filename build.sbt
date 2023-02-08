ThisBuild / scalaVersion := "3.2.2"

ThisBuild / organization           := "works.scala"
ThisBuild / organizationName       := "Scala Works"
ThisBuild / organizationHomepage   := Some(url("https://scala.works"))
ThisBuild / homepage               := Some(url("https://github.com/scala-works/scala-cmd"))
ThisBuild / description            := "A slim CLI framework for Scala 3 apps"
ThisBuild / licenses               := List(
  "Apache 2" -> new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"),
)
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository     := "https://s01.oss.sonatype.org/service/local"
ThisBuild / developers             := List(
  Developer(
    id = "alterationx10",
    name = "Mark Rudolph",
    email = "mark@scala.works",
    url = url("https://alterationx10.com/"),
  ),
)

lazy val root = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .withoutSuffixFor(JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .settings(
    name := "scala-cmd",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-effect" % "3.4.6",
      "org.scalameta" %%% "munit"       % "1.0.0-M7" % Test,
    ),
    Compile / doc / scalacOptions ++= Seq(
      "-project",
      "Scala CMD",
      "-Yapi-subdirectory",
      "-Ygenerate-inkuire",
      "-project-version",
      version.value,
      "-social-links:" +
        "github::https://github.com/scala-works/scala-cmd",
      "-source-links:github://scala-works/scala-cmd",
      "-revision",
      "main",
      "-snippet-compiler:compile",
      "-doc-root-content",
      "README.md",
    ),
  )
  .jvmSettings(
    fork := true,
  )
