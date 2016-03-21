#!/bin/bash

echo "select price, day_change, volume, adjusted_close from augurworks.stocks where ticker='dia' order by date desc;" > .tmp

mysql -u saf < .tmp > output.txt

cat output.txt | sed 's/\t/,/g' > out.txt
tail -n+2 out.txt > truncd.txt

header=`head -n 1 out.txt`
echo "$header,order" > answer.csv

tomorrow=0
while read line; do
    now=`echo $line | awk -F',' '{print $4}'`
    if [[ "$tomorrow" != "0" ]]
    then
	diff=`echo $now - $tomorrow | bc`
	if (( $(echo "$diff < 0" | bc -l) ))
	then
	    echo "$line,BUY" >> answer.csv
	else
	    echo "$line,SELL" >> answer.csv
	fi
    fi
    tomorrow=$now
done < truncd.txt

