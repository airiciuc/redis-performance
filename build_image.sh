#!/usr/bin/env bash

./gradlew installDist

docker build -t pubsub_sub -f  Dockerfile_sub .
docker tag pubsub_sub registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_sub
docker push registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_sub

docker build -t pubsub_pub -f  Dockerfile_pub .
docker tag pubsub_pub registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_pub
docker push registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_pub
