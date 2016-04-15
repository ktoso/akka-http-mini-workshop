import sbt._

object Dependencies {
  object Version {
    val akka        = "2.4.4"
    val akkaStreams = "2.4.4"
    val leveldb = "0.7"
    val logback = "1.1.2"
  }

  object Compile {
    val akkaActor            = "com.typesafe.akka" %% "akka-actor"            % Version.akka

    val akkaStream           = "com.typesafe.akka" %%"akka-stream"                      % Version.akkaStreams
    val akkaHttpCore         = "com.typesafe.akka" %%"akka-http-core"                   % Version.akkaStreams
    val akkaHttp             = "com.typesafe.akka" %% "akka-http-experimental"          % Version.akkaStreams


    val akkaHttpSprayJson    = "com.typesafe.akka" %% "akka-http-spray-json-experimental" % Version.akkaStreams
    val akkaHttpXml          = "com.typesafe.akka" %% "akka-http-xml-experimental"        % Version.akkaStreams

    val akkaHttpTestkit      = "com.typesafe.akka" %% "akka-http-testkit"                 % Version.akkaStreams
    val akkaStreamTestkit    = "com.typesafe.akka" %%"akka-stream-testkit"                % Version.akkaStreams
    val akkaTestKit          = "com.typesafe.akka" %% "akka-testkit"                      % Version.akka
    val akkaMultiNodeTestKit = "com.typesafe.akka" %% "akka-multi-node-testkit"           % Version.akka

    val akkaSlf4j            = "com.typesafe.akka" %% "akka-slf4j"                    % Version.akka
    val logbackClassic       = "ch.qos.logback"    %  "logback-classic"               % Version.logback
  }
  object Test {
    val scalaTest = "org.scalatest" %% "scalatest"  % "2.1.6" % "test"
    val commonsIo = "commons-io"     % "commons-io" % "2.4"   % "test"
  }

  import Compile._
  private val testing = Seq(Test.scalaTest, Test.commonsIo)
  private val streams = Seq(akkaStream, akkaStreamTestkit)
  private val logging = Seq(akkaSlf4j, logbackClassic)

  val core = Seq(akkaActor, akkaTestKit) ++ streams ++ testing ++ logging
  val engine = Seq(akkaActor) ++ testing ++ logging
  val service = Seq(akkaActor, akkaHttpCore, akkaHttp, akkaHttpSprayJson, akkaHttpXml, akkaHttpTestkit) ++ testing ++ logging

  // all in one project, to be usable from Activator
  val all = core ++ engine ++ service
}
