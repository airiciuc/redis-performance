#!/usr/bin/env bash

./gradlew installDist

docker build -t pubsub_sub_2 -f  Dockerfile_sub .
docker tag pubsub_sub_2 registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_sub_2
docker push registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_sub_2

docker build -t pubsub_pub_2 -f  Dockerfile_pub .
docker tag pubsub_pub_2 registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_pub_2
docker push registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_pub_2
