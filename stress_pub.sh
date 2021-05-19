#!/usr/bin/env bash

echo "Channels: ${CHANNELS}"
echo "Initial rounds: ${INITIAL_ROUNDS}"
echo "Initial rate increment: ${INITIAL_RATE_INCREMENT}"
echo "Rate increment: ${RATE_INCREMENT}"

/app/bin/app "pub" "ch" "$CHANNELS" "$INITIAL_ROUNDS" "$INITIAL_RATE_INCREMENT" "$RATE_INCREMENT" &

tail -f /dev/null
