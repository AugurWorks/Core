#!/bin/bash
URL="http://ec2-107-20-152-208.compute-1.amazonaws.com"

rm sentiment.csv
rm daily_sentiment.csv
dos2unix ftext_words.txt

# Log in as Stephen. Yes, that is my hashed password. Please do not distribute.
curl -c cookies.txt $URL"/api/auth/login/stephen@augurworks.com/7msCYOF1lJ3aoclnjbV6KH1I9P2Xn5ht42IQx9JaRRo%3D" > /dev/null
echo ""
echo ""

## Date parsing from the top two lines of ftext_words.txt
start_date=`head -n 1 ftext_words.txt`
end_date=`head -n 2 ftext_words.txt | tail -n 1`
start_date=`date -d $start_date '+%s'`
end_date=`date -d $end_date '+%s'`

rm response.txt

while [ $start_date -lt $end_date ]
do 
	tail -n +3 ftext_words.txt | while read line
	do
		# Increment the end date
		first=`date -d "@$start_date" '+%D'`
		next=$(( $start_date + 86400 ))
		next=`date -d "@$next" '+%D'`
		
		# Using etext, do the query
		query='{"qt": [{"etext":"'
		query+="$line\""
		query+='},'
		query+='{"time": {"min":'
		query+="\"$first\","
		query+='"max":'
		query+="\"$next\""
		query+='}}],"output": {"format": "json"}}'
		echo $query > query.tmp
		
		
		# There should be the response to each query on one line of the response.txt file.
		echo "$first,$next,$line" >> response.txt
		curl -b cookies.txt -XPOST $URL"/api/knowledge/document/query/50ecaf5ae4b0ea25955cdfb8" -d @query.tmp >> response.txt
		echo "" >> response.txt
		rm query.tmp
		
		
#		python ./json_parsing.py $first $next $line >> sentiment.csv
	done
	start_date=$(( $start_date + 86400 ))
done


# Dump the information about stephen
#curl -b cookies.txt $URL"/api/social/person/get" > /dev/null
#cho ""
#echo ""

# Parse all of the responses at once.
python ./batch_json_parse.py > sentiment.csv

# Log out
curl -b cookies.txt $URL"/api/auth/logout" > /dev/null
echo ""
echo ""

rm cookies.txt

rm response.txt
