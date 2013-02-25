#!/bin/bash

# Run the daily stock price gathering
/root/Core/infinite_scripts/daily_stocks.sh

# run sql_to_csv.py
python /root/Core/infinite_scripts/sql_to_csv.py

# move output to the right folder
mv /root/Core/infinite_scripts/something.csv "/root/Core/java/nets/test_files/Generic Full Training Data.csv"

# run generic formatter
python /root/Core/java/nets/test_files/GenericFormatter.py

# run Alfred
java -jar /root/Core/java/Alfred.jar > /root/Core/java/prediction.txt

# email us the output
python /root/Core/infinite_scripts/send_email.py