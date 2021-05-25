#!/usr/bin/env bash

./gradlew installDist

docker build -t pubsub_sub_node -f  Dockerfile_sub .
docker tag pubsub_sub_node registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_sub_node
docker push registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_sub_node

docker build -t pubsub_pub_node -f  Dockerfile_pub .
docker tag pubsub_pub_node registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_pub_node
docker push registry2.swarm.devfactory.com/aurea/jive/redis-test/pubsub_pub_node
