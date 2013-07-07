#!/usr/bin/python

import json
import sys

start_date = sys.argv[1]
end_date = sys.argv[2]
keyword = sys.argv[3]
# Get the JSON response
json_string = open('response.txt', 'r').read()

# Find the stuff that has sentiment
# Note: this does not sum the total sentiment if any name is repeated
data = json.loads(json_string)

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

score=0
for i in info:
	(a,b) = info[i]
	score+=a*b

print str(score)
