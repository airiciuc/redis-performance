#!/usr/bin/env bash

for (( i = 0; i < 50; i++ )); do
  /app/bin/app "sub" "ch*" &
done

tail -f /dev/null