#!/bin/bash

# Looks up a ticker, adds the result to the DB
# Usage: ./ticker_to_db AAPL
./ticker.sh $1 > .tmp && ./add_to_db.sh $( cat .tmp ) && rm .tmp

