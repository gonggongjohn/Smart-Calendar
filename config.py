HOST = '127.0.0.1'
PORT = '3306'
DATABASE = 'smart_calendar'
USERNAME = 'root'
PASSWORD = 'Qazwsx06-'

JSON_AS_ASCII = False

DB_URI = "mysql+pymysql://{username}:{password}@{host}:{port}/{db}?charset=utf8".format(
    username=USERNAME, password=PASSWORD, host=HOST, port=PORT, db=DATABASE)

SQLALCHEMY_DATABASE_URI = DB_URI
SQLALCHEMY_TRACK_MODIFICATIONS = False
SQLALCHEMY_ECHO = True

SECRET_KEY = 'smartcalendar'
SESSION_COOKIE_PATH = '/'
