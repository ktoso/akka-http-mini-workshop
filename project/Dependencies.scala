import sbt._

object Dependencies {
  object Version {
    val akka        = "2.4.0-RC1"
    val akkaStreams = "1.0"
    val leveldb = "0.7"
    val algebird = "0.11.0"
    val logback = "1.1.2"
  }

  object Compile {
    val akkaActor            = "com.typesafe.akka" %% "akka-actor"            % Version.akka

    val akkaCluster          = "com.typesafe.akka" %% "akka-cluster"          % Version.akka
    val akkaClusterTools     = "com.typesafe.akka" %% "akka-cluster-tools"    % Version.akka
    val akkaPersistence      = "com.typesafe.akka" %% "akka-persistence"      % Version.akka
    val akkaClusterSharding  = "com.typesafe.akka" %% "akka-cluster-sharding" % Version.akka // pulls in akka-persistence
    val leveldb              = "org.iq80.leveldb"   % "leveldb"               % Version.leveldb // for sharding

    val akkaStream           = "com.typesafe.akka" %%"akka-stream-experimental"         % Version.akkaStreams
    val akkaStreamTestkit    = "com.typesafe.akka" %%"akka-stream-testkit-experimental" % Version.akkaStreams
    val akkaHttpCore         = "com.typesafe.akka" %%"akka-http-core-experimental"      % Version.akkaStreams
    val akkaHttp             = "com.typesafe.akka" %% "akka-http-experimental"          % Version.akkaStreams
    val akkaTestKit          = "com.typesafe.akka" %% "akka-testkit"                    % Version.akka
    val akkaMultiNodeTestKit = "com.typesafe.akka" %% "akka-multi-node-testkit"         % Version.akka

    val akkaHttpSprayJson    = "com.typesafe.akka" %% "akka-http-spray-json-experimental" % Version.akkaStreams
    val akkaHttpXml          = "com.typesafe.akka" %% "akka-http-xml-experimental"        % Version.akkaStreams
    val akkaHttpTestkit      = "com.typesafe.akka" %% "akka-http-testkit-experimental"    % Version.akkaStreams

    val akkaSlf4j            = "com.typesafe.akka" %% "akka-slf4j"                    % Version.akka
    val logbackClassic       = "ch.qos.logback"    %  "logback-classic"               % Version.logback

    val algebird             = "com.twitter"       %% "algebird-core"                     % Version.algebird
  }
  object Test {
    val scalaTest = "org.scalatest" %% "scalatest"  % "2.1.6" % "test"
    val commonsIo = "commons-io"     % "commons-io" % "2.4"   % "test"
  }

  import Compile._
  private val testing = Seq(Test.scalaTest, Test.commonsIo)
  private val cluster = Seq(akkaCluster, akkaClusterTools, akkaClusterSharding, akkaPersistence, leveldb) ++ Seq(akkaMultiNodeTestKit)
  private val streams = Seq(akkaStream, akkaStreamTestkit)
  private val logging = Seq(akkaSlf4j, logbackClassic)

  val core = Seq(akkaActor, akkaTestKit) ++ streams ++ testing ++ logging
  val engine = Seq(akkaActor, algebird) ++ cluster ++ testing
  val service = Seq(akkaActor, akkaHttpCore, akkaHttp, akkaHttpSprayJson, akkaHttpXml, akkaHttpTestkit) ++ testing

  // all in one project, to be usable from Activator
  val all = core ++ engine ++ service
}