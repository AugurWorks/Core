#!/bin/bash
URL="http://ec2-50-17-35-133.compute-1.amazonaws.com"

rm sentiment.csv
rm daily_sentiment.csv
dos2unix ftext_words.txt

# Log in as Stephen. Yes, that is my hashed password. Please do not distribute.
curl -c cookies.txt $URL"/api/auth/login/stephen@augurworks.com/7msCYOF1lJ3aoclnjbV6KH1I9P2Xn5ht42IQx9JaRRo%3D" > /dev/null
echo ""
echo ""

tomorrow=''
while read date_line
do
	start_date=`date -d $date_line '+%s'`
	score=0
	if [ "$tomorrow" = "" ]
	then
		tomorrow=$start_date
	else
		while read line
		do
	
			first=`date -d "@$start_date" '+%D'`
			next=`date -d "@$tomorrow" '+%D'`
		
			query='{"qt": [{"etext":"'
			query+="$line\""
			query+='},'
			query+='{"time": {"min":'
			query+="\"$first\","
			query+='"max":'
			query+="\"$next\""
			query+='}}],"output": {"format": "json"}}'
		
			echo $query > query.tmp
		
			curl -b cookies.txt -XPOST $URL"/api/knowledge/document/query/50ecaf5ae4b0ea25955cdfb8" -d @query.tmp > response.txt
		
			rm query.tmp
		
			python ./json_parsing.py $first $next $line >> sentiment.csv
			temp=`python ./daily_parsing.py $first $next $line`
			score=`echo $score + $temp | bc -l`
		done < <(tail -n +1 ftext_words.txt)
		next=`date -d "@$tomorrow" '+%D'`
		echo "$next,$score" >> daily_sentiment.csv
		tomorrow=$start_date
	fi
done < <(tail -n +1 dates.csv)

# Dump the information about stephen
#curl -b cookies.txt $URL"/api/social/person/get" > /dev/null
#cho ""
#echo ""

# Log out
curl -b cookies.txt $URL"/api/auth/logout" > /dev/null
echo ""
echo ""

rm cookies.txt

rm response.txt
