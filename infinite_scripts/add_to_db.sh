#!/bin/bash

# Usage: ./add_to_db ticker price date time change open high low vol
# ./ticker.sh AAPL > .tmp && ./add_to_db.sh $( cat .tmp ) && rm .tmp
echo 'INSERT INTO augurworks.stocks (ticker,price,date,time,day_change,open,daily_high,daily_low,volume)
	VALUES('$1','$2',STR_TO_DATE('$3', "%m/%d/%Y"),'$4','$5','$6','$7','$8','$9');' > query.sql

mysql -uroot -psaf < query.sql &

rm query.sql
