# Week 8 Android
---
### 回顧
過去7周,握們從最開始的`Hello World`,陸續掌握了許多程式技巧
* 從C語言開始了解程式的邏輯及設計原理,最後透過python和SQL等高級語言來解決更多有趣的問題
* 要解決問題須設計不同的算法,算法的精準度和正確性尤其重要,因為電腦不瞭解我們心裡所想的方法

下面的課程中我們必須從四種開發路線中選擇一個做為接下來1~2周的學習路徑
* `Android` or `IOS`
* `Web`應用開發
* 使用`Lua`的遊戲開發


### Android
我選擇的路線是Android,在挑選時對於要選擇Android還是IOS考慮了一陣子,但因為電腦不是使用MAC,開發環境不好安裝故而選擇了Android

下面我們會建立一pokedex專案,內容包括顯示寶可夢名稱列表,且點擊後會顯示詳細資料

### 建立專案
開啟Android studio ->new Project ->Empty Activity -> 輸入名稱及路徑後finish

專案內有app資料夾及gradle scripts

app內含所有程式檔案

gradle用於建構專案資源

#### 名詞介紹
* `MVC`設計概念,將程式分為`Model`,`View`,`Controller`三個部分,降低程式間的耦合性      <androidx.recyclerview.widget.RecyclerView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/recycle_view" />
* `model`儲存數據資料
* `View`顯示畫面
* `Controller`控制資料到顯示畫面前的邏輯處理
* `Activities`Android應用程式中每個畫面可以代表一個Activity,Activity內包含這個頁面上所有的控制元件所要做的事(例如點擊後發生什麼)
* `Rresources`除了上述程式以外的其他資源,不僅僅包含java檔
* `Intents`告訴程式從一個Activity到另一Activity之間如何傳遞資料
* `Recycle Views`代表畫面呈現的項目列表,它不會立刻顯示所有的項目,EX:fb推文若有1000則,使用`Recycle Views`顯示時只會先載入螢幕範圍可顯示的推文,往下滑動時才會繼續載入其他項目,原理如下圖所示
<img src="https://miro.medium.com/max/1050/1*CeWEPtWL6Zb1ssP87kIPUQ.jpeg" width="500px" />

接著介紹App內的目錄

#### mainifests
包含一`AndroidManifest.xml`檔案

此檔案內定義activity讓android程式能夠找到你的app中使用的所有activity

#### java
內有三個同路徑資料夾,其中含`MainActivity.java`的是主要程式,定要所有activity,剩下兩個資料夾為測試用

#### res
內含四個目錄資料夾,這裡介紹我們會使用到的幾種
##### Layout
放置頁面的佈局元素xml檔,android studio提供圖形介面來拉取元素

* `<ConstraintLayout>`佈局元素,用來關聯佈局內的各個部件
	* `android:layout_width`及`android:layout_height`標籤屬性,設置該佈局的寬/高度
		* `match_parent`設置該佈局元素寬/高跟母元素一樣,若沒有母元素則填滿螢幕大小
		* `wrap_content`元素寬/高根據文本內容動態調整
	* `tools:context` 這裡的值為`.MainActivity`,代表這個layout關連到`MainActivity`這個class並受它控制
* `<TextView>`簡單顯示文字在螢幕上的標籤
##### values
定義文字,樣式,顏色等等檔案所在
* `strings.xml` 定義靜態文字檔,可根據不同語言更換不同檔案,引用方式:`android:label="@string/string名稱"`