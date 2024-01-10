# Week 7 SQL
---
### Excel
* 資料庫是可以儲存資料的一種應用程式,試算表是最簡單的資料庫方式
* 下面我們建立一個表格蒐集人們最喜歡的電視劇,內容包含時間戳記,劇名及類型
![spreadsheet.png](https://cs50.harvard.edu/x/2020/notes/7/spreadsheet.png)
* 我們將這張表下載到程式目錄中並透過`favorites.py`存取
```python
import csv

counts = {}

with open("CS50 2019 - Lecture 7 - Favorite TV Shows (Responses) - Form Responses 1.csv", "r") as file:
		# 該方法讓我們可以用欄位標題取不同列的值
    reader = csv.DictReader(file)

		# 迭代每一行
    for row in reader:
        title = row["title"]
				
				# 計算每個標題出現次數
        if title in counts:
            counts[title] += 1
        else:
            counts[title] = 1

# 顯示統計結果
# items()返回list,其中每筆資料為元組,item[0]為key,item[1]為value
for title, count in counts.items():
    print(title, count, sep=" | ")
```

* 其中我們還可以對結果進行排序,默認使用key且按ASC||排序
* 我們可以自訂排序的方式
```python
# 自訂排序用的函數
# 這裡我們告訴它按item[1],也就是value來排序
def f(item):
  return item[1]

# key的值不是f(),f()使呼叫函數,f是將函數本身給key參數
# reverse=True 倒序
for title, count in sorted(counts.items(), key=f, reverse=True):
```

* 我們可以用以下語法來定義函數
```python
for title, count in sorted(counts.items(), key=lambda item: item[1], reverse=True):
```
* lambda為匿名函數,參數傳入item,返回的值為item[1]
* lambda只能是一行的函數

### SQL
* 我們開始使用新的程式`sqlite3`,這種程式可以透過名為SQL的語言進行操作
* 執行以下命令將CSV導入`favorate.db`

	
	REM 進入DB,若沒有則會建立同名DB
	~/ $ sqlite3 favorites.db
	SQLite version 3.22.0 2018-01-22 18:45:57
	Enter ".help" for usage hints.
	REM 設定輸出/輸入的檔案格式
	sqlite> .mode csv
	REM 從檔案匯入資料到表格中
	sqlite> .import "CS50 2019 - Lecture 7 - Favorite TV Shows (Responses) - Form Responses 1.csv" favorites

* 匯入檔案後,我們可以從cmmand line輸入SQL指令來獲得資料


	sqlite> SELECT title FROM favorites;
	title
	Dynasty
	The Office
	Blindspot
	24
	Friends
	psych
	Veep
	Survivor
	...
	
* 在處理數據時，我們僅需要四個操作：
	* `CREATE`
	* `READ`
	* `UPDATE`
	* `DELETE`
* SQL有自己的數據格式,用來優化儲存空間
* INTEGER(整數)
	* smallint
	* integer
	* bigint
* NUMERIC(數字)
	* boolean
	* date
	* datetime
	* numeric(scale,precision)，通過為小數點前後的每個數字使用所需的位數來解決浮點不精確性
	* time
	* timestamp
* REAL(浮點數)
	* real，用於浮點值
	* double precision，還有更多位
* TEXT(字符)
	* char(n)，用於確切的字符數
	* varchar(n)，對於可變數量的字符，最多可達到一定限制
	* text

### IMDb
* IMDb具有可[下載](https://www.imdb.com/interfaces/)的`.TSV`檔或以`|`分隔資料的資料集
* 例如，我們可以下載`title.basics.tsv.gz`，其中將包含有關影片標題的基本數據：
* `tconst`，每個標題的唯一標識符，例如 `tt4786824`
* `titleType`，標題的類型，例如 `tvSeries`
* `primaryTitle`，使用的主要標題，例如 `The Crown`
* `startYear`，標題發行的年份，例如 `2016`
* `genres`，以逗號分隔的流派列表，例如 `Drama,History`
* 我們利用cs50 lib 將這些資料insert進db內
```python
import cs50
import csv

# Create database by opening and closing an empty file first open(f"shows3.db", "w").close()
db = cs50.SQL("sqlite:///shows3.db")

# Create table called `shows`, and specify the columns we want, # all of which will be text except `startYear` 
db.execute("CREATE TABLE shows (tconst TEXT, primaryTitle TEXT, startYear NUMERIC, genres TEXT)")

# Open TSV file # https://datasets.imdbws.com/title.basics.tsv.gz 
with open("title.basics.tsv", "r") as titles:

    # Create DictReader     
		reader = csv.DictReader(titles, delimiter="\t")

    # Iterate over TSV file     
		for row in reader:

        # If non-adult TV show         
				if row["titleType"] == "tvSeries" and row["isAdult"] == "0":

            # If year not missing             
						if row["startYear"] != "\\N":

                # If since 1970                 
								startYear = int(row["startYear"])
                if startYear >= 1970:

                    # Insert show by substituting values into each ? placeholder                     
										db.execute("INSERT INTO shows (tconst, primaryTitle, startYear, genres) VALUES(?, ?, ?, ?)",
                               row["tconst"], row["primaryTitle"], startYear, genres)
```
* 透過每個資料的描述,我們可以建立如下結構的DB
![imdb_tables.png](https://cs50.harvard.edu/x/2020/notes/7/imdb_tables.png)
* 我們可以下載[DB Browser for SQLite]來更清楚的看見這些資料(https://sqlitebrowser.org/dl/)
* 每個表之間有一對多,多對多,一對一,等對應關係
* 表格欄位可以指定為特殊類型
	* `PRIMARY KEY`，用作行的主要標識符
	* `FOREIGN KEY`，它指向另一個表中的一行
	* `UNIQUE`，每個欄位值必須是唯一的
	* `INDEX`，它要求我們的DB創建一個索引以根據此列更快地進行查詢。索引是像樹一樣的數據結構，可幫助我們搜索值。
* 使用`CREATE INDEX idx_name ON stars (person_id);`創建索引,`person_id`會有名為`idx_name`的索引,正確建立索引可大幅提高查詢速度

### SQL問題
* SQL間存在**競爭條件**的問題,當有兩個動作對同一資料作操作時會引發錯誤
* 若是同時有兩組更新likes,可能會因為同時select到舊資料導制其中一組更新錯誤
```python
# 同時select到likes=2
rows = db.execute("SELECT likes FROM posts WHERE id=?", id);
likes = rows[0]["likes"]
# 兩組都更新2=>3,丟失一likes
db.execute("UPDATE posts SET likes = ?", likes + 1);
```
* 要解決此問題可以使用transactions,確保一組動作會同時發生才執行下一組
* 另一問題為**SQL injection attack**,攻擊者會透過在參數中加入SQL語句來對DB做攻擊,要確保此問題不會發生我們應該使用佔位符`?`,以自動轉義來自用戶的輸入