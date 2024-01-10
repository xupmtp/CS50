# [Problem Set 1](https://cs50.harvard.edu/x/2020/psets/1/)
---
### 1. [Hello](https://cs50.harvard.edu/x/2020/psets/1/hello/)
##### 題目說明
印出"hello world"

此題是為了熟悉cs50 ide,故不須太多程式碼

##### Answer
```c
#include <stdio.h>

int main(void)
{
    printf("hello, world\n");
}
```
### 2-1. [Mario less](https://cs50.harvard.edu/x/2020/psets/1/mario/less/)
##### 題目說明
印出如下三角形,高度由使用者輸入且需介於1~8間,若輸入不符合條件則重新跳出輸入框

	       #
	      ##
	     ###
	    ####
	   #####
	  ######
	 #######
	########
##### Answer
```c
#include <cs50.h>
#include <stdio.h>

void mario(int n);

int main(void)
{
    int height = get_int("Height:");
    while (height < 1 || height > 8)
    {
        height = get_int("Height:");
    }
    mario(height);
}
void mario(int n)
{
    for(int (int i = 1; i <= n; i++)
    {
        for (int j = n; j >= 1; j--)
        {
            if (j > i)
            {
                printf(" ");
            }
            else
            {
                printf("#");
            }
        }
        printf("\n");
    }
}
```
### 2-2. [Mario more](https://cs50.harvard.edu/x/2020/psets/1/mario/more/)
##### 題目說明
承2.1,印出如下圖形,條件同2.1

	       #  #
	      ##  ##
	     ###  ###
	    ####  ####
	   #####  #####
	  ######  ######
	 #######  #######
	########  ########

##### Answer
```c
  
#include <cs50.h>
#include <stdio.h>

int main(void)
{
 int h = get_int("Height:");
 while(h <= 0 || h > 8){
     h = get_int("Height:");
 }
 for(int i=1;i<=h;i++) {
	for(int j=h;j>=1;j--) {
		if(j>i)printf(" ");
		else printf("#");
	}
	printf("  ");
	for(int j=1;j<=h;j++) {
		if(j<=i)printf("#");
	}
	printf("\n");
  }
}
```
### 3-1. [Cash](https://cs50.harvard.edu/x/2020/psets/1/cash/)
##### 題目說明
給定4種硬幣大小,25美分、10美分、5美分、1美分,今天用戶輸入一金額,求此金額最少須準備多少硬幣,以下幾點條件須注意
1. 用戶輸入為美元,所以會有小數點
2. 小數點2位後4捨5入
3. 若輸入為負數則讓用戶重新輸入
4. 輸入數值必小於float值域

>註:此題有說明可使用貪心算法

##### Answer
```c
#include<cs50.h>
#include<stdio.h>
#include<math.h>

int main(void)
{
    float money;
    do{
        money = get_float("Change owed:");
    }
    while(money<0);
    int cents = round(money * 100);
    int count = 0;
    if(cents/25>0){
        count += cents/25;
        cents %= 25;
    }
    if(cents/10>0){
        count += cents/10;
         cents %= 10;
    }
     if(cents/5>0){
        count += cents/5;
         cents %= 5;
    }
     if(cents>0){
        count += cents;
    }
    printf("%i\n",count);
}
```
### 3-2. [Credit](https://cs50.harvard.edu/x/2020/psets/1/credit/)
##### 題目說明
根據問題中所給的條件,依魯恩算法計算出此卡號是否合法,並判斷出此卡可能屬於哪間公司出品

##### Answer

```c
#include<cs50.h>
#include<stdio.h>

int main(void){
 int len= 0;
 int startWith = 0;
 int sum = 0;
 long number;
	do{
		number = get_long("Number:");  
	}while(number<0);
	//魯恩算法
	while(number!=0){
			len++;
			if(number>=10&&number<100){
				 startWith=number; 
			}
			if(len%2==0){
					int n = (number%10) * 2;
					n=n>=10?n%10+n/10:n;
					sum+=n;
			}else {
					sum+=number%10;
			}
			number/=10;
	}
	//判斷屬於哪間公司,都不符合則非法
	if(sum%10==0){//判斷魯恩算法結果合法
			if((startWith==34||startWith==37)&&len==15){
					printf("AMEX\n");
			}else if(startWith>=51&&startWith<=55&&len==16){
					printf("MASTERCARD\n");
			}else if(startWith/10==4&&(len==13||len==16)){
					printf("VISA\n");
			}else{
					printf("INVALID\n");
			}
	}else{
			printf("INVALID\n");
	}
}
```