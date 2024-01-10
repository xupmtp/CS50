# Week W 4 SQL, Models, and Migrations
---
### Django Models
* Django Models是在SQL之上的抽象模型,他讓我們能使用class和objects操作database而不是使用SQL
* 下面我們建立一airline專案並為其增加database來操作資料
* 首先建立專案及航班APP,記得在`urls.py`和`settings.py`加入專案設定
```python
django-admin startproject airline
cd airline
python manage.py startapp flights
```
* 現在我們要加入models來操作data,開啟app folder中的`models.py`,這個檔案儲存數據相關class,呼叫特定方法後Django便會對DB發出SQL命令
```python
# 建立繼承models.Model的class就相當於在DB中建立table,我們需指定欄位的型態及大小
class Flight(models.Model):
    origin = models.CharField(max_length=64)
    destination = models.CharField(max_length=64)
    duration = models.IntegerField()
```

### Migrations
* 現在我們建立了DB相關class, 但我們還沒有關聯到DB中,先執行以下命令
```python
python manage.py makemigrations
```
* 上面命令會建立新文件在`flights/migrations/`下,這些文件將會建立或編輯DB,接下來我們要將這些migrations應用到DB
```python
python manage.py migrate
```
* 現在所有DB操作都已經應用到`db.sqlite3`這個DB檔中了

### Shell
* 現在開始向DB新增/修改數據,首先進入Django的Shell,在這裡可以執行python命令
```python
python manage.py shell
```
* 接著試著新增一筆資料
```python
# Import our flight model
In [1]: from flights.models import Flight

# Create a new flight
In [2]: f = Flight(origin="New York", destination="London", duration=415)

# Instert that flight into our database
In [3]: f.save()

# Query for all flights stored in the database
In [4]: Flight.objects.all()
Out[4]: <QuerySet [<Flight: Flight object (1)>]>
```
* 查詢結果只會顯示`Flight object`,我們須修改Flights class的tostring方法
```python
class Flight(models.Model):
    origin = models.CharField(max_length=64)
    destination = models.CharField(max_length=64)
    duration = models.IntegerField()

    def __str__(self):
        return f"{self.id}: {self.origin} to {self.destination}"
```
* 現在印出的內容更有可讀性了,我們還可以用`first()`取得第一筆,每筆資料可以使用屬性名稱取值
```python
# Create a variable called flights to store the results of a query
In [7]: flights = Flight.objects.all()

# Displaying all flights
In [8]: flights
Out[8]: <QuerySet [<Flight: 1: New York to London>]>

# Isolating just the first flight
In [9]: flight = flights.first()

# Printing flight information
In [10]: flight
Out[10]: <Flight: 1: New York to London>

# Display flight id
In [11]: flight.id
Out[11]: 1
```

### Foreign Key
* 接著建立Airport class,該class儲存城市與其對應的機場代號
* 這裡我們要修改Flight的欄位origin和destination,將他們改為外來鍵並對應到Airport table
```python
class Airport(models.Model):
    code = models.CharField(max_length=3)
    city = models.CharField(max_length=64)

    def __str__(self):
        return f"{self.city} ({self.code})"

class Flight(models.Model):
		# ForeignKey() 參數分別為: 對應的類別, 關聯模式, 關聯的名稱
    origin = models.ForeignKey(Airport, on_delete=models.CASCADE, related_name="departures")
    destination = models.ForeignKey(Airport, on_delete=models.CASCADE, related_name="arrivals")
    duration = models.IntegerField()

    def __str__(self):
        return f"{self.id}: {self.origin} to {self.destination}"
```
* 每次更動models結構時要重新執行migration命令
* 若之前有資料在table可能會導致結構更新失敗,此時應先刪除資料再更新
* 下面實際演示外鍵關聯下的數據關係
```python
# Import all models
In [1]: from flights.models import *

# Create some new airports
In [2]: jfk = Airport(code="JFK", city="New York")
In [4]: lhr = Airport(code="LHR", city="London")
In [6]: cdg = Airport(code="CDG", city="Paris")
In [9]: nrt = Airport(code="NRT", city="Tokyo")

# Save the airports to the database
In [3]: jfk.save()
In [5]: lhr.save()
In [8]: cdg.save()
In [10]: nrt.save()

# Add a flight and save it to the database
f = Flight(origin=jfk, destination=lhr, duration=414)
f.save()

# 可以看到origin變為Airport物件
In [14]: f
Out[14]: <Flight: 1: New York (JFK) to London (LHR)>
In [15]: f.origin
Out[15]: <Airport: New York (JFK)>
In [15]: f.origin.code
Out[15]: JFK

# 在ForeignKey設置的related_name用途是從Airport中取得所有對應的Flight物件
# 這裡從lhr取得所有destination值為"LHR"的Flight物件
In [17]: lhr.arrivals.all()
Out[17]: <QuerySet [<Flight: 1: New York (JFK) to London (LHR)>]>
```

### Django Admin
* 開發人員要管理DB需要經常使用Shell,操作上不直覺且繁瑣,於是Django提供圖形化介面來簡化流程
* 使用前，首先要建立用來管理的管理者帳號
```python
python manage.py createsuperuser
```
* 接著在App的`admin.py`內註冊要管理的table class
```python
from django.contrib import admin
from .models import Flight, Airport

# Register your models here.
admin.site.register(Flight)
admin.site.register(Airport)
```
* 然後訪問`/admin`輸入帳密進入管理頁面
* 進來後可以在flights App的地方看到剛剛註冊的兩個table,點進後可以編輯我們的DB資料

### Many-to-Many 
* 在1對1或1對多的情形下我們使用ForeignKey來管理對應關係,而多對多的情形下則使用ManyToManyField
```python
class Passenger(models.Model):
    first = models.CharField(max_length=64)
    last = models.CharField(max_length=64)
    flights = models.ManyToManyField(Flight, blank=True, related_name="passengers")

    def __str__(self):
        return f"{self.first} {self.last}"
```
* `Flight`表示要對應的table class
* `blank=True`表示該乘客可以沒有對應的航班
* 謹記修改完後要對db進行搬移
* 現在我們可以在`views.py`中加入一個url根據航班ID取的所有對應的乘客資訊
```python
def flight(request, flight_id):
    flight = Flight.objects.get(id=flight_id)
		# passengers對應related_name的值
    passengers = flight.passengers.all()
    return render(request, "flights/flight.html", {
        "flight": flight,
        "passengers": passengers
    })
```

### Users
* Django中內建了對用戶身分驗證的機制,只要在login時呼叫Django的方法即可驗證登入,不需另外撰寫相關程式

##### 驗證是否登入
* 首先我們到ADMIN頁面加入一些用戶
* 接著在後端要驗證的方法內加入以下程式
```python
# 方法1 呼叫request.user.is_authenticated
def index(request):
    # If no user is signed in, return to login page:
    if not request.user.is_authenticated:
        return HttpResponseRedirect(reverse("login"))
    return render(request, "users/user.html")
		
# 方法2 加入@login_required註釋
@login_required
def index(request):
    return render(request, "users/user.html")
```
##### 註冊
```python
def register(request):
		# 前端送出註冊表單
    if request.method == "POST":
        username = request.POST["username"]
        email = request.POST["email"]

        # Ensure password matches confirmation
        password = request.POST["password"]
        confirmation = request.POST["confirmation"]
        if password != confirmation:
            return render(request, "auctions/register.html", {
                "message": "Passwords must match."
            })

        # 若有重複username會IntegrityError報錯
        try:
            user = User.objects.create_user(username, email, password)
						# save實際儲存資料
            user.save()
        except IntegrityError:
            return render(request, "auctions/register.html", {
                "message": "Username already taken."
            })
				# 註冊完應該是登入狀態
        login(request, user)
        return HttpResponseRedirect(reverse("index"))
    else:
        return render(request, "auctions/register.html")
```

##### 登入
* 登入時須先呼叫authenticate()確認帳密是否正確接著再登入
```python
from django.contrib.auth import authenticate, login, logout

def login_view(request):
    if request.method == "POST":
        # Accessing username and password from form data
        username = request.POST["username"]
        password = request.POST["password"]

        # Check if username and password are correct, returning User object if so
        user = authenticate(request, username=username, password=password)

        # If user object is returned, log in and route to index page:
        if user:
            login(request, user)
            return HttpResponseRedirect(reverse("index"))
        # Otherwise, return login page again with new context
        else:
            return render(request, "users/login.html", {
                "message": "Invalid Credentials"
            })
    return render(request, "users/login.html")
```


##### 登出
* 要登出時也是呼叫Django提供的方法
```python
def logout_view(request):
		# 呼叫logout()便會登出
    logout(request)
    return render(request, "users/login.html", {
                "message": "Logged Out"
            })
```
