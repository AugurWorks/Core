#!/bin/bash

# Looks up a ticker, adds the result to the DB
# Usage: ./ticker_to_db AAPL
/root/Core/infinite_scripts/ticker.sh $1 > .tmp && /root/Core/infinite_scripts/add_to_db.sh $( cat .tmp ) 
&& rm .tmp


