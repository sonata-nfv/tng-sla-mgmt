from pymongo import MongoClient

username = 'admin'
password = 'admin'

c = MongoClient('mongodb://%s:%s@127.0.0.1' % (username, password))

db = c['Slas']

print db.name






