# Week W 0 ~ 2 Bootstrap
---
### Sass

#### 介紹
* sass是一種方便的stylesheet language,它提供我們更輕鬆的編輯CSS,包含變數,繼承,模組等等
* sass和scss文件的差異在sass使用縮排來包含內容(同python),scss使用大括號(同一般css)且一段的結尾須加上分號

#### 安裝
* **npm安裝** 若我們有安裝npm則可以使用npm安裝


	$ npm install -g sass
	
* **Github** 按照[GitHub](https://github.com/sass/dart-sass/releases/tag/1.32.8)上的步驟安裝

#### 編譯Sass文件
* 最簡單的使用方式, 將該層目錄下指定名稱的sass文件編譯成css文件


	$ sass index.sass index.css
	
* 但是每次修改都要執行指令過於繁瑣,我可以用`--watch`監聽該文件,當sass文件有任何改動都會自動重新編譯為css


	$ sass --watch input.scss output.css
	
* 我們也可以指定某的目錄,當該目錄下的sass文件有任何更動都會將CSS編譯到另一層目錄中,兩個目錄中間以`:`隔開

	$ sass --watch app/sass:public/stylesheets
	
> 關於sass文件的寫法參考[Sass: Sass Basics](https://sass-lang.com/guide)