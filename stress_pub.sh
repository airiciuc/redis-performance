#!/usr/bin/env bash

echo "Publishers: ${PUBLISHERS}"
echo "Messages per second: ${RATE}"

for (( i = 0; i < 5; i++ )); do
  echo "Creating publisher ${i}"
  /app/bin/app "pub" "ch" "200" &
  sleep 5m
done

for (( i = 5; i < $PUBLISHERS; i++ )); do
  echo "Creating publisher ${i}"
  /app/bin/app "pub" "ch" "$RATE" &
  sleep 5m
done

tail -f /dev/null
