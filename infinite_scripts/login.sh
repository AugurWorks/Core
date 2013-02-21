#!/bin/bash
URL="http://ec2-50-17-35-133.compute-1.amazonaws.com"

# Log in as Stephen. Yes, that is my hashed password. Please do not distribute.
curl -c cookies.txt $URL"/api/auth/login/stephen@augurworks.com/7msCYOF1lJ3aoclnjbV6KH1I9P2Xn5ht42IQx9JaRRo%3D" > /dev/null
echo ""
echo ""

# Search the enron / email community for enron, and output JSON data
curl -b cookies.txt -XPOST $URL"/api/knowledge/document/query/502997fde4b01d16a3e19876" -d '{
    "qt": [
        {
            "ftext": "enron"
        }
    ],
    "output": {
        "format": "json"
    }
}' > response.txt

echo ""
echo ""


# Dump the information about stephen
curl -b cookies.txt $URL"/api/social/person/get" > /dev/null
echo ""
echo ""


# Log out
curl -b cookies.txt $URL"/api/auth/logout" > /dev/null
echo ""
echo ""

rm cookies.txt
