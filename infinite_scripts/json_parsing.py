#!/usr/bin/python

import json

# Get the JSON response
json_string = open('response.txt', 'r').read()

# Find the stuff that has sentiment
# Note: this does not sum the total sentiment if any name is repeated
data = json.loads(json_string)
for i in range(len(data["data"])):
	for j in range(len(data["data"][i]["entities"])):
		try:
			print str(data["data"][i]["entities"][j]["disambiguated_name"]) + ' : ' + str(data["data"][i]["entities"][j]["sentiment"])
		except: 
			continue
