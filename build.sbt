ThisBuild / scalaVersion      := "3.2.2"
ThisBuild / organizationName  := "works.scala.cmd"
ThisBuild / versionScheme     := Some("early-semver")
ThisBuild / version           := Versioning.versionFromTag
ThisBuild / publishMavenStyle := true

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala-cmd",
    fork := true,
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
      "README.md"
    )
  )
