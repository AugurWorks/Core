#!/bin/bash

# Usage: ./ticker TICKER
# Example: ./ticker AAPL
# Will output ticker information in the form:
# TICKER PRICE DATE TIME CHANGE OPEN DAILY_HIGH DAILY_LOW VOLUME
INPT=$( curl -s 'http://download.finance.yahoo.com/d/quotes.csv?s='$1'&f=sl1d1t1c1ohgv&e=.csv' )

IFS=', ' read -a array <<< $INPT

echo "${array[@]}"

