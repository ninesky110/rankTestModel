name := "rankTestModel"
 
version := "1.0"

lazy val `ranktestmodel` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"
libraryDependencies += "javax.inject" % "javax.inject" % "1"
libraryDependencies += "ml.dmlc" %% "xgboost4j" % "1.1.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

assemblyMergeStrategy in assembly := { entry =>
  val strategy = (assemblyMergeStrategy in assembly).value(entry)
  if (strategy == MergeStrategy.deduplicate) MergeStrategy.first
  else strategy
}

      