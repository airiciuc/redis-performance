#!/usr/bin/env bash

echo "Channels: ${CHANNELS}"

/app/bin/app "sub" "ch" "$CHANNELS" &

tail -f /dev/null