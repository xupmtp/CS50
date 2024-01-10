# [Problem Set 5](https://cs50.harvard.edu/x/2020/psets/5/)
---
### [Speller](https://cs50.harvard.edu/x/2020/psets/5/speller/)
##### 題目說明
依題目要求設計資料結構並完成以下函數
* `load`讀取檔案中所有單字並寫入資料結構
* `hash`返回索引表示儲存單字的node位置
* `size`返回資料結構中共儲存多少單字
* `check`返回該單字是否存在於資料結構內(不分大小寫)
* `unload`釋放所有node的空間

其中需注意以下幾點事項:
1. 可假定單字長度不超過45
2. 每行只有1個單字
3. `dictionary.c`內的常數N可自行設定
4. 比起空間小更應該注重時間短

##### Answer
完成`dictionary.c`中的函數
不可更改`speller.c`內的程式

設計概念:
每種字母(不區分大小寫)儲存在一個陣列位置,每個位置儲存格式為node
有新單字則存於node開頭
```c
// Implements a dictionary's functionality

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <strings.h>

#include "dictionary.h"

// Represents a node in a hash table
typedef struct node
{
    char word[LENGTH + 1];
    struct node *next;
}
node;

// Number of buckets in hash table
// 26對應英文字母
const unsigned int N = 26;

// Hash table
node *table[N];
//word count
//計算總單字
int count = 0;

// Returns true if word is in dictionary else false
bool check(const char *word)
{
    // TODO
		//取得單字在哪一位置
    int h = hash(word);
    node *tmp = table[h];
    if (tmp == NULL)
    {
        return false;
    }
    while (tmp != NULL)
    {
				//strcasecmp()不區分大小寫比較
        if (strcasecmp(tmp->word, word) == 0)
        {
            return true;
        }
        tmp = tmp->next;
    }
    return false;
}

// Hashes word to a number
unsigned int hash(const char *word)
{
    // TODO
		// 大小寫皆存於同一node
    return tolower(word[0]) - 97;
}

// Loads dictionary into memory, returning true if successful else false
bool load(const char *dictionary)
{
    // TODO
    FILE *file = fopen(dictionary, "r");
    if (!file)
    {
        return false;
    }
		//單字不超過45長度(LENGTH定義在speller.c),+1用於存\0
		//fscanf()每次讀取最大長度為wrod大小的單字,遇到空格或換行符號則停止
		//當fscanf()返回EOF代表到達文件結尾
    char word[LENGTH + 1];
    while (fscanf(file, "%s", word) != EOF)
    {
        node *n = malloc(sizeof(node));
        strcpy(n->word, word);
        n->next = NULL;
        int first = tolower(word[0]) - 97;
        n->next = table[first];
        table[first] = n;
        // free(n);
        count++;
    }
    fclose(file);
    return true;
}

// Returns number of words in dictionary if loaded else 0 if not yet loaded
unsigned int size(void)
{
    // TODO
    return count;
}

// Unloads dictionary from memory, returning true if successful else false
//遍歷每個node節點並釋放記憶體
bool unload(void)
{
    // TODO
    for (int i = 0; i < N; i++)
    {
        if (table[i] == NULL)
        {
            continue;
        }
        node *list = table[i];
        while (list != NULL)
        {
            node *tmp = list->next;
            free(list);
            list = tmp;
        }
    }
    return true;
}

```