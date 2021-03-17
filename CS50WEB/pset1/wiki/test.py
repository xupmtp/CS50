with open(r'C:\Users\simon\Desktop\import.sql', 'w') as file:
    for i in range(50000):
        file.write(f"INSERT INTO IMAGEDETAIL03(workno, businessno, codeno, caseno, fc, qc, indexfield01) "
                   f"VALUES('01','01','01','{str(i+1).rjust(5, '0')}','Y','QC', '測試資料');\n")
