from flask import Flask, render_template, redirect, request, session
from flask_mail import Mail, Message

app = Flask(__name__)
app.config["MAIL_DEFAULT_SENDER"] = "simoniii10539@gmail.com"
app.config["MAIL_PASSWORD"] = "simon0369"
app.config["MAIL_PORT"] = 587
app.config["MAIL_SERVER"] = "smtp.gmail.com"
app.config["MAIL_USE_TLS"] = True
app.config["MAIL_USERNAME"] = "simoniii10539@gmail.com"
app.config["MAIL_SUPPRESS_SEND"] = False
app.config["MAIL_DEBUG "] = True

# 將設定的變數傳給mail函數, 此物件用於發送mail
mail = Mail(app)

@app.route("/", methods=["GET", "POST"])
def index():
    if request.method == "POST":
        email = request.form.get("email")
        print(email)
        message = Message("Hi", recipients=["simon850602@gmal"])
        print("send mail start!")
        mail.send(message)
        print("send seccess!")
    return render_template("index.html")

@app.route("/send")
def send():
    email = request.args.get("email")
    print(email)
    message = Message("Hi", recipients=["simon850602@gmail.com"])
    print("send mail start!")
    mail.send(message)
    print("send seccess!")
    return render_template("index.html")