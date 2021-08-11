from ext import db


class User(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    username = db.Column(db.String(50))
    password = db.Column(db.String(50))
    phone = db.Column(db.String(50))
    nickname = db.Column(db.String(50))


class Schedule(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    user = db.Column(db.Integer)
    name = db.Column(db.String(100))
    category = db.Column(db.Integer)
    start = db.Column(db.DateTime)
    end = db.Column(db.DateTime)


class Category(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    name = db.Column(db.String(50))
