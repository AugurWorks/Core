#!/bin/bash

# Will run every day, searching infinite for the notable keywords
# Expects the word file as input
INPUT_FILE=$1
OUTPUT_FILE=$2

# Check that the input file exists and that the output file does not exist.
if [ ! -f $INPUT_FILE ]; then
    echo $INPUT_FILE" does not exist. Exiting now."
    return 1
fi
if [ -f $OUTPUT_FILE ]; then
    echo $OUTPUT_FILE" already exists. Exiting to avoid data loss."
    return 1
fi

TODAY=$(date +"%s")
TODAY_DATE=$(date -d "@$TODAY" +"%m/%d/%y")

YESTERDAY=$(( $TODAY - 86400 ))
YESTERDAY_DATE=$(date -d "@$YESTERDAY" +"%m/%d/%y")

URL="http://ec2-107-20-152-208.compute-1.amazonaws.com"

start_date=$YESTERDAY
end_date=$TODAY

echo "Searching infinite for keywords in file "$INPUT_FILE" from \
start date "$YESTERDAY_DATE" to end date "$TODAY_DATE"."
echo "Results will be output to "$OUTPUT_FILE"."
echo "Infinite URL: "$URL

# Logging in
curl -c cookies.txt $URL"/api/auth/login/stephen@augurworks.com/7msCYOF1lJ3aoclnjbV6KH1I9P2Xn5ht42IQx9JaRRo%3D" > /dev/null
echo ""
echo ""

# Querying infinite
while [ $start_date -lt $end_date ]
do 
	cat $INPUT_FILE | while read line
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
		echo "$first,$next,$line" >> $OUTPUT_FILE
		# Putting a * should search all communities we know about.
		curl -b cookies.txt -XPOST $URL"/api/knowledge/document/query/*" -d @query.tmp >> $OUTPUT_FILE
		echo "" >> $OUTPUT_FILE
		rm query.tmp
       	
	done
	start_date=$(( $start_date + 86400 ))
done

# Logging out
curl -b cookies.txt $URL"/api/auth/logout" > /dev/null
echo ""
echo ""

# Clean up the cookies file
rm cookies.txt
