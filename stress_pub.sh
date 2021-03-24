#!/usr/bin/env bash

echo "Publishers: ${PUBLISHERS}"
echo "Messages per second: ${RATE}"

for (( i = 0; i < $PUBLISHERS; i++ )); do
  /app/bin/app "pub" "ch${i}" "$RATE" &
done

tail -f /dev/null
