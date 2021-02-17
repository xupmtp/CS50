import os

from cs50 import SQL
from flask import Flask, flash, jsonify, redirect, render_template, request, session
from flask_session import Session
from tempfile import mkdtemp
from werkzeug.exceptions import default_exceptions, HTTPException, InternalServerError
from werkzeug.security import check_password_hash, generate_password_hash
import datetime
import pytz

from helpers import apology, login_required, lookup, usd, get_tw_timezone


# Configure application
app = Flask(__name__)

# Ensure templates are auto-reloaded
app.config["TEMPLATES_AUTO_RELOAD"] = True

# Ensure responses aren't cached
@app.after_request
def after_request(response):
    response.headers["Cache-Control"] = "no-cache, no-store, must-revalidate"
    response.headers["Expires"] = 0
    response.headers["Pragma"] = "no-cache"
    return response

# Custom filter
app.jinja_env.filters["usd"] = usd

# Configure session to use filesystem (instead of signed cookies)
app.config["SESSION_FILE_DIR"] = mkdtemp()
app.config["SESSION_PERMANENT"] = False
app.config["SESSION_TYPE"] = "filesystem"
Session(app)

# Configure CS50 Library to use SQLite database
db = SQL("sqlite:///finance.db")

# Make sure API key is set
# export API_KEY=pk_2e6d9ee055b0473a8e0c655fa05c446c
# os.environ.get() 獲取環境變量值
if not os.environ.get("API_KEY"):
    raise RuntimeError("API_KEY not set")


# 有加@login_required的URL,當未登入使用者到該URL時會強制跳到登入頁
@app.route("/")
@login_required
def index():
    """首頁 顯示所有已擁有股票及帳戶資訊"""
    shares_rows = db.execute("SELECT * FROM holdStock WHERE userid=:userid and shares > 0",
            userid=session.get("user_id"))
    user_rows = db.execute("SELECT * FROM users WHERE id=:userid",
            userid=session.get("user_id"))

    shares = []
    shares_total = float(user_rows[0]["cash"])

    # 加總所有股票金額(每個股票價格須抓API)
    for stock in shares_rows:
        sinfo = lookup(stock["symbol"])
        if not sinfo:
            return apology("Ooops! It's a magic error")
        sinfo["shares"] = stock["shares"]
        sinfo["total"] = int(stock["shares"]) * sinfo["price"]
        shares_total += sinfo["total"]
        sinfo["price"] = usd(sinfo["price"])
        sinfo["total"] = usd(sinfo["total"])
        shares.append(sinfo)

    user_rows[0]["total"] = usd(shares_total)
    user_rows[0]["cash"] = usd(user_rows[0]["cash"])

    return render_template("index.html", shares=shares, user=user_rows[0])


@app.route("/buy", methods=["GET", "POST"])
@login_required
def buy():
    """購買股票"""
    if request.method == "POST":
        symbol = request.form.get("symbol")
        try:
            shares = int(request.form.get("shares"))
        except:
            return apology("shares need to more than 1")
        userid = session.get('user_id')
        if not symbol:
            return apology("must provide symbol")
        stock = lookup(symbol)
        if not stock:
            return render_template("buy.html", errorMsg="INVALID SYMBOL")

        # 確認餘額是否足夠
        user = db.execute("SELECT * FROM users WHERE id = :id", id=userid);
        total = float(stock["price"]) * shares
        if user[0]["cash"] < total:
            return render_template("buy.html", errorMsg="You don't have sufficient balance.")

        # 更新帳戶金額
        db.execute("UPDATE users SET cash=:cash WHERE id = :id", cash=user[0]["cash"] - total, id=userid);


        # 帳號無此股票則新增,否則更新(原數量+買進量)
        db.execute("""INSERT OR REPLACE INTO holdStock(userid, symbol, shares)
        VALUES(:userid, :symbol,
        CASE WHEN (SELECT COUNT(1) FROM holdStock WHERE userid=:userid AND symbol=:symbol) > 0
        THEN (SELECT shares FROM holdStock WHERE userid=:userid AND symbol=:symbol)
        ELSE 0 END + :shares)""",
                    userid=userid,
                    symbol=stock["symbol"],
                    shares=shares)

        # 新增歷史訊息
        db.execute("""INSERT INTO history(userid, symbol, price, shares, transtime)
        VALUES(:userid, :symbol, :price, :shares, :date)""",
                    userid=userid,
                    symbol=stock["symbol"],
                    price=float(stock["price"]),
                    shares=shares,
                    date=get_tw_timezone())
        return redirect("/")
    return render_template("buy.html")


@app.route("/history")
@login_required
def history():
    """從history表抓出歷史訊息"""

    def f(s):
        s["price"]=usd(s["price"])
        return s
    rows = db.execute("SELECT * FROM history WHERE userid=:userid", userid=session.get("user_id"))
    rows = list(map(f, rows))

    return render_template("history.html", shares=rows)


@app.route("/login", methods=["GET", "POST"])
def login():
    """Log user in"""

    # Forget any user_id
    session.clear()

    # User reached route via POST (as by submitting a form via POST)
    if request.method == "POST":

        # Ensure username was submitted
        if not request.form.get("username"):
            return apology("must provide username", 403)

        # Ensure password was submitted
        elif not request.form.get("password"):
            return apology("must provide password", 403)

        # Query database for username
        rows = db.execute("SELECT * FROM users WHERE username = :username",
                          username=request.form.get("username"))

        # Ensure username exists and password is correct
        if len(rows) != 1 or not check_password_hash(rows[0]["hash"], request.form.get("password")):
            return apology("invalid username and/or password", 403)

        # Remember which user has logged in
        session["user_id"] = rows[0]["id"]

        # Redirect user to home page
        return redirect("/")

    # User reached route via GET (as by clicking a link or via redirect)
    else:
        return render_template("login.html")


@app.route("/logout")
def logout():
    """Log user out"""

    # Forget any user_id
    session.clear()

    # Redirect user to login form
    return redirect("/")


@app.route("/quote", methods=["GET", "POST"])
@login_required
def quote():
    """
    查看股票頁面
    為了頁面美觀, 股票資訊以modal方式顯示
    """

    return render_template("quote.html")


@app.route("/getStock")
def getStock():
    """供AJAX抓取指定股票資訊"""
    stock = lookup(request.args.get("symbol"))
    if not stock:
        return apology("INVALID SYMBOL")
    return jsonify(detail=f'A share of {stock["name"]}. ({stock["symbol"]}) costs {usd(stock["price"])} .')

@app.route("/register", methods=["GET", "POST"])
def register():
    """註冊 帳號"""

    if request.method == "POST":
        # Ensure username was submitted
        username = request.form.get("username")
        if not username:
            return render_template("register.html", errorMsg="must provide username")

        # Ensure password was submitted
        elif not request.form.get("password"):
            return render_template("register.html", errorMsg="must provide password")

        # Query database for username
        rows = db.execute("SELECT * FROM users WHERE username = :username",
                          username=username)

        # Ensure username exists and password is correct
        # 帳號已註冊時返回錯誤訊息
        if len(rows) >= 1:
            return render_template("register.html", errorMsg="username has already exist")

        # Insert new user to users
        # 密碼寫入前要用generate_password_hash()轉為hasah值
        db.execute("INSERT INTO users(username, hash) VALUES(:username, :hash_pwd)",
                          username=username,
                          hash_pwd=generate_password_hash(request.form.get("password")))
        session["user_id"] = username
        return redirect("/")

    return render_template("register.html")


@app.route("/sell", methods=["GET", "POST"])
@login_required
def sell():
    """賣出股票"""

    # GET時此頁面要顯示所有擁有股票
    # DB股票數為0時不刪除
    shares_rows = db.execute("SELECT * FROM holdStock WHERE userid=:userid and shares > 0",
                userid=session.get("user_id"))

    if request.method == "POST":
        symbol = lookup(request.form.get("symbol"))
        shares = int(request.form.get("shares"))
        userid =  session.get("user_id")
        if not symbol:
            return apology("Error!", 500)
        if not shares:
            return apology("must provide shares")

        # 若剩餘數量足夠就減去賣出量
        res = db.execute("""UPDATE holdStock set shares =
        (SELECT shares FROM holdStock WHERE  userid=:userid AND symbol=:symbol) - :shares
        WHERE userid=:userid AND symbol=:symbol AND shares >= :shares""",
        userid=userid,
        symbol=symbol["symbol"],
        shares=shares)
        if res == 0:
            return render_template("sell.html", errorMsg="Too Many Shares", shares=shares_rows)

        #  更新帳戶餘額
        db.execute("""UPDATE users set cash=
        (SELECT cash FROM users WHERE id=:userid) + :cash WHERE id=:userid""",
        cash=symbol["price"] * shares,
        userid=userid)

        # 新增歷史訊息
        db.execute("""INSERT INTO history(userid, symbol, price, shares, transtime)
        VALUES(:userid, :symbol, :price, :shares, :date)""",
                    userid=userid,
                    symbol=symbol["symbol"],
                    price=float(symbol["price"]),
                    shares=0 - shares,
                    date=get_tw_timezone)

        return redirect("/")

    return render_template("sell.html", shares=shares_rows)

@app.route("/apology")
@login_required
def apologyPage():
    """前端AJAX有錯時導入錯誤頁面"""
    return apology(request.args.get("errmsg"))


@app.route("/reset", methods=["GET", "POST"])
@login_required
def reset_password():
    """重設密碼"""

    if request.method == "POST":
        # Ensure username was submitted
        opwd = request.form.get("old_password")
        npwd = request.form.get("new_password")
        if not opwd:
            return render_template("reset.html", errorMsg="must provide old password")

        # Ensure password was submitted
        elif not npwd:
            return render_template("reset.html", errorMsg="must provide new password")

        # update時要判斷舊密碼是否正確
        rows = db.execute("SELECT * FROM users WHERE id = :uid",
                                  uid=session.get("user_id"))

        # check_password_hash()驗證字串生成hash後是否和傳入hash相同
        # 不可用generate_password_hash()來比較兩hash值
        if not check_password_hash(rows[0]["hash"], opwd):
            return render_template("reset.html", errorMsg="INVAILD OLD PASSWORD")

        # 相同密碼每次generate_password_hash()產生結果仍然不同
        res = db.execute("UPDATE users SET hash=:npwd WHERE id=:uid",
        npwd=generate_password_hash(npwd),
        uid=session.get("user_id"))

        session.clear()
        return render_template("login.html")
    return render_template("reset.html")

@app.route("/add", methods=["GET", "POST"])
@login_required
def add_cash():
    """新增金錢"""

    if request.method == "POST":
        cash = request.form.get("cash")
        if not cash:
            return render_template("add.html", errorMsg="must provide cash")

        db.execute("UPDATE users SET cash=(SELECT cash FROM users WHERE id = :uid) + :cash WHERE id=:uid",
        uid=session.get("user_id"),
        cash=cash)
        return redirect("/")

    return render_template("add.html")

def errorhandler(e):
    """Handle error"""
    if not isinstance(e, HTTPException):
        e = InternalServerError()
    return apology(e.name, e.code)


# Listen for errors
for code in default_exceptions:
    app.errorhandler(code)(errorhandler)
