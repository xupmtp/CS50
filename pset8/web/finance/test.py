import datetime
import pytz
# Asia/Taipei
tw = pytz.timezone(u'Asia/Taipei')

#set d timezone is 'Asia/Taipei'
a = datetime.datetime.now(tw).strftime("%Y-%m-%d %H:%M:%S")

print(a)