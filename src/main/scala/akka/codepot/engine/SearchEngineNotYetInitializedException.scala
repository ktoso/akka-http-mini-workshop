package akka.codepot.engine

case class SearchEngineNotYetInitializedException(msg: String)
  extends RuntimeException(msg)
