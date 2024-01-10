# Week 2 Array
---
### debug
##### 編譯時讓編譯器報錯
在編譯文件前面輸入`help50`將會提示錯誤在哪一行及應該要修正的內容為何,EX:
`help50 make fileName`
##### 設置中斷點進行debug
在cs50IDE上的行數設置中斷點(紅點),編譯文件後運行`debug50 ./fileName`

中斷點若設置一般語句上:
程式會停在該行

中斷點若設置在函數上:
程式會停止在執行函數的第一行

若要關閉debug模式可以使用快捷鍵`Ctrl+C`

##### check50
`check50`是檢查提出的代碼與問題集內要求的答案是否符合
##### style50
`style50`會檢查提交的代碼是否符合c語言程式撰寫時的風格
### Array
宣告數組在變數候用中括號內放數組大小,EX:宣告一int陣列其大小為10
`int nums[10];`
作為函數參數時的顯示方式
`void array(int nums[]){}`
##### 字串陣列
字串是由char字符及空字符的陣列組合而成,透過呼叫陣列位置可以取得指定位置字符

字符結尾都有一空字符表結束位置,該字符在ASC||表中十進位表示為0('A'為65),如下所示

```c
//str[0]=H
//打印每個字符結果:H e l l o \0
string str = "Hello";
```
##### 引入string庫
語法:`#include<string.h>`
包含函數:
`strlen(字串)`:取得該字串長度
```c
//常見遍歷字串寫法
//若寫i<strlen(name)每次loop會呼叫一次strlen()方法 效能較差
string name = "Harry";
for(int i=0,n=strlen(name);i<n;i++){
	printf("%s\n",name[i]);
}
```
### main()參數
程式主要執行的地方可設置參數,當運行程式時後面可以空格隔開字串,每空一格便是多一參數

以下示範印出進入點參數
```c
//此文件為test.c
#include<stdio.h>

int main(int argc,string args[])
{
	printf("argc:%i\n",argc);
	printf("args:%s,%s\n",args[0],args[1]);
}
```

執行程式

	$ make test
	$ ./test Hello World
執行結果
	
	3
	./test
	Hello
可看到執行程式時的指令在main()方法中	可用參數形式取得
argc為參數數量(以空格隔開)
args為參數集合陣列
### 其他
##### 強制轉型
同java,在變數前加轉型型別,在某些情況下c會自動幫忙做強制轉型的動作,像是在佔位符中EX:
```c
string toInt = "123";
int num = (int)toInt;//123
char c = 'A';
printf("%i",c);//65
```
##### 建立常量
使用`const`關鍵字建立依無法被更改的常量,一般建立常量變數名稱習慣大寫,EX:

`const int N = 3.14;`

##### static
static關鍵字建立的變數作用於區域範圍
但每次調用

