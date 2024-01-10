# Week 8 Web Flask
---
>此筆記包含CS50X2019 Week 8 Web 及CS50X2020 Week 9 Flask內容

在Web中,我們將利用**CSS+HTML+JavaScript**打造客戶端內容

在伺服器端則使用**Flask+SQLite**來處理資料

最終將打造一個擁有註冊/登入以及買/賣股票功能的web網站

因其他內容以有經驗所以下面主要介紹python的flask框架如何使用

### Flask
flask是一款輕量級的web框架,並且擁有許多強大的提供使用

這裡使用CS50 IDE上提供的flask環境,若要自行安裝及啟動falsk server請參考[官方文件](https://flask.palletsprojects.com/en/1.1.x/)

### 建立flask應用程式
專案目錄結構
* `application.py`為主要應用程式,啟動server需在此目錄下執行指令
* `requirements.txt`定義其他用到的library
* `static/`除了html外的靜態資源
* `templates/`放置html模板, 模板將作為最終的顯示畫面

	App/
	|__application.py 
	|__ requirements.txt
	|__ static/
	    |__img/
	    |__js/
	    |__css/
	|__templates/
	    |__index.html
	    |__xxx.html

```python
from flask import Flask
# 參數值__name__表示此文件為flask應用程式
app = Flask(__name__)

@app.route('/')
def hello_world():
		# 返回'Hello, World!'字串
    return 'Hello, World!'
```
最後執行`flask run`便可以看到`Hello, World!`

### 模板及傳值
* 接著我們要將回傳的字串替換為html模板,讓我們的網站可以顯示html內容
* 並且,我們還要試著將參數從伺服器傳遞給html模板使用
* **在模板中使用雙大括號來操作資料的是一種名為[Jinja](https://jinja.palletsprojects.com/en/2.11.x/)的模板語言**

application.py
```python
# 使用模板需import render_template
from flask import Flask, render_template

app = Flask(__name__)

@app.route("/")
def index():
		list = ["Simon", "Emma", "John"]
		# 第一參數為模板路徑,之後參數為要回傳的變數名稱及值
    return render_template("index.html", name="Mike", names=list)
```
* 在html中接收伺服器傳來的變數需使用`{{ parameter }}`包起,中間為變數名稱
* 使用if條件時以`{% if expression %}`開頭,結尾加上`{% endif %}`表結束位置
* 使用for回圈時以`{% for n in names %}`開頭,結尾為`{% endfor %}`,中間內容會執行迴圈的次數

index.html
```html
<!DOCTYPE html>

<html lang="en">
    <head>
        <title>Index</title>
    </head>
    <body>
		<!-- 執行結果為 "Hello Mike" -->
		<h1>Hello {{ name }}</h1>
		
		<!--條件成立時執行<h3>行,否則執行<h4>行 -->
		{% if name == 'Mike' %}
		<h3>Hi i am jack</h3>
		{% else %}
		<h4>Hi Jack</h4>
		<!--表示if範圍結束 -->
		{% endif %}
		
		<ul>
			<!-- 加入迴圈次數的<li>內容 -->
			{% for n in names %}
					<li>{{ n }}</li>
			{% endfor %}
    </ul>
    </body>
</html>
```

### 表單GET/POST
* 不同的請求方法就像是不同的路線, 即使URL相同也可以做不同的事
* 重複使用相同的URL可以減少大量URL時產生的混亂
* 在http協議中,`GET`通常用於向server取得資料(select),`POST`則是向server發送資料(update, insert)
* 這是一種默認的協議,雖然並未強制要求遵守,但開發時傾向使用準則來做開發標準
* html表單中,默認提交為`GET`
* 在python中, 取得`GET`參數使用`request.args.get("pname")`, 取得`POST`參數使用`request.form.get("pname")`, 如果name包含多個資料(例如複選框)則`get("")`方法改為`getlist("")`

```python
# 要取得request物件import request
# 設定轉向頁面import redirect
from flask import Flask, render_template, redirect, request

# methods參數指定此url可以傳入方法種類,默認只有get
@app.route("/register", methods=["GET", "POST"])
def register():
    if request.method == "GET":
				# 取得網址列?後面的參數值使用args
				name_get = request.args.get("name")
				# get方法單純做傳值,不做其他事
        return render_template("show.html", name=name)
    else:
				# form用來取post的表單內容
        name = request.form.get("name")
				# post方法通常會傳入或更新某些資料
        db.execute("INSERT INTO ....")
				# 重新導向到首頁
        return redirect("/")
```

#### 表單驗證
* 在送出表單時, 我們可能不希望使用者傳送一些非法資料到server,例如不在select選單中的選項, 這時我們可以將限定的資料(EX:select選項)從後端傳給前端顯示,當使用者傳送資料到server時,只要與原本server內的資料相互驗證即可避免非法手段入侵網站

### Session
* `Session`負責紀錄在server端上的使用者訊息,接著產生一組對應的 ID，存入 cookie 後傳回用戶端
* `Cookie`是瀏覽器存放資料的地方，可以存放任何大小不超過4096字節(不同瀏覽器可能有差)的資料,常用於存放session回傳id
* 下面演示將表單資料存入session來個別化顯示資料
```python
# 取得session字典需import session
from flask import Flask, render_template, redirect, request, session

# 啟用session
from flask_session import Session


app = Flask(__name__)
# 設置session不要永久存在
app.config["SESSION_PERMANENT"] = False

# 設置session相關數據儲存位置在flask應用程式的文件內
# 默認為null表示儲存在cookie中
# 其他值參考 https://www.cnblogs.com/cwp-bg/p/9339865.html
app.config["SESSION_TYPE"] = "filesystem"

# 為特定網站啟用session, 這裡為我們的flask網站
# 啟用後我們可以透過字典的方式來取得特定用戶的session內容
Session(app)

# 顯示session儲存的代辦事項內容
@app.route("/")
def index():
    # session為儲存session內容的字典
    if "todos" not in session:
        session["todos"] = []
    # 每個使用者存的session皆不同,所以session回傳的內容也不同
    return render_template("index.html", todos=session["todos"])

# 這裡將表單資料儲存進session
@app.route("/add", methods=["GET", "POST"])
def register():
    if request.method == "GET":
        return render_template("add.html")
    else:
        todo = request.form.get("task")
        session["todos"].append(todo)
        return redirect("/")
```

### 模板系統
* 在flask中內建了模板系統
* 通過創建/引用模板,可以大幅減少相同程式碼的撰寫
* 下面建立一個名為`layout.html`的模板,`{% block body %}`表示此部分html由子模板插入內容,`{% endblock %}`為結束位置
* `{% block style %}`表示此部分css內容由子模板插入,此標籤可包含`<style>`tag也可不包含
```html
<!DOCTYPE html>

<html lang="en">
    <head>
				<!-- 表示此頁面使用unicode編碼 -->
				<meta charset="utf-8">
        <title>Index</title>
				<style>
					{% block style %}
					{% endblock %}
				</style>
    </head>
    <body>
        {% block body %}
        {% endblock %}
    </body>
</html>
```
* 接著建立一個表單網頁,並且將`layout.html`的內容引入來取代部分內容
```html
<!-- 宣告此html使用layout.html的內容 -->
{% extends "layout.html" %}

<!-- 在此html中自行擴展的style內容 -->
{% block style %}
body
{
	color:blue;
}
{% endblock %}
<!-- 在此html中自行擴展的body內容 -->
{% block body %}
        <h1>Register</h1>
        <form action="/add" method="post">
            <input type="text" name="task" placeholder="task"/><br>
            <input type="submit" />
        </form>
{% endblock %}
```

### 發送Mail
* 在flask中有提供發送Email的功能, 只要提供相關訊息便可輕鬆發送email給使用者
* 為了避免個資洩漏, 隱私訊息使用`os.getenv()`從環境變數中取得, 在cs50 ide中使用 `export path_name=path_val`指令在cmd中設置環境變數
* **作為寄件	的gmail帳號必須要完成前置作業, 參考[使用 Python Flask 完成寄信功能](https://github.com/twtrubiks/Flask-Mail-example)**

>目前在CS50 IDE中發送mail會得到`OSError: [Errno 99] Cannot assign requested address`錯誤, 但在local環境中可以正常發送, 此問題尚無解決
```python
import os

from flask import Flask, render_template, request
from flask_mail import Mail, Message

app = Flask(__name__)
# os.getenv("path name")取得環境變數值
# 沒指定寄件人時預設寄件人
app.config["MAIL_DEFAULT_SENDER"] = os.getenv("MAIL_DEFAULT_SENDER")
# 寄件人密碼
app.config["MAIL_PASSWORD"] = os.getenv("MAIL_PASSWORD")
# Mail server port
app.config["MAIL_PORT"] = 587
# 指定gamil server為寄件server
app.config["MAIL_SERVER"] = "smtp.gmail.com"
# 使用TLS加密方式
app.config["MAIL_USE_TLS"] = True
# 寄件人帳號
app.config["MAIL_USERNAME"] = os.getenv("MAIL_DEFAULT_SENDER")
# 指定為True時不會發送mail
app.config["MAIL_SUPPRESS_SEND"] = False
# 顯示錯誤訊息
app.config["MAIL_DEBUG "] = True
# 將設定的變數傳給mail函數, 此物件用於發送mail
mail = Mail(app)

@app.route("/")
def send():
    # 第一參數為title, html為內文, recipients指定收件人
    message = Message("Hi", html="<h1>Test my mail</h1>", recipients=["simon850602@gmail.com"])
    # 發送mail
    mail.send(message)
    return render_template("index.html")
```

### AJAX
* ajax是一門利用javascript來實現網址呼叫的技術, 利用js呼叫網址後, 我們會在所謂的callback函數中使用回傳的內容
* callback函數是當網址回應內容後才執行的函數, 因為internet可能會有連線慢甚至失去連線的情形, 所以只要等網址回應才執行callback函數便不會導致後面的JS程式無法執行, 也就是所謂的**阻塞**