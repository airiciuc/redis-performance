#!/usr/bin/env bash

echo "Host: ${HOST}"
echo "Channels: ${CHANNELS}"

/app/bin/app "sub" "$HOST" "ch" "$CHANNELS" &

tail -f /dev/null