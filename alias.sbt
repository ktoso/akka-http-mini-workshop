addCommandAlias("run-http-service", "runMain akka.codepot.service.HttpServiceApp " +
  "-Dakka.remote.netty.tcp.port=2551 " +
  "-Dakka.cluster.roles.0=http-service " +
  "-Dcodepot.http.port=8080")

addCommandAlias("run-engine-1", "runMain akka.codepot.engine.SearchEngineApp " +
  "-Dakka.remote.netty.tcp.port=2552 " +
  "-Dakka.cluster.roles.0=search-engine")

addCommandAlias("run-engine-2", "runMain akka.codepot.engine.SearchEngineApp " +
  "-Dakka.remote.netty.tcp.port=2553 " +
  "-Dakka.cluster.roles.0=search-engine")

addCommandAlias("run-engine-3", "runMain akka.codepot.engine.SearchEngineApp " +
  "-Dakka.remote.netty.tcp.port=2554 " +
  "-Dakka.cluster.roles.0=search-engine")

addCommandAlias("run-engine-4", "runMain akka.codepot.engine.SearchEngineApp " +
  "-Dakka.remote.netty.tcp.port=2555 " +
  "-Dakka.cluster.roles.0=search-engine")


addCommandAlias("run-journal", "runMain akka.codepot.persistence.SharedJournalApp " +
  "-Dakka.remote.netty.tcp.port=2556 " +
  "-Dakka.cluster.roles.0=shared-journal")