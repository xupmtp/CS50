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
