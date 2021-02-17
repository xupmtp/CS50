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
    print("".join([" " if j < i else "#" for j in range(1, height + 1)])