name := "event-counter"

version := "0.0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-http" % "10.0.5",
	"com.datastax.cassandra" %  "cassandra-driver-core" % "3.2.0"
)

assemblyMergeStrategy in assembly ~= {
	(old) => {
		case x if x.endsWith("META-INF/io.netty.versions.properties") ⇒ MergeStrategy.first
		case x => old(x)
	}
}

mainClass in assembly := Some("Main")
