# [Problem Set 6](https://cs50.harvard.edu/x/2020/psets/6/)
---
### 1. [Hello](https://cs50.harvard.edu/x/2020/psets/6/hello/)
##### 題目說明
印出python版本"hello world"
同pset1
##### Answer
```python
name = input("What is your name?\n")
print(f"hello, {name}")
```

### 2. [Mario less](https://cs50.harvard.edu/x/2020/psets/6/mario/less/)
##### 題目說明
印出python版本單邊三角形
同pset1
##### Answer
```python
# 不符合規則要不斷執行
while True:
    try:
        height = int(input("Height: "))
        # 不在範圍內或非數字則丟出exception
        if 8 >= height > 0:
            break
        else:
            raise TypeError()
    except:
        continue

# join()用於連接list內元素,前面字串為串接符號
for i in range(height, 0, -1):
    print("".join([" " if j < i else "#" for j in range(1, height + 1)]))
```

### 2. [Mario more](https://cs50.harvard.edu/x/2020/psets/6/mario/more/)
##### 題目說明
印出python版本金字塔
同pset1
##### Answer
```python
# 不符合規則須不斷執行
while True:
    try:
        height = int(input("Height: "))
        # 不在範圍內或非數字則丟出exception
        if 8 >= height > 0:
            break
        else:
            raise TypeError()
    except:
        continue

# join()用於連接list內元素,前面字串為串接符號
for i in range(height, 0, -1):
		# 左邊三角形
    list = [" " if j < i else "#" for j in range(1, height + 1)]
		# 空格 + 右邊三角形
    list += ["  "] + ["#" for j in range(height - i + 1)]
    print("".join(list))
```
### 3. [Cash](https://cs50.harvard.edu/x/2020/psets/6/cash/)

##### 題目說明
找出使用的硬幣數目,同pset1

##### Answer
```python
while True:
    try:
        dollors = float(input("Change owed: "))
        # 不在範圍內或非數字則丟出exception
        if dollors >= 0:
            break
        else:
            raise TypeError()
    except:
        continue

dollors *= 100
count = 0
if dollors >= 25:
    count += dollors // 25
    dollors %= 25
if dollors >= 10:
    count += dollors // 10
    dollors %= 10
if dollors >= 5:
    count += dollors // 5
    dollors %= 5
if dollors >= 1:
    count += dollors // 1

print(int(count))
```

### 4. [Credit](https://cs50.harvard.edu/x/2020/psets/6/credit/)

##### 題目說明
確認卡號是否合法,同pset1

##### Answer
```python
while True:
    try:
        number = int(input("Number: "))
        # 不在範圍內或非數字則丟出exception
        break
    except:
        continue

len, startWith, s = 0, 0, 0
# 魯恩算法
while number != 0:
    len += 1
    if number >= 10 and number < 100:
        startWith = number
    if len % 2 == 0:
        n = (number % 10) * 2
        n = n % 10 + n // 10 if n >= 10 else n
        s += n
    else:
        s += number % 10
    number //= 10

# 判斷魯恩算法結果合法
if s % 10 == 0:
    if (startWith == 34 or startWith == 37) and len == 15:
        print("AMEX")
    elif 55 >= startWith >= 51 and len == 16:
        print("MASTERCARD")
    elif startWith // 10 == 4 and (len == 13 or len == 16):
        print("VISA")
    else:
        print("INVALID")

else:
    print("INVALID")
```

### 5. [Readability](https://cs50.harvard.edu/x/2020/psets/6/readability/)

##### 題目說明
可讀性,同pset2

##### Answer
```python
import math


def main():
    letters, words, sentences = 0, 1, 0
    sentence_test = ["!", ".", "?"]

    text = input("Text: ")
    for i in text:
        letters += 1 if i.isalpha() else 0
        words += 1 if i.isspace() else 0
        sentences += 1 if i in sentence_test else 0

    print(letters, words, sentences)
    w_by_l = (letters / words) * 100
    sentence_by_words = (sentences / words) * 100
    index = (0.0588 * w_by_l) - (0.296 * sentence_by_words) - 15.8
		# 四捨五入結果
    index = round(index)
    print_grade(index)


def print_grade(index):
    if index < 1:
        print("Before Grade 1")
    elif index >= 16:
        print("Grade 16+")
    else:
        print(f"Grade {index}")


main()
```

### 6. [DNA](https://cs50.harvard.edu/x/2020/psets/6/dna/)

##### 題目說明
DNA中存在多個核甘酸,每個核甘酸均包含4種鹼基:腺嘌呤（A），胞嘧啶（C），鳥嘌呤（G）或胸腺嘧啶（T）

由多個鹼基串聯並在特定位置連續多次重複我們稱為**短串聯重複序列（STR）**

今天給定每個人的**STR**次數(.CSV),找到此DNA(.txt)是屬於誰的


##### Answer
計算DNA中每組STR的數目,比較是否有人符合該DNA,沒有則返回"No match"

```python
from sys import argv, exit
import csv
import re

# 參數需有 dna.py xxx.csv xx.txt
if len(argv) != 3:
    print("missing command-line argument")
    exit(1)

# 存姓名對應的STR陣列
names = {}
with open(argv[1], "r") as cfile:
    rows = csv.reader(cfile)
    for i, r in enumerate(rows):
        if i == 0:
            strs = r[1:]
        else:
            names[r[0]] = r[1:]


with open(argv[2], "r") as tfile:
    dna = tfile.read()
    # 存DB欄位中的STR最大連續數目
    get_count = []
    for i in strs:
        count = 0
        s = i
        # 每次增加i字串一次直到找不到該總和字串
        while re.search(s, dna):
            count += 1
            s += i
        get_count.append(count)

    # csv存字串所以這裡要轉字串再比
    get_count = list(map(lambda x: str(x), get_count))
    for k, v in names.items():
        # 所有項目相同即為該人員DNA
        if get_count == v:
            print(k)
            exit(0)
    print("No match")

```