#!/bin/sh
set -euo pipefail
IFS=$'\n\t'

scalac overlap.scala && scala overlap coding_challenge_data_set.txt 
