# [Problem Set 7](https://cs50.harvard.edu/x/2020/psets/7/)
---

### 1. [Movies](https://cs50.harvard.edu/x/2020/psets/7/movies/)
##### 題目說明
依題目說明完成13個SQL語句

##### Answer
1
```sql
SELECT title
FROM movies
WHERE year = 2008;
```
2
```sql
select birth 
from people 
where name = "Emma Stone";
```
3
```sql
select title 
from movies 
where year >= 2018 
order by title;
```
4
```sql
select count(*) as countr 
from ratings 
where rating = 10.0;
```
5
```sql
select title, year 
from movies 
where title like "Harry Potter%" 
order by year;
```
6
```sql
select avg(rating) avg_rating 
from ratings 
where movie_id in 
(select id from movies where year = 2012);
```
7
```sql
select m.title, r.rating
from movies as m
join ratings as r
on m.id = r.movie_id
where m.year = 2010
order by r.rating desc, m.title;
```
8
```sql
select p.name from stars as s
join people as p
on s.person_id = p.id
where movie_id in 
(select id from movies where title = "Toy Story");
```
9
```sql
select name from people where id in
(select person_id from stars where movie_id in
(SELECT id FROM  movies
WHERE year =  2004))
order by birth;
```
10
```sql
select name from people where id in
(select person_id from directors where movie_id in
(select movie_id from ratings where rating >=9));
```
11
```sql
select m.title from ratings as r
left outer join movies as m
on r.movie_id = m.id
where r.movie_id in
(select movie_id from stars where person_id =
(select id from people where name = "Chadwick Boseman"))
order by r.rating desc  limit 5;

```
12
```sql
select title from movies where id in
(select movie_id from stars where person_id =
(select id from people where name = "Johnny Depp")
--聯集兩結果,只取重複值
intersect
select movie_id from stars where person_id =
(select id from people where name = "Helena Bonham Carter"))
```
13
```sql
select name from  people where id in
(select person_id from stars where movie_id in
(select movie_id from stars where person_id =
(select id from people where name = "Kevin Bacon" and birth = 1958))
and person_id !=(select id from people where name = "Kevin Bacon" and birth = 1958));
```

### 2. [Houses](https://cs50.harvard.edu/x/2020/psets/7/houses/)
##### 題目說明
* `import.py`匯入csv資料到db內
* `roster.py`console顯示特定房屋的查詢結果
##### Answer
* import.py
```python
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
```

* roster.py
```python
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
```