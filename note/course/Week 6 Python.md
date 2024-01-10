# Week 6 Python
---
這周開始介紹新的程式語言python,python是一門功能強大且簡便的語言

>要記住這門課程的目標是學習編程的方法而非特定的程式語言

更多內容參考python[官方文檔](https://docs.python.org/3/)
### Python基礎
##### 打印console
`end`參數為結尾符號,默認`\n`
```python
print("hello world" end="")
```
變數可以直接用於字串內
```python
name = "Mike"
# 字串前要加"f"
print(f"hello {name}")
```

##### 獲取使用者輸入
```python
name = input("Name: ")
print("hello, " + name)
```
##### 條件式
```python
x, y = 3, 4
if x < y:
    print("x is less than y")
elif x > y:
    print("x is greater than y")
else:
    print("x is equal to y")
```
##### 迴圈
```python
# while
while True:
	print("cough")

# for 遍歷陣列
# range(n)生成陣列取代遍歷的陣列
for i in [0, 1, 2]:
    print("cough")
```
##### 基本數據類型
* `bool`，`True`或`False`
* `float`，實數
* `int`，整數
* `str`，字符串
* `range`，數字序列
* `list`，可變值的序列，我們可以更改，添加或刪除
* `tuple`，我們不能更改的不可變值序列
* `dict`，鍵/值對的集合，例如哈希表
* `set`，唯一值的收集

### 一些例子
##### 模糊影像
```python
from PIL import Image, ImageFilter

before = Image.open("bridge.bmp")
after = before.filter(ImageFilter.BLUR)
after.save("out.bmp")
```
##### 更簡潔的輸出
```python
print("Moo~\n" * 3)
```
##### 慣例寫法
若直接在第3行呼叫`cough()`而沒有寫在方法內,會報錯找不到函數
所以一般慣例會將主程式寫於一`main()`方法內,最後再呼叫它
```python
def main():
    for i in range(3):
        cough()

def cough():
    print("cough")

main()
```
##### stack overflow
* 以下這段程式並不會導致int溢位,因為python內整數並無上限值(適合作大數據)

* 此段程式會執行直到耗盡該計算機記憶體空間為止

```python
from time import sleep

i = 1
while True:
    print(i)
    sleep(1)
    i *= 2
```

##### 命令行參數
可獲取執行時的參數(同C語言),參數以空格隔開

	python sys.py arg1 arg2
```python
from sys import argv

for arg in argv:
    print(arg)
```

##### 退出程式
```python
from sys import argv, exit

if len(argv) != 2:
    print("missing command-line argument")
    exit(1)
print(f"hello, {argv[1]}")
exit(0)
```

##### 字串
python可以用`==`進行字串比較
```python
a = "asdf"
b = "ddd"
print(a == b)
```
複製字串時也可以直接用`=`複製
```python
s = "asdf"

t = s

t = t.capitalize()

print(f"s: {s}")
print(f"t: {t}")
```
##### 交換變數
不必再新增一臨時變數
```python
x = 1
y = 2

print(f"x is {x}, y is {y}")
x, y = y, x
print(f"x is {x}, y is {y}")
```

### 檔案I/O
##### 打開一個CSV檔
```python
import csv

# "a"為寫入檔案
file = open("phonebook.csv", "a")

name = input("Name: ")
number = input("Number: ")

# 建立writer物件
writer = csv.writer(file)
# 用writer物件寫入CSV檔
writer.writerow((name, number))

file.close()
```

##### 更簡化的寫法
* `with`關鍵字會自動幫我們關閉文件
```python
...前略
with open("phonebook.csv", "a") as file:
    writer = csv.writer(file)
    writer.writerow((name, number))
```

### 其他新功能
#### 正則表達式
* `.`，對於任何字符
* `.*`，用於0個或更多字符
* `.+`，用於1個或更多字符
* `?`，可選
* `^`，用於開始輸入
* `$`，用於輸入結束

使用`re` library用於正則表達式
`search()`方法返回是否有匹配的內容
```python
import re

s = input("Do you agree?\n")

# 第三參數用於指定特殊匹配模式,re.IGNORECASE表示忽略單字大小寫
if re.search("^y(es)?$", s, re.IGNORECASE):
    print("Agreed.")
elif re.search("^no?$", s, re.IGNORECASE):
    print("Not agreed.")
```

##### 語音辨識
我們可以下載名為`speech_recognition`的library,它可以接收音頻並將它轉換為文字
```python
import speech_recognition

# 獲得音頻
recognizer = speech_recognition.Recognizer()
with speech_recognition.Microphone() as source:
    print("Say something!")
    audio = recognizer.listen(source)

print("Google Speech Recognition thinks you said:")
# 取得音頻文字
words = recognizer.recognize_google(audio)

# Respond to speech if "hello" in words:
    print("Hello to you too!")
elif "how are you" in words:
    print("I am well, thanks!")
elif "goodbye" in words:
    print("Goodbye to you too!")
else:
    print("Huh?")
```

##### 影像辨識
執行[detect.py和recognize.py](https://cdn.cs50.net/2019/fall/lectures/6/src6/6/faces/)
* `recognize.py`可以在多人照片中找到David在哪裡
* `detect.py`將擷取所有人臉部份的圖像

##### QC Code
我們可以利用`qrcode` library以網址生成qrcode的PNG圖檔
```python
# Generates a QR code
# https://github.com/lincolnloop/python-qrcode

import qrcode

# Generate QR code
img = qrcode.make("https://youtu.be/oHg5SJYRHA0")

# Save as file
img.save("qr.png", "PNG")
```