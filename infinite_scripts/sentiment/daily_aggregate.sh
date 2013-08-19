#!/bin/bash

HOME=/root/Core/infinite_scripts/sentiment

# Silently delete the response file.
rm $HOME/response.txt 2> /dev/null

# Make sure the keyword file exists.
if [ ! -f $HOME/keywords.txt ]; then
    echo "Keyword file does not exist. Exiting now."
    exit 1
fi

# Make sure we don't clobber an existing sentiment query.
if [ -f $HOME/sentiment.csv ]; then
    echo "Sentiment file already exists. Exiting now to avoid data loss."
    exit 1
fi

if [ -f $HOME/sentiment_full.csv ]; then
    echo "Full sentiment file already exists. Exiting now to avoid data loss."
    exit 1
fi

# Run the pipeline.
$HOME/login_daily.sh $HOME/keywords.txt $HOME/response.txt
python $HOME/batch_json_parse.py $HOME/keywords.txt $HOME/response.txt $HOME/sentiment.csv $HOME/sentiment_full.csv

MESSAGE="email_message.txt"
echo "Aggregate sentiment measured over the past 24 hours: " >> $HOME/$MESSAGE
cat $HOME/sentiment.csv >> $HOME/$MESSAGE
echo "" >> $HOME/$MESSAGE
echo "Sentiment per keyword: " >> $HOME/$MESSAGE
cat $HOME/sentiment_full.csv >> $HOME/$MESSAGE

cat $HOME/$MESSAGE

python $HOME/send_sentiment_email.py $HOME/$MESSAGE

rm $HOME/sentiment.csv 2> /dev/null
rm $HOME/sentiment_full.csv 2> /dev/null
rm $HOME/response.txt 2> /dev/null
rm $HOME/$MESSAGE 2> /dev/null

exit 0