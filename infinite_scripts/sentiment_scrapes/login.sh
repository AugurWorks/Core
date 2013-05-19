#!/bin/bash
URL="http://ec2-50-17-35-133.compute-1.amazonaws.com"

# Log in as Stephen. Yes, that is my hashed password. Please do not distribute.
curl -c cookies.txt $URL"/api/auth/login/stephen@augurworks.com/7msCYOF1lJ3aoclnjbV6KH1I9P2Xn5ht42IQx9JaRRo%3D" > /dev/null
echo ""
echo ""

## Date parsing from the top two lines of ftext_words.txt
start_date=`head -n 1 ftext_words.txt`
end_date=`head -n 2 ftext_words.txt | tail -n 1`
start_date=`date -d $start_date '+%s'`
end_date=`date -d $end_date '+%s'`
days_passed=`echo $(( (end_date-start_date)/86400 ))`
num_days=0

while [ $start_date -le $end_date ]
do 
	tail -n +3 ftext_words.txt | while read line
	do
		# Search the enron / email community for enron, and output JSON data
		query='{"qt": [{"ftext":"'
		query+="$line\""
		query+='}],"output": {"format": "json"},'
		
		first=`date -d "@$start_date" '+%D'`
		next=$(( $start_date + 86400 ))
		next=`date -d "@$next" '+%D'`
		
		query+='"time":{"min":'
		query+="$first\""
		query+='"max":'
		query+="$next\""
		query+='}}'
		
		echo $query > query.tmp
		
		curl -b cookies.txt -XPOST $URL"/api/knowledge/document/query/502997fde4b01d16a3e19876" -d @query.tmp > response.txt
		
		rm query.tmp
		
		python ./json_parsing.py $first $next >> sentiment.csv
	done
	start_date=$(( $start_date + 86400 ))
done

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
