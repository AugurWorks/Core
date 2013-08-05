#!/usr/bin/python

import json
import sys

fp = open('response.txt','r')

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

for i in sentiments:
	print str(i) + " : " + str(sentiments[i])
