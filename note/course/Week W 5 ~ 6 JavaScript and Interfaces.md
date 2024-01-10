# Week W 5 ~ 6 JavaScript and Interfaces
---
* 這兩個章節主要介紹JavaScript及利用前端三種語言製作出流暢的使用者介面

### DOM操作
* `document.querySelector()` 取得指定dom元素,取得方式同CSS,若有多個相同元素只會取第一個
* 若要取所有符合元素用`document.querySelectAll()`,並使用forEach遍歷內容
```js
let heading = document.querySelector('h1');

let headings = document.querySelectorAll('h1');

headings.forEach((dom) => {
	// dom元素可用的屬性同html
	dom.style.background = 'blue'
})
```
##### 綁定事件
綁定事件有3種主流的方法
1. 元素上綁定,將js方法加到html標籤的onclick屬性上
```html
<script>
	function hello() {
		alert('Hello')
	}
</script>
<button onclick="hello()">Click</button>
```
2. JS DOM元素綁定,在JS取得元素後添加onclick屬性(注意方法1是綁定結果hello(), 方法2是綁定函數本身hello)
```js
function hello() {
	alert('Hello')
}
let btn = document.querySelector('button')
btn.onclick = hello
```
3. 新增事件監聽器,使用元素的addEventListener方法綁定事件
```js
let btn = document.querySelector('button')
btn.addEventListener("click", hello)
```
* JS還有一些常用的事件如下
* `onclick`
* `onmouseover`
* `onkeydown`
* `onkeyup`
* `onload`
* `onblur`

##### 延遲載入
```html
<script>
	document.querySelector('button').onclick = count;
</script>
<button>Click</button>
```
* 上面這段程式會報`TypeError`,因為null沒有onclick屬性
* 會找不到button的原因是JS部分先執行了才載入DOM元素,我們可以在document加入監聽函數,該事件會等所有元素載入後才執行callback function
```js
// 所有元素載入後才執行function
document.addEventListener('DOMContentLoaded', function() {
    // 放入可能導致null的code
});
```
##### 自訂屬性
* 在HTML中提供自訂屬性的功能,只要在屬性前加`data-`就可以
* 在JS中可以使用`ele.dataset.YOURNAME`取得該屬性值
```html
<div data-color="blue"></div>
<script>
	let div = document.querySelector('div')
	// blue
	let color = div.dataset.color
</script>
```

### 重複執行
指定時間重複執行該函數
```js
let counter = 0;
            
function count() {
    counter++;
    document.querySelector('h1').innerHTML = counter;
}

document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('button').onclick = count;

    setInterval(count, 1000);
});
```

### Local Storage
儲存在client端的資料,每個用戶顯示的內容皆不相同
* `localStorage.getItem(key)` 根據key取得對應value
* `localStorage.setItem(key, value)` 設置指定的key及value,若已設置過該key則會覆蓋value

### Async
* 在ES6後我們使用fetch()方法取代原生的ajax用法,該方法擁有兩個參數,第一個事發送的url,第二個是物件,該物件包含發送所需的屬性
* 第二個參數用到的屬性可參考[Web APIs \| MDN](https://developer.mozilla.org/en-US/docs/Web/API/WindowOrWorkerGlobalScope/fetch)
```js
fetch('http://example.com/movies.json')
  .then(function(response) {
    return response.json();
  })
  .then(function(myJson) {
    console.log(myJson);
  });
```
* 第一個then的callback function會返回一個response的promise物件(參考[Response \| MDN](https://developer.mozilla.org/en-US/docs/Web/API/Response))
* 使用`json()`方法會返回一個解析為JS obj的promise物件(參考[Body.json() \| MDN](https://developer.mozilla.org/en-US/docs/Web/API/Body/json)),這時我們可以再用callback函數取的我們要的data

### User Interfaces
User Interfaces是使用者與頁面交互的方式,作為Web開發人員，我們的目標是使這些交互對用戶來說盡可能地令人愉悅

### Infinite Scroll
* 當使用者滾動到頁面底部時, 加載新內容到頁面中, 這種做法經常在社交軟體(如: fb, twitter)的動態牆看到
* 下面示範在Django應用中實現滾動載入
* 首先在後端加入模擬貼文回應的結果資料
```python
def posts(request):

    # 表示開始和結束的筆數
    start = int(request.GET.get("start") or 0)
    end = int(request.GET.get("end") or (start + 9))

    # 回傳的資料list
    data = []
    for i in range(start, end + 1):
        data.append(f"Post #{i}")

    # 人為延遲, 模擬正常網頁的情形
    time.sleep(1)

    # Return list of posts
    return JsonResponse({
        "posts": data
    })
```
* 現在前端接收到的資料應該會是一個json格式
```js
{
    "posts": [
        "Post #10",
        "Post #11", 
        "Post #12", 
        "Post #13", 
        "Post #14", 
        "Post #15"
    ]
}
```
* 接著加入`index.html`, 在此頁面中加載靜態的JS文件,其中`div`是加載所有文章的地方
```html
{% load static %}
<!DOCTYPE html>
<html>
    <head>
        <title>My Webpage</title>
        <style>
            .post {
                background-color: #77dd11;
                padding: 20px;
                margin: 10px;
            }

            body {
                padding-bottom: 50px;
            }
        </style>
        <script scr="{% static 'posts/script.js' %}"></script>
    </head>
    <body>
        <div id="posts">
        </div>
    </body>
</html>
```
* 最後在JS中,加入滾動視窗事件,每次滾動到底部時呼叫API加載新內容
```js
// 開始的文章ID
let counter = 1;

// 每次加載的數量
const quantity = 20;

// DOM載入完成後先載入開始的20個文章
document.addEventListener('DOMContentLoaded', load);

// 滾動到底部時加載新文章
window.onscroll = () => {
		// window.innerHeight取得client端螢幕的高度
		// window.scrollY為目前滾動的距離
		// document.body.offsetHeight是該document文件完整的高度(從頭滑動到底)
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight) {
        load();
    }
};

// 加載文章
function load() {

    // Set start and end post numbers, and update counter
    const start = counter;
    const end = start + quantity - 1;
    counter = end + 1;

    // 取得新文章並append到div
    fetch(`/posts?start=${start}&end=${end}`)
    .then(response => response.json())
    .then(data => {
        data.posts.forEach(add_post);
    })
};

// 建立新元素append到div中,該元素包含response的文章資料
function add_post(contents) {

    // Create new post
    const post = document.createElement('div');
    post.className = 'post';
    post.innerHTML = contents;

    // Add post to DOM
    document.querySelector('#posts').append(post);
};
```

### Animation
* 我們可以使用CSS添加動畫,這可以使我們的網站更加有趣
* 在CSS中建立Animation我們使用以下格式建立開始到結束時的元素樣式,其中動畫過程可以是開始結束(`from`&`to`)或是動畫階段(0%~100%),動畫的名稱自訂
```css
@keyframes MY_NAME {
	from {
		/* 開始樣式 */
	}
	to {
		/* 結束樣式 */
	}
}
```
或是
```css
@keyframes MY_NAME {
	0% {
		/* 開始樣式 */
	}
	50% {
		/* 動畫到50%時的樣式 */
	}
	100% {
		/* 結束樣式 */
	}
}
```
* 現在我們對`h1`標籤套用此動畫
```css
h1 {
		/* 套用的動畫名稱 */
		animation-name: MY_NAME;
		/* 動畫執行次數,若該值為infinite則不斷執行 */
		animation-iteration-count: 1
		/* 動畫持續秒數 */
		animation-duration: 2s;
		/* 動畫執行模式, forwards表示結束時元素停在100%的樣式 */
		animation-fill-mode: forwards;
		/* 動畫是否執行 paused為停止 */
		animation-play-state: running;
}
```
* 我們也可以使用JS控制動畫

```js
// 點擊button時動畫若為停止則啟動,若為啟動則停止
document.querySelector('button').onclick = () => {
			if (h1.style.animationPlayState == 'paused') {
					h1.style.animationPlayState = 'running';
			}
			else {
					h1.style.animationPlayState = 'paused';
			}
	}
```
