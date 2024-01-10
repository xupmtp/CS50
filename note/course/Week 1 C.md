# Week 1 C
---
### 使用工具
IDE:CS50線上IDE
語言:C語言
### C語言
檔名結尾為`.c`
```c
//import相關類別包

//取得使用者輸入等相關包
#include<cs50.h>
//printf相關包
#include<stdio.h>

//方法寫在main之後會找不到,在前面呼叫一次讓main方法可以找到
void mow(int n);
//主要程式執行
int main(void){
	mow(3);
	//讓使用者輸入內容,回傳字串
	string name = get_string("What is your name?");
	//讓使用者輸入內容(限整數數字)
	int age = get_int("Age:");
	
	printf("name:%s,age:%i\n",name,age);
}

//方法函數
void mow(int n){
	for(int i=0;i<n;i++){
		printf("mow~ mow~");
	}
}
```
##### 佔位符
c語言若要print數字等非字串資料時,需使用格式化輸入EX:"`printf("%s",123);`

以下每種型別對應佔位符號
|符號|型別|
--|--|
%s|string
%i|int
%li|long
%f|float,double
%c|char
%p|address(打印地址位置)


### commend line in IDE
在CS50線上IDE的console中可輸入cmd指令,其中cmd為模擬LINUX系統下的指令
##### 一般指令
$表示這是一行指令
`$ls`:看當前目錄下的所有檔案,檔名後有`*`的表示是可執行檔

`$rm 檔名`:刪除檔名,執行指令後會問是否刪除,可輸入y或yes刪除檔案

`$pwd`:顯示當前目錄的完整目錄

`$cd 目錄名`:跳至該目錄,直接執行`$cd`可跳回原始目錄

`Ctrl`+`R`:開啟歷史紀錄,可用上下鍵跳網之前的命令並用`Enter`來執行

`Ctrl`+`L`/`K`:清除終端,`L`為向下滾動到最新行,還可用查看歷史看之前的內容,`K`為完全清除

`Ctrl`+`C`:若有選擇文字為複製功能,若直接使用為強制中止並跳出程式

##### 執行檔案
在編譯器下用`make 檔名`編譯c檔案為機器語言
`make 檔名`實際上做的動作為
1. `clang -o 編譯後的檔名 檔名`
`clang` 為編譯檔案
`-o 編譯後的檔名`為設置編譯後的檔案名稱

要執行編譯後的檔案為`./檔名`
其中`./`表示到當前目錄找該檔案

##### 清除console內容
可以輸入`clear`或是用快捷鍵`Ctrl+L`來刪除

### 其他
##### RAM
所有程式執行時,編譯為機器語言的文件所儲存的地方,關閉程式後相關文件也會消失