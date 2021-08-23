from ext import db


class User(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    username = db.Column(db.String(50))
    password = db.Column(db.String(50))
    phone = db.Column(db.String(50))
    nickname = db.Column(db.String(50))
    regtime = db.Column(db.DateTime)
    occupation = db.Column(db.Integer)
    major = db.Column(db.Integer)
    school = db.Column(db.Integer)


class Schedule(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    uuid = db.Column(db.String(50))
    user = db.Column(db.Integer)
    name = db.Column(db.String(100))
    category = db.Column(db.Integer)
    start = db.Column(db.DateTime)
    end = db.Column(db.DateTime)
    pos_alt = db.Column(db.DECIMAL(16, 13))
    pos_long = db.Column(db.DECIMAL(16, 13))


class Category(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    name = db.Column(db.String(50))


class Occupation(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    name = db.Column(db.String(50))


class Major(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    name = db.Column(db.String(50))


class School(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    name = db.Column(db.String(50))
