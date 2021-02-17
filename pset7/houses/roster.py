# TODO
from cs50 import SQL
from sys import argv, exit


# 第2參數為要查詢的房屋名稱
if len(argv) != 2:
    print("Miss command argv")
    exit(1)

db = SQL("sqlite:///students.db")
list = db.execute("SELECT * FROM students WHERE house = ? ORDER BY last, first;", argv[1])
for n in list:
    # 沒有中間名要去掉空白
    middle = " " + n["middle"] if n["middle"] else ""
    print(f"{n['first']}{middle} {n['last']}, born {n['birth']}")
