#!/bin/bash

# Looks up a ticker, adds the result to the DB
# Usage: ./ticker_to_db AAPL
/root/Core/infinite_scripts/adjusted_ticker.sh $1 > .tmp && /root/Core/infinite_scripts/add_to_db.sh $( cat 
.tmp ) 
&& rm .tmp


