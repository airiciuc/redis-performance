#!/usr/bin/env bash

./gradlew installDist

docker build -t pubsub_sub_cluster_mixed -f  Dockerfile_sub .
docker tag pubsub_sub_cluster_mixed registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_sub_cluster_mixed
docker push registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_sub_cluster_mixed

docker build -t pubsub_pub_cluster_mixed -f  Dockerfile_pub .
docker tag pubsub_pub_cluster_mixed registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_pub_cluster_mixed
docker push registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_pub_cluster_mixed
