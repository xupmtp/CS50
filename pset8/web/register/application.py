from flask import Flask, render_template, redirect, request, session
from flask_session import Session
from cs50 import SQL

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

db = SQL("sqlite:///lecture.db")

@app.route("/")
def index():
    rows = db.execute("select * from registrants")
    return render_template("index.html", rows=rows)

@app.route("/register", methods=["GET", "POST"])
def register():
    if request.method == "GET":
        return render_template("register.html")
    else:

        name = request.form.get("name")
        if not name:
            return render_template("apology.html", message="You must provide name")
        email = request.form.get("email")
        if not email:
            return render_template("apology.html", message="You must provide email")
        db.execute("INSERT INTO registrants(name, mail) VALUES(:name, :mail);", name=name, mail=email)
        return redirect("/")