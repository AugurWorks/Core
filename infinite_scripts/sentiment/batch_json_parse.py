#!/usr/bin/python

import json
import sys
import os

# Keyword file
keyword_file = sys.argv[1]

# Input file will be the responses from the infinite server
input_file = sys.argv[2]

# Output file will be where the sentiment is aggregated
output_file = sys.argv[3]

full_output_file = sys.argv[4]

if os.path.isfile(keyword_file) == False:
	print "Keyword file does not exist. Exiting now."
	sys.exit(1)
if os.path.isfile(input_file) == False:
	print "Input file from infinite does not exist. Exiting now."
	sys.exit(1)
if os.path.isfile(output_file) == True:
	print "Output file already exists. Exiting now to avoid data loss."
	sys.exit(1)
if os.path.isfile(full_output_file) == True:
	print "Full output file already exists. Exiting now to avoid data loss."
	sys.exit(1)


fp = open(input_file,'r')

# Will contain key,value pairs:
#	disambiguated_name : [(start,end,keyword,sentiment,significance),( ... ), ... ]
sentiments = dict()

meta_string = True
start_date = ""
end_date = ""
keyword = ""

for line in fp:
	line = line.strip()
	if meta_string == True:
		words = line.split(',')
		start_date = words[0]
		end_date = words[1]
		keyword = words[2]
	else:
		data = json.loads(line)
		info = {}
		for i in range(len(data["data"])):
			for j in range(len(data["data"][i]["entities"])):
				try:
					name = str(data["data"][i]["entities"][j]["disambiguated_name"])
					sentiment = float(data["data"][i]["entities"][j]["sentiment"])
					significance = float(data["data"][i]["entities"][j]["significance"])
					if name in info:
						(a,b) = info[name]
						info[name] = (a+sentiment,b+significance)
					else:
						info[name] = (sentiment,significance)
				except: 
					continue
		for disambiguated_name in info:
			(sentiment,significance) = info[disambiguated_name]
			if disambiguated_name not in sentiments:
				sentiments[disambiguated_name] = []
			sentiments[disambiguated_name].append( (start_date,end_date,keyword,sentiment,significance) )
		
	meta_string = not meta_string

fp2 = open(keyword_file, 'r')
fp2.readline()
fp2.readline()
target_words = []
for line in fp2:
	line = line.rstrip()
	target_words.append(line)


date_to_total_score = dict()
date_to_sentiment = dict()
for i in target_words:
	for j in sentiments:
		if i in j:
			list_of_scores = sentiments[j]
			for entry in list_of_scores:
				if entry[0] not in date_to_total_score:
					date_to_total_score[entry[0]] = 0
				date_to_total_score[entry[0]] = date_to_total_score[entry[0]] + (float(entry[3])*float(entry[4]))
				if (entry[0],entry[2]) not in date_to_sentiment:
					date_to_sentiment[(entry[0],entry[2])] = 0
				date_to_sentiment[(entry[0],entry[2])] = date_to_sentiment[(entry[0],entry[2])] + (float(entry[3])*float(entry[4]))

fp3 = open(output_file, "w")
for i in sorted(date_to_total_score, reverse=True):
	fp3.write(str(i) + "," + str(date_to_total_score[i]))

fp3 = open(full_output_file, "w")
for i in sorted(date_to_sentiment, reverse=True):
	fp3.write(str(i) + "," + str(date_to_total_score[i]))


sys.exit(0)
