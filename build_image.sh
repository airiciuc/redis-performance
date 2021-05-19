#!/usr/bin/env bash

./gradlew installDist

docker build -t pubsub_sub_cluster -f  Dockerfile_sub .
docker tag pubsub_sub_cluster registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_sub_cluster
docker push registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_sub_cluster

docker build -t pubsub_pub_cluster -f  Dockerfile_pub .
docker tag pubsub_pub_cluster registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_pub_cluster
docker push registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_pub_cluster
