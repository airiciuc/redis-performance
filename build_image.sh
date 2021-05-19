#!/usr/bin/env bash

./gradlew installDist

docker build -t pubsub_sub_cluster -f  Dockerfile_sub .
docker tag pubsub_sub_cluster airiciuc/pubsub_sub_cluster
docker push airiciuc/pubsub_sub_cluster

docker build -t pubsub_pub_cluster -f  Dockerfile_pub .
docker tag pubsub_pub_cluster airiciuc/pubsub_pub_cluster
docker push airiciuc/pubsub_pub_cluster
