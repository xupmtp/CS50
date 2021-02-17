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

