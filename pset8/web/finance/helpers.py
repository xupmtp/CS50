import os
import requests
import urllib.parse
import datetime
import pytz

from flask import redirect, render_template, request, session
from functools import wraps


# 參考API用法, 返回錯誤頁面, 並生成錯誤圖片
def apology(message, code=400):
    """Render message as an apology to user."""
    def escape(s):
        """
        Escape special characters.

        https://github.com/jacebrowning/memegen#special-characters
        """
        for old, new in [("-", "--"), (" ", "-"), ("_", "__"), ("?", "~q"),
                         ("%", "~p"), ("#", "~h"), ("/", "~s"), ("\"", "''")]:
            s = s.replace(old, new)
        return s
    return render_template("apology.html", top=code, bottom=escape(message)), code


# 登入裝飾器, 這裡覆寫@login_required裝飾器的函數
def login_required(f):
    """
    Decorate routes to require login.

    http://flask.pocoo.org/docs/1.0/patterns/viewdecorators/
    """
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if session.get("user_id") is None:
            return redirect("/login")
        return f(*args, **kwargs)
    return decorated_function


# 從API取得股票資訊
def lookup(symbol):
    """Look up quote for symbol."""

    # Contact API
    try:
        api_key = os.environ.get("API_KEY")
        response = requests.get(f"https://cloud-sse.iexapis.com/stable/stock/{urllib.parse.quote_plus(symbol)}/quote?token={api_key}")
        response.raise_for_status()
    except requests.RequestException:
        return None

    # Parse response
    try:
        quote = response.json()
        return {
            "name": quote["companyName"],
            "price": float(quote["latestPrice"]),
            "symbol": quote["symbol"]
        }
    except (KeyError, TypeError, ValueError):
        return None


# 金額format
def usd(value):
    """Format value as USD."""
    return f"${value:,.2f}"

# 取得現在TW時間
def get_tw_timezone():
    tw = pytz.timezone(u'Asia/Taipei')
    return datetime.datetime.now(tw).strftime("%Y-%m-%d %H:%M:%S")
