# Week 5 Data Structures(資料結構)
---
### 上週回顧
#### 指針
初始化指針時,該指針沒有指向任何記憶體空間,此時給指針賦值時便會報錯

```c
int main(void)
{
    int *x;
    int *y;
		
		//指針沒指向任何空間,無法賦值
		//*x = 42;

		//給指針一空間
    x = malloc(sizeof(int));
		
		//將y指向x的位置,兩變數共享同一位址
		y = x
		
		//此時*y的值也會變
    *x = 42;
}
```

#### 陣列擴增
陣列指向的是一塊連續的,固定的記憶體空間,若要新增元素給陣列就只能新增一空間更大的陣列並將原本的值和要加入的值傳給新陣列

>第6行中給int變數3塊空間等同於`int list[3]`,都是在記憶體中給變數一段連續且長度為3的位址
```c
#include <stdio.h>
#include <stdlib.h>

int main(void)
{
    int *list = malloc(3 * sizeof(int));
    if (list == NULL)
    {
        return 1;
    }

    list[0] = 1;
    list[1] = 2;
    list[2] = 3;

    int *tmp = malloc(4 * sizeof(int));
    if (tmp == NULL)
    {
        return 1;
    }

    for (int i = 0; i < 3; i++)
    {
        tmp[i] = list[i];
    }

    tmp[3] = 4;

    free(list);
		
		//釋出指向list的空間
    list = tmp;

    for (int i = 0; i < 4; i++)
    {
        printf("%i\n", list[i]);
    }

		//釋出原本指向tmp的空間
    free(list);
}
```
上面程式可以用`realloc()`函數取代,`realloc()`的功能是重新分配大小給參數中的陣列

```c
#include <stdio.h>
#include <stdlib.h>

int main(void)
{
    int *list = malloc(3 * sizeof(int));
    if (list == NULL)
    {
        return 1;
    }

    list[0] = 1;
    list[1] = 2;
    list[2] = 3;

    // Here, we give realloc our original array that list points to, and it will
    // return a new address for a new array, with the old data copied over:
    int *tmp = realloc(list, 4 * sizeof(int));
    if (tmp == NULL)
    {
        return 1;
    }
    // Now, all we need to do is remember the location of the new array:
    list = tmp;

    list[3] = 4;

    for (int i = 0; i < 4; i++)
    {
        printf("%i\n", list[i]);
    }

    free(list);
}
```

### 資料結構(Data structures)
* 資料結構可以讓我們以不同的方式儲存所需資料
* 要在c建立資料結構,我們需要以下工具來完成
	* `struct`建立一個資料結構
	* `.`訪問其中的屬性
	* `*`轉到該指針指向的記憶體位址

### 鍊表(Linked Lists)
* 鍊表可以儲存資料在記憶體的各個位置,並隨時可以新增資料
* 透過儲存下個資料的位址從而將鍊表串聯起來
![linked_list](https://cs50.harvard.edu/x/2020/notes/5/linked_list_with_addresses.png)
>c語言的NULL便是`\0`,他同時也是字串結尾的符號,而且NULL是一個全0的空指針,我們可以將其視為無指向

* 與陣列不同,我們不可隨機訪問鍊表,我們必須跟隨每個元素儲存的指針才能訪問下個元素
* 下面是我們自己實現的鍊表節點,`number`為儲存的資料,`node`指向下一個`node`
```c
//開頭定義node名稱才不會讓結構中的node找不到node結構
typedef struct node
{
    int number;
		//結構中使用自己作為屬性時需加"struct"
    struct node *next;
}
node;
```
* 接著我們透過程式連接鍊表
* 要添加元素於尾端時,需先給node分配空間,並設值給他
```c
node *list = NULL;
node *n = malloc(sizeof(node));

if (n != NULL)
{
    //這裡使用的"->"取代"."來訪問屬性,目的是讓程式的可讀性增加
		//(*n).number = 2;
    n->number = 2;
    
		//還沒有下個節點所以先設為NULL
    n->next = NULL;
}

list = n;
```
* 現在list已經指向一節點
![list_with_one_node.png](https://cs50.harvard.edu/x/2020/notes/5/list_with_one_node.png)
* list只會指向第一個節點,所以要訪問全部元素時必須跟隨每個節點的下個指針
```c
// 創建臨時指針來遍歷list
node *tmp = list;
// 節點還有下個指針時
while (tmp->next != NULL)
{
    // 前往下個指針
    tmp = tmp->next;
}
// 現在tmp已指向最後一個node,我們可以在其身後添加新節點
//list還是指向開頭,但它的尾端已由tmp新加了一節點
//因為兩者指針是指向同一位址改變tmp值list也會變
```
* 如果要在鏈接list的前面插入一個節點，則需要在更新list之前更新節點，使其指向list的開頭節點。否則，我們將失去其餘list節點
```c
//指向list的開頭
n->next = list;
//list開頭指向新的節點n
list = n;
```
* 最後程式結束前,我們必須釋放每個節點的記憶體空間,否則會導致內存洩漏
```c
//當還有下個節點時
while (list != NULL)
{
	//暫存下個節點
	node *tmp = list->next;
	//釋放當前節點
	free(list);
	//指向下個節點
	list = tmp;
}
```
>完整程式參考[linked-lists](https://cs50.harvard.edu/x/2020/notes/5/#linked-lists)

### 其他資料結構
#### Tree
![binary_search_tree.png](https://cs50.harvard.edu/x/2020/notes/5/binary_search_tree.png)
* tree每一層有兩個節點,像是更複雜的鍊表,其中每個節點有2個指針指向left和right
* 上圖的tree有個專有名稱叫二叉搜索樹(binary search tree),因為它的排序方式讓我們能夠正確地按二進制方法來搜尋元素
* 以一個排序過的陣列來看,最上層的節點指向陣列中間值,下一層左邊節點為"左半中間值",右節點為"右半中間值",所以該結構的特性為**左半邊值永遠小於右半邊**
* 使用binary search tree可以輕鬆完成二進制搜尋來搜尋數字50
```c
typedef struct node
{
    int number;
    struct node *left;
    struct node *right;
} node;

// 回傳是否有該值
bool search(node *tree)
{
    //到達根節點return
    if (tree == NULL)
    {
        return false;
    }
    
		//遞迴往下遍歷節點
    else if (50 < tree->number)
    {
        return search(tree->left);
    }
    else if (50 > tree->number)
    {
        return search(tree->right);
    }
    
		//找到50 return true
    else {
        return true;
    }
}
```
#### Hash Table
* 我們在26大小的陣列中按開頭字母順序儲存資料,每個字母用鍊表儲存它所包含的所有單字
* Hash表的優點是幾乎擁有既定的搜尋時間O(1),缺點是所需記憶體龐大
![hash_table.png](https://cs50.harvard.edu/x/2020/notes/5/hash_table.png)
* 上圖中,我們可以用字母`R`搜尋到`Ron`和`Remus`
* 通常使用hash表會希望能得到唯一的值,若我們要以前3個字母開頭作為搜尋依據,雖然可搜尋到唯一值`Ron`,但所需的陣列數為26 * 26 * 26,單字越多空間越大


* 我們還可以使用另一種叫做`trie`的數據結構
![trie.png](https://cs50.harvard.edu/x/2020/notes/5/trie.png)
* trie中每個節點都是一個陣列,第一個字母指向一陣列,個有效字母指向列一陣列
* 即使trie中包含很多單字,搜尋時間也將只有單字長度而已,這代表我們搜尋及插入單字的時間複雜度都只有O(1),但是缺點是每個字符的記憶的大小是鍊表的26倍

#### 其他
* **Queue**是一個先進先出的隊列
* **Stack**是後進先出的堆疊
* **dictionary**可以透過key來找到值,我們可以使用hash表來實現字典
