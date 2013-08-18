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

# Run the pipeline.
./login_daily.sh keywords.txt response.txt
python ./batch_json_parse.py keywords.txt response.txt sentiment.csv

# Show the total sentiment.
echo "Total sentiment for the last 24 hours: " 
echo `head -n 1 sentiment.txt | awk -F"," '{print $2}'`

exit 0