#!/bin/bash

# Function to get one-off old data from stocks. 

# usage: same inputs as backdata_download.sh, except no ticker
# ./backdate_script.sh 0 23 2013 1 23 2013 d

./backdata_download.sh AAPL $1 $2 $3 $4 $5 $6 $7
./backdata_download.sh GOOG $1 $2 $3 $4 $5 $6 $7
./backdata_download.sh USO $1 $2 $3 $4 $5 $6 $7
./backdata_download.sh ^GSPC $1 $2 $3 $4 $5 $6 $7
./backdata_download.sh GLD $1 $2 $3 $4 $5 $6 $7
./backdata_download.sh UNG $1 $2 $3 $4 $5 $6 $7
./backdata_download.sh SLV $1 $2 $3 $4 $5 $6 $7
./backdata_download.sh DFI $1 $2 $3 $4 $5 $6 $7
./backdata_download.sh DIA $1 $2 $3 $4 $5 $6 $7
