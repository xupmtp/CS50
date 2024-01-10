# [Problem Set 4](https://cs50.harvard.edu/x/2020/psets/4/)
---
### 1. [Filter less](https://cs50.harvard.edu/x/2020/psets/4/filter/less/)
##### 題目說明
讀取`.bmp`檔圖片,分別對其做灰階,棕色,左右顛倒,模糊等處理,處理的函數皆寫於`helper.c`檔案中
##### Answer
本題目只要實作`helper.c`程式即可

>如果要在程式中呼叫自訂方法只需在開頭定義讓程式知道,不須加在`helper.h`內
```c
#include "helpers.h"
#include <stdio.h>
#include <math.h>

//開頭呼叫讓程式知道
void test();

// 灰階
//將三種顏色加總後取平均,最後4捨5入
//注意數字至少需有1個為浮點數,否則除法後為整數
void grayscale(int height, int width, RGBTRIPLE image[height][width])
{
	for (int i = 0; i < height; i++)
	{
			for (int j = 0; j < width; j++)
			{
					int avg = round(((float)image[i][j].rgbtBlue + image[i][j].rgbtGreen + image[i][j].rgbtRed) / 3);
					image[i][j].rgbtBlue = avg;
					image[i][j].rgbtGreen = avg;
					image[i][j].rgbtRed = avg;
			}
	}
	printf("image already convert to grayscale\n");
	return;
}

// 棕褐色
//按照題目公式取3種顏色的新值
void sepia(int height, int width, RGBTRIPLE image[height][width])
{
	for (int i = 0; i < height; i++)
	{
			for (int j = 0; j < width; j++)
			{
					int r = image[i][j].rgbtRed;
					int g = image[i][j].rgbtGreen;
					int b = image[i][j].rgbtBlue;
					int convertR = round(0.393 * r + 0.769 * g + 0.189 * b);
					int convertG = round(0.349 * r + 0.686 * g + 0.168 * b);
					int convertB = round(0.272 * r + 0.534 * g + 0.131 * b);
					image[i][j].rgbtRed = convertR > 255 ? 255 : convertR;
					image[i][j].rgbtGreen = convertG > 255 ? 255 : convertG;
					image[i][j].rgbtBlue = convertB > 255 ? 255 : convertB;
			}
	}
	printf("image already convert to sepia\n");
	return;
}

// 水平翻轉
void reflect(int height, int width, RGBTRIPLE image[height][width])
{
	for (int i = 0; i < height; i++)
	{
			for (int j = 0, n = width / 2; j < n; j++)
			{
					RGBTRIPLE temp = image[i][j];
					image[i][j] = image[i][width - j - 1];
					image[i][width - j - 1] = temp;
			}
	}
	printf("image already convert to reflect\n");
	return;
}

// Blur image
//加總九宮格的值後取平均,不能直接覆蓋原本值否則下個元素平均會出錯
void blur(int height, int width, RGBTRIPLE image[height][width])
{
	//儲存平均後的值
	RGBTRIPLE res[height][width];
	//判斷是否到邊界
	int check[4] = {1, 0, 0, 1};//右左上下
	for (int i = 0; i < height; i++)
	{
			check[2] = i == 0 ? 0 : 1;
			check[3] = i == height - 1 ? 0 : 1;
			for (int j = 0; j < width; j++)
			{
					check[0] = j == width - 1 ? 0 : 1;
					check[1] = j == 0 ? 0 : 1;
					int sumr = 0;
					int sumg = 0;
					int sumb = 0;
					int count = 0;

					//九宮格加總後取平均
					for (int k = i - check[2]; k <= i + check[3]; k++)
					{
							for (int n = j - check[1]; n <= j + check[0]; n++)
							{
									sumr += image[k][n].rgbtRed;
									sumg += image[k][n].rgbtGreen;
									sumb += image[k][n].rgbtBlue;
									count++;
							}
					}
					res[i][j].rgbtRed = round((float)sumr / count);
					res[i][j].rgbtGreen = round((float)sumg / count);
					res[i][j].rgbtBlue = round((float)sumb / count);
			}
	}
	//覆蓋原本圖片的像素
	for (int i = 0; i < height; i++)
	{
			for (int j = 0; j < width; j++)
			{
					image[i][j] = res[i][j];
			}
	}
	printf("image already convert to blur\n");
	return;
}

//假如有自訂的fun
void test()
{
    printf("aaaaa");
}
```

### 2. [Filter more](https://cs50.harvard.edu/x/2020/psets/4/filter/more/)
##### 題目說明
承1. 新增**邊緣**,邊緣的做法是採用影像辨識的初階技術[Sobel operator](https://zh.wikipedia.org/wiki/%E7%B4%A2%E8%B2%9D%E7%88%BE%E7%AE%97%E5%AD%90),實際做法參考[矩陣卷積](https://www.itread01.com/content/1541912176.html),此題使用橫向的Gx矩陣與直向的Gy矩陣,並依以下公式計算出該像素的值(結果超過255以255來計算)
$$\sqrt{Gx^2+Gy^2}$$

>Gx/Gy的值越大或越小代表該像素與周圍的像素顏色相差越多
##### Answer
灰階,反射,模糊的做法和第一題相同,此處略過不列出

```c
#include "helpers.h"
#include <stdio.h>
#include <math.h>

//讓程式認得下面的函數
RGBTRIPLE getResult(int i, int j, int check[4], int width, RGBTRIPLE image[][width]);

// Detect edges
void edges(int height, int width, RGBTRIPLE image[height][width])
{
    RGBTRIPLE res[height][width];
		//判斷九宮格,同blur做法
    int check[4] = {1, 0, 0, 1};//右左上下

    for (int i = 0; i < height; i++)
    {
        check[2] = i == 0 ? 0 : 1;
        check[3] = i == height - 1 ? 0 : 1;
        for (int j = 0; j < width; j++)
        {
            check[0] = j == width - 1 ? 0 : 1;
            check[1] = j == 0 ? 0 : 1;

						//取得計算後的新像素值
            res[i][j] = getResult(i, j, check, width, image);
        }
    }

    for (int i = 0; i < height; i++)
    {
        for (int j = 0; j < width; j++)
        {
            image[i][j] = res[i][j];
        }
    }
    printf("image already convert to edges\n");
    return;
}

RGBTRIPLE getResult(int i, int j, int check[4], int width, RGBTRIPLE image[][width])
{
    //矩陣存r, g, b
		//sumx,sumy分別代表Gx,Gy的計算總值
    int sumx[3] = {0, 0, 0};
    int sumy[3] = {0, 0, 0};
    RGBTRIPLE res;

		//x, y卷積矩陣
    int gx[3][3] = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    int gy[3][3] = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

		//對應矩陣值相乘後加總
    for (int k = i - check[2], x = 1 - check[2]; k <= i + check[3]; k++, x++)
    {
        for (int n = j - check[1], y = 1 - check[1]; n <= j + check[0]; n++, y++)
        {
            sumx[0] += image[k][n].rgbtRed * gx[x][y];
            sumx[1] += image[k][n].rgbtGreen * gx[x][y];
            sumx[2] += image[k][n].rgbtBlue * gx[x][y];

            sumy[0] += image[k][n].rgbtRed * gy[x][y];
            sumy[1] += image[k][n].rgbtGreen * gy[x][y];
            sumy[2] += image[k][n].rgbtBlue * gy[x][y];
        }
    }
		
		//計算平方根後四捨五入
    int sqr = round(sqrt(sumx[0] * sumx[0] + sumy[0] * sumy[0]));
    int sqg = round(sqrt(sumx[1] * sumx[1] + sumy[1] * sumy[1]));
    int sqb = round(sqrt(sumx[2] * sumx[2] + sumy[2] * sumy[2]));

		//結果值超過255以255計算
    res.rgbtRed = sqr > 255 ? 255 : sqr;
    res.rgbtGreen = sqg > 255 ? 255 : sqg;
    res.rgbtBlue = sqb > 255 ? 255 : sqb;

    return res;
}
```

### 3. [recover](https://cs50.harvard.edu/x/2020/psets/4/recover/)

##### 題目說明
給一損壞的`.raw`檔(raw檔常用於數碼相機的儲存),裡面包含50張JPG圖片,題目要求必須將檔案中的所有`.jpg`檔案取出後並輸出,題目按以下要求完成
>檔案依[FAT32文件系统](https://blog.csdn.net/u010650845/article/details/60881687)儲存,所以1MB的檔案在SD卡上面占用1048576 ÷ 512 = 2048塊空間
* 每次讀取512bytes,當讀取bytes不到512代表到達檔案結尾
* 依照數碼相機儲存方式,每個jpg檔案間沒有多餘字元,所以一個jpg檔的開頭代表上個jpg的結尾
* jpg開頭為`0xff`,`0xd8`,`0xff`,第4個byte可以是`0xe0`~`0xef`

>檔案中出現0 byte的地方是製造商將所有閒置空間都以0填充

##### Answer

```c
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[])
{
		//argv[1]為壓縮檔路徑
    char *arg1 = argv[1];
    FILE *file = fopen(arg1, "r");
    if (file == NULL)
    {
        printf("no file\n");
        return 1;
    }
		//每次只讀512bytes
    unsigned char bytes[512];
		
		//下面變數皆用於存jpg檔
    char *filename = malloc(10 * sizeof(char));
    int n = 0;
    FILE *img;

		//fread()每次呼叫會往下讀取指定字節直到結束
    //若fread() return不到512代表檔案到結尾
    while (fread(bytes, 1, 512, file) == 512)
    {
        //判斷是JPG檔時開新的檔案儲存bytes
				//bytes[3]開頭前4位都必須是'1110',所以所們判斷時可忽略最後幾位
        if (bytes[0] == 0xff && bytes[1] == 0xd8 && bytes[2] == 0xff && (bytes[3] & 0xf0) == 0xe0)
        {
            //將字串設給filename變數,檔名從000.jpg開始
            sprintf(filename, "%03i.jpg", n++);
            //打開新的檔案
            img = fopen(filename, "w");
        }
        //寫入byte給空白檔案直到JPG結束
        fwrite(bytes, 1, 512, img);
    }
    //close file 後開先關
    fclose(img);
    free(filename);
    fclose(file);
}
```