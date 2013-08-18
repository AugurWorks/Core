#!/bin/bash

# Silently delete the response file.
rm response.txt 2> /dev/null

# Make sure the keyword file exists.
if [ ! -f keywords.txt ]; then
    echo "Keyword file does not exist. Exiting now."
    exit 1
fi

# Make sure we don't clobber an existing sentiment query.
if [ -f sentiment.csv ]; then
    echo "Sentiment file already exists. Exiting now to avoid data loss."
    exit 1
fi

if [ -f sentiment_full.csv ]; then
    echo "Full sentiment file already exists. Exiting now to avoid data loss."
    exit 1
fi

# Run the pipeline.
./login_daily.sh keywords.txt response.txt
python ./batch_json_parse.py keywords.txt response.txt sentiment.csv sentiment_full.csv

MESSAGE="email_message.txt"
echo "Aggregate sentiment measured over the past 24 hours: " >> $MESSAGE
cat sentiment.csv >> $MESSAGE
echo "" >> $MESSAGE
echo "Sentiment per keyword: " >> $MESSAGE
cat sentiment_full.csv >> $MESSAGE

cat $MESSAGE

python ./send_sentiment_email.py

rm sentiment.csv 2> /dev/null
rm sentiment_full.csv 2> /dev/null
rm response.txt 2> /dev/null
rm $MESSAGE 2> /dev/null

exit 0