# Week W 3 Django
---
### 建立Django專案
##### 安裝模組

	$ pip3 install Django

##### 建立新專案

	$ django-admin startproject PROJECT_NAME

##### 啟動server

	$ python manage.py runserver
	
### 建立子應用程式
* 若要開發大型專案，其中會有許多不同不功能如:買家 購物 賣家，若所有功能都在同一程式中實現會導致修改及維護的問題
* Django提供方法創建其他子應用程式，讓我們可以將不同的功能拆成各個子程序，之後不管是要新增/修改還是移除該功能都可以輕鬆達成
* 這些子程序透過主項目的文件來管理
	* `setting.py` 管理所有子程序，新程序要在**INSTALLED_APPS**變數中註冊(不需要時可快速移除)
	* `urls.py` 管理應用程式URL，將`views.py`的方法mapping到指定URL，專案下有一個管理所有URL的`urls.py`，我們也可以在每個子項目中新增`urls.py`管理該子項目URL，這麼做可以在維護時更快找到對應URL

##### 建立新應用程式

	$ python manage.py startapp APP_NAME
	
##### 新增URL回傳內容
* `views.py` 負責決定回傳的內容是什麼(同flask的app.py)
```python
from django.http import HttpResponse

# index函數回傳Hello字串,request沒用到也需加
def index(request):
    return HttpResponse('Hello!')
def simon(request):
    return HttpResponse('Hello Simon!')
```

* `urls.py` 將url關連到`views.py`指定的function

hello/urls
```python
from django.urls import path
# . 表示從當前目錄引用views.py
from . import views

urlpatterns = [
		# 表示此url顯示views.py中index函數回傳的內容
    path('', views.index, name='index')
		path('simon', views.simon, name='simon')
]
```
* 因為我們在hello內新增了`urls.py`,所有我們要在主應用程式的`urls.py`註冊該urls

myapp/urls.py
```python
from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),
    # hello.urls表示在hello模組中獲取urls檔案
    # 下面這段表示將hello.urls內的url都接在hello/之後
    path('hello/', include('hello.urls'))
]
```
* 現在我們啟動server,開啟browser輸入`127.0.0.1:8000/hello`會顯示"Hello!",輸入`127.0.0.1:8000/hello/simon`會顯示"Hello Simon!"

### 使用url做為參數
* 我們可以傳入url的值給我們的函數使用
* 首先在`hello/urls.py`加入一段path,url部分表示我們將這段url做為color變數傳給views.color函數
```python
path('<str:color>', views.color, name='color')
```
* 然後在`views.py`中新增函數, 第二個參數名稱`color`需和`<str:color>`中的變數名一樣
```python
def color(request, color):
    return HttpResponse(f'My favorite color is {color}!')
```
* 接著輸入`127.0.0.1:8000/hello/red`會看到"My favorite color is red"
* 在url`hello/`底下未註冊的url都會導入此頁面並根據url動態顯示內容

>只有`hello/`下一層的url會導入該頁面,若url為`hello/yellow/red`還是返回404

### 渲染HTML
* 現在我們要將回傳的內容從字串改為html,讓我們的專案更符合一般網站
* 所有的html模板必須放於`templates/`目錄下
* 在專案下可能有多個應用程序,為了避免有相同名稱的html文件互相衝突,我們會在`templates/`下建一個與應用程序名稱相同的目錄並將文件放在該目錄下,EX:hello程序會有`templates/hello/index.html`
* 在`views.py`中使用`render()`函數返回模板路徑

```python
from django.shortcuts import render

def index(request):
    # 避免路徑衝突 templates多建一hello目錄
    return render(request, 'hello/index.html')
```

### 模板語言
* 在Django中前端使用Jinja來取得資料
* 用法和Flask相同,這裡不多介紹,詳細用法看[Week 8 Web Flask](:note:4e2cd6dd-dc58-4608-8f2d-6de4b5c251c2)

### 傳遞資料
* views函數回傳render()函數時,新增第三參數,值為dict型別
```python
def index(request):
    # 回傳隨機數1或2
    return render(request, 'tasks/index.html', {
        'num': random.randint(1, 2),
        'tasks': [111, 222, 444, 1234]
    })
```
* 在html可以用變數 if..else..的方式使用該值,若回傳是陣列則可以使用for loop遍歷所有值
```html
<body>
    <h1>random number is {{ num }}</h1>
    {% if num == 1 %}
        <h2>Yes</h2>
    {% else %}
        <h2>No</h2>
    {% endif %}
    <h1>show my tasks</h1>
    <ul>
        {% for task in tasks %}
            <li>{{ task }}</li>
<!--        迴圈為空執行這段-->
        {% empty %}
            <li>No task</li>
        {% endfor %}
    </ul>
</body>
```

### 靜態資源
* 所有的靜態文件必須放在`static/`目錄中,和html文件一樣,為了避免路徑衝突,我們在static下新增和應用程序相同名稱的目錄EX:在tasks程序下新增`static/tasks/index.css`
* 在html中,要加載靜態資源須在文件頂部加入`{% load static %}`

```html
{% load static %}

<!DOCTYPE html>
...
```
* 要加入靜態資源URL時,我們使用django的特殊語法,未來即使目錄結構要更改也不需重新修改URL

```html
<link href="{% static 'tasks/index.css' %}" rel="stylesheet">
```

### Forms
#### 添加連結
* 要跳轉到應用程式的其他頁面使用django的url語法
```html
<a href="{% url 'add' %}">Add a New Task</a>
```
* 該url的值是我們在`urls.py`中設定path()時給的name參數值
* 若有多個應用程式有相同的名稱`add`,URL可能會導向錯誤的頁面,所以我們給該應用程序一個名稱
```python
# 設置url程序名稱
app_name = "tasks"
urlpatterns = [
    path("", views.index, name="index"),
    path("add", views.add, name="add")
]
```
* 然後我們在連結上加上app名稱,這樣便可避免url衝突
```html
<a href="{% url 'tasks:add' %}">Add a New Task</a>
```
* 現在我們幫form表單加上送出的url
```html
<form action="{% url 'tasks:add' %}" method="post">
```
* 為了防止其他網站的使用者發出[跨站請求偽造（CSRF）攻擊](https://portswigger.net/web-security/csrf),django提供了CSRF token,該token對應每個新的session都是唯一的,提交form時django會檢查該token與最近提供的token是否相同
* 要在form表單內加入token須加上`{% csrf_token %}`,該語法會自動產生包含token的hidden欄位
```html
<form action="{% url 'tasks:add' %}" method="post">
    {% csrf_token %}
    <input type="text", name="task">
    <input type="submit">
</form>
```
* 下面我們透過更簡單的方式建立form, 我們將使用django提供的[Forms API](https://docs.djangoproject.com/en/3.0/ref/forms/api/)
* 首先在views.py建立一class
```python
from django import forms


class NewForm(forms.Form):
		# 建立字串欄位
    tasks = forms.CharField(label='New task')
		# 建立數字欄位
    num = forms.IntegerField(label='Num', min_value=1, max_value=10)
		# 建立不同type char欄位
		msg = forms.CharField(widget=forms.Textarea)
```
* NewForm class繼承了forms.Form類別,該類別提供許多方法建立我們所需的表單欄位
```python
def add(request):
    return render(request, 'tasks/add.html', {
        'form': NewForm()
    })
```
* 回傳資料加入類別,在表單中只要呼叫該值(如下所示)便會自動建立對應欄位
```html
 <form action="{% url 'tasks:add' %}" method="post">
        {% csrf_token %}
        {{ form }}
        <input type="submit">
</form>
```
* 欄位方法指定`label`值會在input元素前加入label,值為參數值,用以提示使用者該欄位是什麼
* 其他欄位可指定不同值用以驗證表單,如:min_value/max_value指定欄位上下限,前端會在html標籤上加入驗證,後端我們可以用`form.is_valid()`驗證這些內容(同時驗證前後端可減少系統出錯的機率)
* 上面我們已經成功建立表單欄位,現在我們要將資料傳送到後端處理
```python
from django.urls import reverse
from django.http import HttpResponseRedirect


def add(request):
    if request.method == 'POST':
        # 將表單送出的資料儲存到class
        form = NewForm(request.POST)
        # 根據我們class的設定自動驗證資料(server端)
        if form.is_valid():
            # 和class屬性作區別 form.task取得的是'CharField' cleaned_data才會取得資料
            task = form.cleaned_data['task']
            # 加入代辦清單
            tasks.append(task)
            # 重新導向待辦頁 reverse()可以根據url name找到該url,之後修改url時只要name不變就不需更動其他程式
            return HttpResponseRedirect(reverse('tasks:index'))
    else:
        return render(request, 'tasks/add.html', {
            'form': NewForm()
        })
```
* 成功提交後我們用HttpResponseRedirect()將頁面導向待辦頁避免重複提交,reverse()函數意思類似`{% url 'tasks:add' %}`,透過name名稱自動找到url

### Sessions
* session是每個用戶訪問網站時取得唯一數據的方式
* 在django中使用session前必須先決定session儲存的位置,默認是以table的方式,但目前DB中沒有這些table,所以我們執行`python manage.py migrate`建立這些表
* 然後修改原本使用`tasks`list的地方,改為調用session
```python
def index(request):
    if 'tasks' not in request.session:
        request.session['tasks'] = []

    return render(request, 'tasks/index.html', {
        'num': random.randint(1, 2),
        'tasks': request.session['tasks']
    })
# def add....
	# 加入代辦清單
	request.session['tasks'] += [task]
```
