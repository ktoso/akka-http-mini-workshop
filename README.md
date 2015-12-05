akka-codepot-workshop
=====================
codepot workshop combining Akka Cluster with streaming and various patterns.

TODO:
- [ ] akka-http and ask into worker which scans entire thing
- [ ] akka-http and ask into worker with separate dispatcher
- [ ] akka-http and ask into master, who has workers (remote)
- [ ] akka-http and ask into master, who has workers (remote), who fail
- [ ] akka-http and ask into master, who has workers (remote), who fail, so we use backup-requests
- [ ] how does this compare with micro services
- [ ] actSlow_!!! show crappy blocking impl's impact on performance (then separate dispatcher, then `after`)
- [ ] akka-http and ask into cluster sharding
- [ ] show cluster sharding how it recovers nodes
- [ ] remembered entries
- [ ] recovery of cache from journal - make them PersistentActors
- [ ] return JSON
- [ ] return streaming JSON 
- [ ] akka-http and ask into cluster sharding, with LRU cache (compose actors)

License
=======

Apache 2.0