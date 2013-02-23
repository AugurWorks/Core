#!/bin/bash

# Helper function for one-off data integration

# Usage: ./backdata_download TICKER startmo startday startyear endmo endday endyear

curl -s 'http://ichart.finance.yahoo.com/table.csv?s='$1'&a='$2'&b='$3'&c='$4'&d='$5'&e='$6'&f='$7'&g='$8'&ignore=.csv' > old.tmp

tail -n+2 old.tmp > old_edit.tmp

while read line; do
	IFS="," read -a temp <<< $line
	unset $IFS
	array=($temp)
	subtraction=`echo ${array[4]} - ${array[1]} | bc `
	echo 'INSERT INTO augurworks.stocks (ticker,price,date,time,day_change,open,daily_high,daily_low,volume,adjusted_close) VALUES ("'$1'",'${array[4]}',STR_TO_DATE("'${array[0]}'","%Y-%m-%d"),"4:30 PM",'$subtraction','${array[1]}','${array[2]}','${array[3]}','${array[5]}','${array[6]}');' >> oldquery.sql 
done < old_edit.tmp

mysql -uroot -paugurworks < oldquery.sql

rm oldquery.sql
rm old.tmp
rm old_edit.tmp



## YAHOO reverse engineered API for historical data download
#http://ichart.finance.yahoo.com/table.csv?s=GE&a=0&b=2&c=1962&d=1&e=23&f=2013&g=d&ignore=.csv
#s=GE // stock=TICKER
#a = start month (0 indexed)
#b = start day
#c = start year
#d = end month (0 indexed)
#e = end day
#f = end year
#g = daily


