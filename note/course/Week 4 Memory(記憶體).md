# Week 4 Memory(記憶體)
---
### 十六進位
十六進位為計算機科學上常用的進位,表示法為:
0 1 2 3 4 5 6 7 8 9 A B C D E F
為了避免與10進制,8進制搞混,於是在十六進制前會加上`0x`來表示,EX:0x1,0xF

>+在電腦中儲存資料的方式便是16進位,2進位在儲存上過於龐大,而10進位處理複雜,於是乎便產生了16進位的儲存方式
##### RGB
RGB是電腦上常用的顏色表示法
* R:red
* G:green
* B:blue

在CSS中的寫法為:`#000000`
其中每兩個0代表一種顏色的值域,由左至右分別為R,G,B三種顏色,從0~256以16進位來表示,數值越大代表顏色越多EX:
`#000000`代表沒有任何顏色的黑色
`#FF0000`代表紅色
`#FFFF00`代表紅色混和的黃色
`#FFFFFF`代表三種混和的白色
#### RGB
RGB是電腦上常用的顏色表示法
* R:red
* G:green
* B:blue

在CSS中的寫法為:`#000000`
其中每兩個0代表一種顏色的值域,由左至右分別為R,G,B三種顏色,從0~256以16進位來表示,數值越大代表顏色越多EX:
`#000000`代表沒有任何顏色的黑色
`#FF0000`代表紅色
`#FFFF00`代表紅色混和的黃色
`#FFFFFF`代表三種混和的白色

### 指標
#### &符號
寫於變數前面,表示該變數儲存的記憶體位置
```c
int n = 50;
printf("%p\n",&n);
```
#### *符號
宣告變數時(下面第2行),加上*表示這個變數儲存的是記憶體位置

使用變數時(下面第6行),加上*表示從此記憶體位址取值出來

>注意:若變數p前面沒加`*`則會報錯
```c
int a = 10;
int *b = &a;
int c = a;
a = 20;

//c複製a的值,所以之後a改變時c不變
printf("c:%i\n", c);//10
//b複製a的記憶體位址,a改變時b也會跟著改變
printf("b:%i\n", *b);//20
//%p表示這是記憶體位置
printf("%p\n", b);//0x7ffc78d6d2cc
```

### 交換
#### 傳值呼叫
這是一般使用傳值呼叫(call by value)方式的函數,即使我們在方法內交換變數值,改變的也只有方法內的變數,實際傳入方法的變數x,y值不會改變
```c
#include <stdio.h>
void swap(int a, int b);

int main(void)
{
    int x = 1;
    int y = 2;

    printf("x is %i, y is %i\n", x, y);
    swap(x, y);
    printf("x is %i, y is %i\n", x, y);
}

void swap(int a, int b)
{
    int tmp = a;
    a = b;
    b = tmp;
}
```
#### 傳址呼叫
想要在方法中改變x,y的值,我們叫必須傳入兩變數的記憶體位址,並交換記憶體位址內的值,這就是所謂的傳址呼叫(call by address)

```c
#include <stdio.h>

void swap(int *a, int *b);

int main(void)
{
    int x = 1;
    int y = 2;

    printf("x is %i, y is %i\n", x, y);
    swap(&x, &y);
    printf("x is %i, y is %i\n", x, y);
}

void swap(int *a, int *b)
{
    int tmp = *a;
    *a = *b;
    *b = tmp;
}
```

### 記憶體分布(Memory layout)
在我們的電腦記憶體中會將程序中不同類型的數據分別儲存在記憶體的不同位置
![memory_layout.png](https://cs50.harvard.edu/x/2020/notes/4/memory_layout.png)

* **machine code**是編譯完後的2進位代碼,該代碼將被加載到記憶體的“頂部”
* **globals**是我們在程序中聲明的全局變量或整個程序可以訪問的其他共享變量
* **heap**是個空白區域,使用`malloc()`會從此處獲得空間供程式使用
* **stack**儲存函數及局部變數,我們的`main()`方法位於stack的最底部
	* 我們可以看到,若是用call by value的方式,傳遞的是2跟1這兩個值,所以實際上並不會更動x,y的值
<img src="https://cs50.harvard.edu/x/2020/notes/4/stack.png" height="300px" title="stack"/>
	* 若是使用call by address的方式從`main()`取得x,y位址,實際更改的便是x,y的值
<img src="https://cs50.harvard.edu/x/2020/notes/4/pointers.png" height="300px" title="pointers"/>


>從記憶體分佈中可看出,`heap`和`stack`是使用同一空間,所以當過度使用記憶體時(如:malloc沒釋放,遞迴過多次)就會造成stack or heap overflow
### 字串符
week4以前所使用的`string`型別是cs50課程使用`typedef`實現的自定義型別,week4後我們了解到字串是以多個字符(char)組成的方式存於記憶體
EX:一個字串`s`儲存的值為"EMMA"
則他在記憶體中儲存的方式如下表
E<br>0x123|M<br>0x124|M<br>0x125|A<br>0x126|\0<br>0x127
--|--|--|--|--
其中每一格代表一個儲存位置,0x123就代表記憶體位置

所以我們可以用下面方式來表達一字串
```c
char *str = "EMMA";
```
`＊`符號會指向字串的記憶體開始位置,上面來說便是**E**的位置,而C語言會自動幫我們找到剩下的所有字符
>因為字串結尾會有一隱藏字符`\0`,所以程式知道要在哪裡停止尋找字符

#### 複製字串
```c
char *s = "EMMA";
char *t = s;
s[0] = 'A';
printf("%s", t);//AMMA
```
從上面例子看出,變數s儲存的是字串的記憶體位址,複製s只是複製該位址,最終還是指向s存放的地方

所以我們複製字串最好的做法是先新增一記憶體位置,接著分別複製s每個字符給新變數,方法如下

```c
#include<cs50.h>
#include<ctype.h>
#include<stdio.h>

int void main()
{
	char *s = get_string("Name: ");
	//malloc()函數分配制定大小的記憶體空間給變數,參數為空間大小
	//字串最後必須有空中止符("\0"),所以空間大小為字串長度+1
	char *t = malloc(strlen(s) + 1);
	//n存放長度避免每圈都呼叫一次len函數
	//因為空中止符也要複製所以迴圈次數為 i < n + 1
	for (int i = 0, n = strlen(s); i < n + 1; i++)
	{
		t[i] = s[i];
	}
	
	//以上迴圈可以呼叫strcpy()函數來取代,其功用便是將第2個參數的值複製給第1參數
	strcpy(t, s);
	
	//若有分配記憶體出去,使用完畢後須釋放該空間,否則會占用越來越多記憶體
	free(t);
}
```
>複製字串時若忘了加空中止符,在使用或打印該字串時可能會出現比預期更多的字元,也有可能產生錯誤

#### malloc
malloc函數用於分配指定空間的記憶體給變數,傳入參數為記憶體大小(int),該方法返回指針

不同型別所需空間也不同

可以用`sizeof(型別or變數)`取得空間大小,`sizeof`是c的關鍵字而非函數,傳入變數或型別名稱會返回該型別所占記憶體大小
```c
int *x = malloc(10 * sizeof(int));
```
#### Valgrind記憶體調適工具
cs50 IDE提供了Valgrind讓我們解決記憶體相關的問題,如:未釋放的記憶體等等,之後可以用help50做debug

使用方法為執行程式前加algrind
`$ valgrind ./myProgram`

### get_xxx
之前我們取得console輸入是用`cs50.h`這個lib,學到指標後,我們可以自己用`scanf()`來實現相同的功能

* `scanf()`用法和printf類似,但傳入的參數為變數記憶體位址
* scanf的除錯功能不強,若輸入型別不對則會返回變數原本的初始值,若變數沒初始值則會返回該型別預設值
* 輸入為字串時,需分配其記憶體大小,若沒分配(例如給NULL)則返回null,若分配的空間不夠,則scanf可能試圖將char數組末尾的內容寫入未知內存，從而導致程序崩潰。
```c
#include <stdio.h>

int main(void)
{
    int x;
    printf("x: ");
    scanf("%i", &x);
    printf("x: %i\n", x);//x沒設值,型別錯誤時回傳x = 0
	
	char *s = NULL;//正確為char s[5];
    printf("s: ");
    scanf("%s", s);
    printf("s: %s\n", s);//即使輸入字串還是返回null
}
```

### 檔案I/O
c語言中FILE類別用來讀取檔案並操作,使用需時加入`#include<stdio.h>`

使用時用指針指向該文件,該指針的記憶體位置便是讀取到文件後電腦將該文件暫存的位置

```c
#include<stdio.h>

int void main()
{
	//第一個參數為文件路徑(相對或絕對皆可)
	/**
	* 第二個參數表示要對文件進行的操作
	* r 讀取文件
	* w 寫入文件
	* a 追加內容到文件
	*/
	FILE *file = fopen("test.csv", "a")
	
	//將文字寫入檔案中,第一參數為要寫入的檔案,其他參數同printf()
	//這裡示範寫入excel,其中每格欄位以逗號做區隔
	fprintf(file, "%s,%s\n", "Hello","Simon");
	
	//用完後關閉檔案釋放記憶體
	fclose(file);
}
```

##### JPEG
* 我們可以編寫一程式來判斷檔案是否為JPEG
[unsigned](http://phorum.study-area.org/index.php?topic=28904.0)關鍵字指定該變數不會有負數的值
* 一般[JPEG](https://iter01.com/422213.html)的慣例,其檔案二進位的前3碼必定為0xff,0xd8,0xff ,順序固定不換,根據此慣例可以判斷不是這樣開頭的檔案必定不是JPEG
* 有些檔案格式開頭同JPEG,所以我們只能判斷不是JPEG的檔案
```c
#include <stdio.h>

int main(int argc, char *argv[])
{
    // Check usage
    if (argc != 2)
    {
        return 1;
    }

    // Open file
    FILE *file = fopen(argv[1], "r");
    if (!file)
    {
        return 1;
    }

    // Read first three bytes
    unsigned char bytes[3];
    fread(bytes, 1, 3, file);

    // Check first three bytes
    if (bytes[0] == 0xff && bytes[1] == 0xd8 && bytes[2] == 0xff)
    {
        printf("Maybe\n");
    }
    else
    {
        printf("No\n");
    }

    // Close file
    fclose(file);
}
```
