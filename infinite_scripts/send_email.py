#!/usr/bin/python

import smtplib
import sys

gmail_user = "alfred@augurworks.com"
gmail_password = "BatMobile"
FROM = "Alfred Pennyworth"
TO = ['stephen@augurworks.com','brian@augurworks.com','drew@augurworks.com']
SUBJECT = 'Tomorrow\'s predictions'

f = open('/root/Core/java/nohup.out')
pred = f.read()

TEXT = pred

message = """\From: %s\nTo: %s\nSubject: %s\n\n%s""" % (FROM, ", ".join(TO), SUBJECT, TEXT)

try:
    server = smtplib.SMTP("smtp.gmail.com", 587)
    server.ehlo()
    server.starttls()
    server.ehlo()
    server.login(gmail_user, gmail_password)
    server.sendmail(FROM, TO, message)
    server.close()
    print 'successfully sent the mail'
except:
    print "failed to send mail"
    print sys.exc_info()
