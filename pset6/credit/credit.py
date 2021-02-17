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
