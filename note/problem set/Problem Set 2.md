# [Problem Set 2](https://cs50.harvard.edu/x/2020/psets/2/)
---
### 1. [Readability](https://cs50.harvard.edu/x/2020/psets/2/readability/)
##### 題目說明
依題目要求判斷該文章的閱讀年級
公式如下

	index = 0.0588 * L - 0.296 * S - 15.8
其中，`L`是文本中每100個單詞的平均字母數，並且`S`是文本中每100個單詞的平均句子數,根據計算結果4捨5入即為答案

文章內應該依據下列規則來判斷內容
1. 字母間以空格隔開,其他符號隔開都應視為同一字母,EX:`test-text`
2. 句子結尾應包含`!`,`.`,`?`其中一符號
3. 如果結果index為16或更高,應該輸入`"Grade 16+"`,如果小於1則輸入`"Before Grade 1"`

##### Answer
此題主要難度在內建函數的運用,需參考[CS50 Programmer's Manual](https://man.cs50.io/)內的API說明來應用
```c
#include <cs50.h>
#include <stdio.h>
#include <ctype.h>
#include <math.h>
#include <string.h>

int get_index();

void print_grade(int index);

float letters = 0.0;
float words = 1.0;
float sentences = 0.0;

int main(void)
{
	string text = get_string("Text: ");

	for(int i = 0; i < strlen(text); i++)
	{
			//isalpha(text[i])判斷是否為字母,return非0表示字母
			letters += isalpha(text[i]) != 0 ? 1.0 : 0.0;
			//判斷是否空白,return非0表示空白
			words += isblank(text[i]) != 0 ? 1.0 : 0.0;
			//判斷句尾
			sentences += '!' == text[i] || '.' == text[i] || '?' == text[i] ? 1.0 : 0.0;
	}

	print_grade(get_index());

}

/**
*依照題目公式取得等級
*/
int get_index()
{
	float word_by_letters = (letters / words) * 100.0;
	float sentence_by_words = (sentences / words) * 100.0;
	float index = (0.0588 * word_by_letters) - (0.296 * sentence_by_words) - 15.8;
	return roundf(index);
}

/**
*印出grade
*/
void print_grade(int index)
{
	if(index < 1)
	{
			printf("%s\n", "Before Grade 1");
	}
	else if(index >= 16)
	{
			printf("%s\n", "Grade 16+");
	}
	else
	{
			printf("%s %i\n", "Grade", index);
	}
}
```
### 2-1. [Caesar](https://cs50.harvard.edu/x/2020/psets/2/caesar/)
##### 題目說明
輸入加密數字,要加密的文字,按照凱薩加密演算法輸出結果,如下所示

	$ ./caesar 13
	plaintext:  hEL LO
	ciphertext: uRY YB
1. 每個文字按字母排列順序往後移X位(區分大小寫),且文字空格、符號不變
2. 若後移超過最大字母(Zz)則從A開始(像是上面的O)

##### Answer
```c
#include<cs50.h>
#include<stdio.h>
#include <stdlib.h>
#include<string.h>
#include <ctype.h>

int main(int argc, string argv[])
{
	//判斷輸入加密密碼不可超過1個
	if(argc != 2)
	{
			printf("Usage: ./caesar key\n");
			return 1;
	}
	//判斷是否為數字
	for(int i = 0; i < strlen(argv[1]); i++)
	{
			if(isdigit(argv[1][i]) == 0)
			{
					printf("Usage: ./caesar key\n");
					return 1;
			}
	}
	int k = atol(argv[1]);


	string input = get_string("plaintext: ");
	//加密
	for(int i = 0, n = strlen(input); i < n; i++)
	{
			if(input[i] >= 'a' && input[i] <= 'z')
			{
					input[i] = ((input[i] - 'a' + k) % 26) + 'a';
			}
			else if(input[i] >= 'A' && input[i] <= 'Z')
			{
					input[i] = ((input[i] - 'A' + k) % 26) + 'A';
			}
	}

	printf("ciphertext: %s\n", input);
	return 0;
}
```

### 2-2. [Substitution](https://cs50.harvard.edu/x/2020/psets/2/substitution/)
##### 題目說明
此題為ceasar題目的進階,依照下列題是完成題目
1. 輸入26個密碼字母,字母不可重複且不區分大小寫
2. 需檢查字母須為26為且不可多個
3. 輸入加密文字
4. 密碼字母第1個即為A,第2個即為B,按此規則替換加密文字(區分大小寫)
5. 輸出結果

EX1:

	$ ./substitution YTNSHKVEFXRBAUQZCLWDMIPGJO
	plaintext:  HELLO
	ciphertext: EHBBQ
##### Answer
此題重點是如何儲存密碼以及判斷密碼是否重複,其他程式同2.1
```c
#include<cs50.h>
#include<stdio.h>
#include <stdlib.h>
#include<string.h>
#include <ctype.h>

int main(int argc, string argv[])
{
	//確認是否有出現過字母
	bool check[26] = {false};
	//儲存密碼,letters[0]為要將A替換的新字母順序(0~25)
	//EX:letters[0]=1則字母A替換為B
	int letters[26];
	string salt = argv[1];

	if (argc != 2)
	{
			printf("Usage: ./substitution key\n");
			return 1;
	}
	if (strlen(salt) != 26)
	{
			printf("Key must contain 26 characters.\n");
			return 1;
	}
	for (int i = 0; i < strlen(salt); i++)
	{
			char c = toupper(salt[i]);
			if (isalpha(c) == 0 || check[c - 'A'])
			{
					printf("Usage: ./substitution key\n");
					return 1;
			}
			letters[i] = c - 'A';
			check[c - 'A'] = true;
	}

	string input = get_string("plaintext: ");

	for (int i = 0, n = strlen(input); i < n; i++)
	{
			if (input[i] >= 'a' && input[i] <= 'z')
			{
					input[i] = letters[input[i] - 'a'] + 'a';
			}
			else if (input[i] >= 'A' && input[i] <= 'Z')
			{
					input[i] = letters[input[i] - 'A'] + 'A';
			}
	}

	printf("ciphertext: %s\n", input);
	return 0;
}
```