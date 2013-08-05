#!/bin/bash

day=`eval date +%d`
month1=`eval date +%m`
month=`echo $month1 - 1 | bc `
year=`eval date +%Y`
curl -s 'http://ichart.yahoo.com/table.csv?s='$1$'&a='$month'&b='$day'&c='$year'&d='$month'&e='$day'&f='$year'&g=d&ignore=.csv' > old.tmp

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