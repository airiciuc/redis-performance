#!/usr/bin/env bash

./gradlew installDist

docker build -t pubsub_sub -f  Dockerfile_sub .
docker tag pubsub_sub airiciuc/pubsub_sub
docker push airiciuc/pubsub_sub

docker build -t pubsub_pub -f  Dockerfile_pub .
docker tag pubsub_pub airiciuc/pubsub_pub
docker push airiciuc/pubsub_pub
