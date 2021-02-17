# TODO
import csv
from cs50 import SQL
from sys import argv, exit


# 第2參數為csv檔名
if len(argv) != 2:
    print("Miss command argv")
    exit(1)

with open(argv[1], "r") as file:
    read = csv.DictReader(file)
    db = SQL("sqlite:///students.db")
    for f in read:
        name = f["name"].split(" ")
        # 若沒有中間名db欄位存入null
        middle = name[1] if len(name) == 3 else None
        last = name[2] if len(name) == 3 else name[1]
        db.execute("""INSERT INTO students
        (first, middle, last, house, birth) VALUES
        (?, ?, ?, ?, ?)""", name[0], middle, last, f["house"], f["birth"])