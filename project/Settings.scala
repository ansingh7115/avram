import Dependencies._
import Merging._
import Testing._
import Version._
import sbt.Keys._
import sbt._
import sbtassembly.AssemblyPlugin.autoImport._

object Settings {

  val artifactory = "https://broadinstitute.jfrog.io/broadinstitute/"

  val commonResolvers = List(
    "artifactory-releases" at artifactory + "libs-release",
    "artifactory-snapshots" at artifactory + "libs-snapshot"
  )

  //coreDefaultSettings + defaultConfigs = the now deprecated defaultSettings
  val commonBuildSettings = Defaults.coreDefaultSettings ++ Seq(
    javaOptions += "-Xmx2G",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
  )

  val commonCompilerSettings = Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-encoding", "utf8",
    "-target:jvm-1.8",
    "-language:postfixOps",
    "-Ypartial-unification"
  )

  //sbt assembly settings
  val commonAssemblySettings = Seq(
    assemblyMergeStrategy in assembly := customMergeStrategy((assemblyMergeStrategy in assembly).value),
    test in assembly := {}
  )

  //common settings for all sbt subprojects
  val commonSettings =
    commonBuildSettings ++ commonAssemblySettings ++ commonTestSettings ++ List(
      organization  := "org.broadinstitute.dsde.workbench",
      scalaVersion  := "2.12.6",
      resolvers ++= commonResolvers,
      scalacOptions ++= commonCompilerSettings
    )

  //the full list of settings for the root project that's ultimately the one we build into a fat JAR and run
  //coreDefaultSettings (inside commonSettings) sets the project name, which we want to override, so ordering is important.
  //thus commonSettings needs to be added first.
  val rootSettings = commonSettings ++ List(
    name := "avram",
    libraryDependencies ++= rootDependencies
    //the version is applied in rootVersionSettings and is set to 0.1-githash.
    //we don't really use it for anything but we might when we publish our model
  ) ++ commonAssemblySettings ++ rootVersionSettings


}