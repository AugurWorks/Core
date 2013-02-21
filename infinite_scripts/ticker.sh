#!/bin/bash

# Usage: ./ticker TICKER
# Example: ./ticker AAPL
# Will output ticker information in the form:
# TICKER
# PRICE
# DATE
# TIME
# CHANGE
# OPEN
# DAILY HIGH
# DAILY LOW
# VOLUME
INPT=$( curl -s 'http://download.finance.yahoo.com/d/quotes.csv?s='$1'&f=sl1d1t1c1ohgv&e=.csv' )

IFS=', ' read -a array <<< $INPT

for element in "${array[@]}"
do
    echo "$element"
done
