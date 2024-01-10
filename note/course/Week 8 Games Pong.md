# Week 8 Games Pong
---
### 目錄
1. [前言](#前言)
2. [環境設置](#環境設置)
3. [Lua基本介紹](#Lua基本介紹)
4. [Hello_World](#Hello_World)
5. [控制螢幕及元件位置](#控制螢幕及元件位置)
6. [虛擬螢幕](#虛擬螢幕)
7. [建立元件](#建立元件)
8. [移動元件](#移動元件)
9. [Class](#Class)
10. [FPS](#FPS)
11. [碰撞](#碰撞)
12. [遊戲規則](#遊戲規則)
13. [音樂](#音樂)
14. [打包成執行檔](#打包成執行檔)

### 前言
Pong是一款模擬桌球比賽的2D遊戲(詳見[維基百科](https://zh.wikipedia.org/wiki/%E4%B9%93))
下面介紹使用框架Love+Lua語言做出pong遊戲

### 環境設置

#### 安裝
開發IDE使用VSCODE, 要執行lua程式還需下載[LÖVE2D](https://love2d.org/)
此外在VSCODE中安裝love2D suuport套件方便執行程式

##### 執行
要執行lua程式有幾種方法

1. 將含有main.lua的資料夾整個拖到love.exe中執行
2. 利用前面安裝的love2D suuport套件設定儲存main.lua文件時執行
3. command line下指令`love [path]`, path應該是含有main.lua文件的資料夾路徑(EX:`C:\love\loveTest01`)
>在windows中要使用love命令需在環境變數`path`中加入love.exe所在的路徑, 預設安裝路徑是`C:\Program Files\love`
### Lua基本介紹
這裡使用lua5.1版進行開發
#### 資料型別
* nil
* 布林 (boolean)
* 數字 (number)
* 字串 (string)
* 表 (table)
* 函式 (function)


名稱|值|說明
--:|--:|--:
nil|nil|表示沒有值, 變數宣告後但未設置其他值時，該變數的的值即為 nil
布林 (Boolean)|true, false|lua語言中除了false nil以外皆為true,包含0,空字串,空table等等
數字 (Numbers)|整數及小數|5.3版前Numbers皆為浮點數,5.3 版後才引入整數型別整數 (integer)
字串 (Strings)|雙引號表示文字|Lua 是弱型別語言，字串和數字會自動轉換 EX: `"10" + 1 == 11`
表 (Table)|{}|lua唯一的資料結構, 類似python字典
函式 (Function)| function test()<br>end|包裝程式碼的區塊

#### 運算符
參考[Lua 运算符 \| 菜鸟教程](https://www.runoob.com/lua/lua-miscellaneous-operator.html)
#### 註解
* `--` 單行註解
* `--[[...]]` 多行註解

#### 變數宣告與值域
* lua沒有特別綁定型別而是由值的內容去做判斷
* 一般宣告預設為全域變數
```lua
-- 預設為全域變數
msg = "Hello"
-- 在函數內可加local表示此為區域變數
local num = 123

print(msg)
```

#### 流程控制
函數宣告
```lua
function fn()
	print("...")
end
```

迴圈
* 終止條件為<=第2參數,一般語言是小於須注意
* 第3參數為遞增/減條件,可不加,默認為1
* 使用ipairs()函數可以迭代table物件
* 或是用`#`取得table長度,然後手動遍歷, 要注意table索引從**1**開始
```lua
for i = 0, 10, 1 do
	print(i)
end
-- print 1 ~ 10

local arr = {"a", "b", "c", "d", "e"}

for i, e in ipairs(arr) do
  print(e)
end

-- 其他方式
for i = 1, #arr do
  print(arr[i])
end
```

條件式
```lua
if 1 == 1 then
	-- ...
elseif 2 == 2 then
	-- ...
else
	-- ...
end
```

三元運算子
```lua
x = 2
-- 條件成立跑and後的值否則跑or後的值
y = (x > 1) and 1 or 0
```

### Hello_World
* 在lua語言中,`main.lua`類似程式的進入點,每個執行的資料夾需含有此文件
* lua語言內只有table的數據類型,其儲存方式類似python的dict,無論是列表或字典在lua都以table方式儲存
* 遊戲開發中有一個重要概念為game loop,它是一個無限循環的迴圈,當玩家鍵盤輸入時便會觸發流程,而遊戲便是不斷的循環這些事件
	* input -> update -> render -> input ...
	* input 玩家觸發鍵盤輸入時
	* update 處理輸入邏輯 可能是移動 可能是攻擊等等
	* render 渲染畫面 將處理結果渲染成畫面
* love中提供幾種函數來處理game loop流程 
	* `love.load()` 處理輸入
	* `love.update()` 更新遊戲內容
	* `love.draw()` 渲染畫面
* 下面我們用`love.draw()`渲染"Hello World"到畫面上
```lua
function love.draw()
	love.graphics.print("Hello World")
end
```

### 控制螢幕及元件位置
* 接著我們要試著控制螢幕大小, 然後將"hello world"文字置於螢幕中央
* 我們會使用到幾種love提供的方法
	* `love.window.setMode(width, heifht, params)` 設定螢幕大小, `params`為table類型 用來設置其他相關參數
	* `love.graphics.printf(text, x, y, [width], [align])` printf用來在畫面中顯示文字, 除了內容外還可以指定位置(x,y)及寬度,對齊方式
* 在love螢幕畫面中, 左上角為(0, 0)的位置往右和一般數學一樣是x軸遞增, 但往下則變為y軸遞增

```lua
-- 定義螢幕長寬 大寫表示為常數
WINDOW_WIDTH = 800
WINDOW_HEIGHT = 600


function love.load()
    love.window.setMode(WINDOW_WIDTH, WINDOW_HEIGHT, {
        -- 是否要全螢幕
        fullscreen = false,
        -- 同步到顯示器的刷新率, true不會看到螢幕撕裂
        vsync = true,
        -- 可調整螢幕大小
        resizable = false
    })
end

function love.draw()
    -- y軸位置設置為寬度一半 -6是因為文字大小為12px 寬度為畫面寬度 且對其方式為置中
    love.graphics.printf("Hello World Lua", 0, WINDOW_HEIGHT / 2 - 6, WINDOW_WIDTH, 'center')
end
```

### 虛擬螢幕
* 從上面內容會發現文字有點小, 所以在這裡我們會利用第三方library `push`來放大文字
* 我們到[push github](https://github.com/Ulydev/push)中下載代碼, 然後將`push.lua`放到與`main.lua`同層目錄中, 這樣便可以使用push library了
* `push`的功用是在螢幕內模擬其他大小的螢幕, 元件在設置大小,位置時會以虛擬螢幕長寬做計算, EX:若是在1600X900螢幕中模擬一塊800X450的區塊, 則該區塊內的內容便會被放大
* 放大後文字的像素仍然不變, 有可能會變得較模糊,所以我們要調用`love.graphics.setdefaultfilter(min, mag)`來修正畫面, `min, mag`分別是縮小及放大圖片時要套用的過濾模式,過濾模式分別有
	* `linear` 畫面會較為偏向模糊
	* `nearest` 畫面會較為偏像素風

```lua
-- 定義螢幕長寬 大寫表示為常數
WINDOW_WIDTH = 800
WINDOW_HEIGHT = 600

-- 定義虛擬螢幕長寬比
VIRTUAL_WIDTH = 200
VIRTUAL_HEIGHT = 150

-- import push library
push = require 'push'


function love.load()
    -- 過濾螢幕顯示方式為像素顯示
    love.graphics.setDefaultFilter('nearest', 'nearest')
    -- 改調用push的螢幕設置 同時設置虛擬螢幕及原本螢幕的大小
    push:setupScreen(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, WINDOW_WIDTH, WINDOW_HEIGHT, {
        fullscreen = false,
        vsync = true,
        resizable = false
    })
end

-- user 按下按鍵時觸發
function love.keypressed(key)
    -- 按下esc鍵時觸發關閉螢幕
    if key == 'escape' then
        love.event.quit()
    end
end

function love.draw()
    -- start end包圍的區塊以push設置的格式來顯示 這裡表示用虛擬螢幕的方式渲染畫面
    push:apply('start')
    -- y軸位置設置為寬度一半 -6是因為文字大小為12px 寬度為畫面寬度 且對其方式為置中
    love.graphics.printf(
        "Hello World Lua", 
        0, 
        -- 因為螢幕大小不一樣了(變虛擬螢幕) 所以改用新的長寬
        VIRTUAL_HEIGHT / 2 - 6, 
        VIRTUAL_WIDTH, 
        'center')
    push:apply('end')
end
```

### 建立元件
* 上面我們可以控制螢幕的大小及區塊, 現在我們要來建立遊戲中的球拍, 球, 分數板等等物件,其中還要包含要顯示的標題
* 為了要讓標題, 需要對其設置不同的字體, 首先先到[DaFont](https://dafont.com)下載需要字體的`.ttf`(或其他格式字體檔)
* 接下來會用到一些love的方法
	* `love.graphics.newFont(path, size)` 建立一種新字體 path式字體檔路徑, size為字體大小
	* `love.graphics.setFont(font)`設置當前活動使用此字體 font為透過newFont()建立的物件
	* `love.graphics.rectangle( mode, x, y, width, height)` 繪製矩形 mode有兩種模式 `fill`為填滿矩形`line`為中空矩形,  x,y設置舉行位置, width,height設置長寬
* `love.graphics.clear(r, g, b, a)` 清除當前背景並設置新顏色 rgb對應三原色,a為透明度 0為透明, 參數值皆需介於0~1, 所以照一般rgb設0~255後還需除以255

* 接續上面程式碼
```lua
-- ...
-- 字體路徑
FONTPATH = 'font1.TTF'

-- 建立新字體
function love.load()
    -- 過濾螢幕顯示方式為像素顯示
    love.graphics.setDefaultFilter('nearest', 'nearest')

    -- 建立小字體 標題用
    smallFont = love.graphics.newFont(FONTPATH, 8)
    -- 建立大字體 分數用
    scoreFont = love.graphics.newFont(FONTPATH, 32)
		
		-- 分數 預設全域所以其他function也能取用
    player1Scroe = 0
    player2Scroe = 0

    -- 改調用push的螢幕設置 同時設置虛擬螢幕及原本螢幕的大小
    push:setupScreen(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, WINDOW_WIDTH, WINDOW_HEIGHT, {
        fullscreen = false,
        vsync = true,
        resizable = false
    })
end

-- 繪製元件
function love.draw()
    -- start end包圍的區塊以push設置的格式來顯示 這裡表示用虛擬螢幕的方式渲染畫面
    push:apply('start')

    -- 重設背景色 參數值介於0~1分255等分
    love.graphics.clear(40 / 255, 45 / 255, 52 / 255, 255 / 255)

    -- 繪製中心球 大小 5X5 -2是盡量讓球靠在中心
    love.graphics.rectangle('fill', VIRTUAL_WIDTH / 2 - 2, VIRTUAL_HEIGHT / 2 - 2, 5, 5)

    -- 繪製左邊球拍 大小5X20
    love.graphics.rectangle('fill', 5, 20 , 5, 20)

    -- 繪製右邊球拍 大小5X20
    love.graphics.rectangle('fill', VIRTUAL_WIDTH - 10, VIRTUAL_HEIGHT - 40, 5, 20)

    -- 設置小字體 然後渲染標題文字
    love.graphics.setFont(smallFont)
    love.graphics.printf("Hello Pong!", 0, 20, VIRTUAL_WIDTH, 'center')

    -- 改設大字體 然後渲染兩個分數
    love.graphics.setFont(scoreFont)
    love.graphics.print(player1Scroe, VIRTUAL_WIDTH / 2 - 50, VIRTUAL_HEIGHT / 3)
    love.graphics.print(player2Scroe, VIRTUAL_WIDTH / 2 + 30, VIRTUAL_HEIGHT / 3)
		
		push:apply('end')
end
```

### 移動元件
* 上面我已經設置好球及球拍了,接著我們要讓球拍可以上下移動,讓球可以隨機往一個方向移動
* 要偵測按鍵是否按下, 需使用`love.keyboard.isDown(key)` 此函數是個callback函數 可以在任何地方調用 當按下按鍵時會偵測是否和`key`參數的按鍵一樣並返回boolean值
* 在`love.update(dt)`中我們可以對user的輸入做處理, love會在每一幀調用update函數, 參數`dt`表示自上次更新後到現在過了幾秒, 參考[dt - LOVE](https://love2d.org/wiki/dt)
* `math.max(a,b)`和`math.min(a,b)`用法與java相同
* `math.randomseed(n)` 設置亂數種子, `math.random(n)`會隨機回傳x, x的值介於 1 <= x <= n, 若參數值為2個則x介於此兩數之間

```lua
function love.load()
		-- ...
    -- 球拍y軸高度改以變數動態改變
    player1Y = 20
    player2Y = VIRTUAL_HEIGHT - 40
		
		-- ball的x,y位置
    ballX = VIRTUAL_WIDTH / 2 - 2
    ballY = VIRTUAL_HEIGHT / 2 - 2

    -- ball的x, y軸移動距離(PX)
    ballDX = math.random(2) == 1 and 100 or -100
    ballDY = math.random(-50, 50)

    -- 判斷遊戲是否開始
    gamestat = 'start
		-- ...
end

--[[
控制按鍵動作
dt表示自上次調用update後過多久(秒)
love會在每一幀調用此函數
]]
function love.update(dt)
    -- =======控制球拍=======
    -- 左邊球拍 以'w,s'鍵移動
    if love.keyboard.isDown('w') then
        -- 球拍不可超出上方螢幕
        player1Y = math.max(0, player1Y - PADDLE_SPEED * dt)
    elseif love.keyboard.isDown('s') then
        -- 球拍不可超出下方螢幕
        player1Y = math.min(VIRTUAL_HEIGHT - 20, player1Y + PADDLE_SPEED * dt)
    end

    -- 右邊球拍 以'上,下'鍵移動
    if love.keyboard.isDown('up') then
        -- 球拍不可超出上方螢幕
        player2Y = math.max(0, player2Y - PADDLE_SPEED * dt)
    elseif love.keyboard.isDown('down') then
        -- 球拍不可超出下方螢幕
        player2Y = math.min(VIRTUAL_HEIGHT - 20, player2Y + PADDLE_SPEED * dt)
    end

    -- ======控制球======
    if gamestat == 'play' then
        ballX = ballX + ballDX * dt
        ballY = ballY + ballDY * dt
    end
end

-- user 按下按鍵時觸發
function love.keypressed(key)
    -- 按下esc鍵時觸發關閉螢幕
    if key == 'escape' then
        love.event.quit()
    elseif key == 'enter' or key == 'return' then
        -- 按下enter時開始或停止遊戲
        if gamestat == 'play' then
            gamestat = 'start'
        elseif gamestat == 'start' then
            -- 重新開始遊戲
            gamestat = 'play'
             -- ball的x,y位置
            ballX = VIRTUAL_WIDTH / 2 - 2
            ballY = VIRTUAL_HEIGHT / 2 - 2

            -- ball的x, y軸移動距離(PX)
            ballDX = math.random(2) == 1 and 100 or -100
            ballDY = math.random(-50, 50)
        end
    end
end

function love.draw()
    -- start end包圍的區塊以push設置的格式來顯示 這裡表示用虛擬螢幕的方式渲染畫面
    push:apply('start')
		
		-- ...
		
		-- 繪製中心球 大小 5X5 -2是盡量讓球靠在中心 x, y位置動態變化
    love.graphics.rectangle('fill', ballX, ballY, 5, 5)

    -- 繪製左邊球拍 大小5X20 高度改動態變化
    love.graphics.rectangle('fill', 5, player1Y , 5, 20)

    -- 繪製右邊球拍 大小5X20 高度改動態變化
    love.graphics.rectangle('fill', VIRTUAL_WIDTH - 10, player2Y, 5, 20)
		
		-- 開始及結束遊戲時顯示不同文字
    if gamestat == 'play' then
        love.graphics.printf("Hello play Game!", 0, 20, VIRTUAL_WIDTH, 'center')
    elseif gamestat == 'start' then
        love.graphics.printf("Hello start Game!", 0, 20, VIRTUAL_WIDTH, 'center')
    end
		-- ...
		
    push:apply('end')
end
```

### Class
* 在這個章節中要學習的是物件導向的程式,我們要修改之前的程式,將球拍和球變成物件來使用
* 在lua原生lib中沒有物件導向的概念, 但我們可以透過第3方library來模擬class
* 首先下載[hump/class.lua](https://gitlab.discs.ateneo.edu/CS123_B3/Tong-EATS/blob/d7d5e30fedbe500a1e9e0851fd90a0adc0afc1a0/hump/class.lua)並將它放到main.lua的目錄中
* 接著要建立`Paddle.lua`及`Ball.lua`兩個檔案,分別代表球拍和球的class
* 接著在`Paddle.lua`中加入以下程式, init()方法用來建立建構子, 其他函數名稱可自訂
```lua
-- Paddle.lua 

-- 建立球拍class
Paddle = Class{}

-- init函數有特殊意義, 為類別建構子, 調用為隱式調用
function Paddle:init(x, y, width, height)
    -- self意思是調用此物件本身 後面為屬性名稱
    self.x = x
    self.y = y
    self.width = width
    self.height = height

    -- 設置槳的初始移動速度
    self.dy = 0
end

-- 自訂函數,調用方法從"."改為":"

-- 移動球拍位置
function Paddle:update(dt)
    -- 球拍上移
    if self.dy < 0 then
        self.y = math.max(0, self.y + self.dy * dt)
    -- 球拍下移
    elseif self.dy > 0 then
        self.y = math.min(VIRTUAL_HEIGHT - 20, self.y + self.dy * dt)
    end
end

-- 繪製球拍
function Paddle:render()
    love.graphics.rectangle('fill', self.x, self.y , self.width, self.height)
end
```
* 然後在`Ball.lua`中加入以下程式
```lua
-- Ball.lua

Ball = Class{}

-- 建構子
function Ball:init(x, y, width, height)
    self.x = x
    self.y = y

    -- 記住初始位置 每次從此位置重新開始
    self.initX = x
    self.initY = y
    self.width = width
    self.height = height

    -- 初始方向及速度設為隨機數
    self.dx = math.random(2) == 1 and 100 or -100
    self.dy = math.random(-50, 50)
end


-- 重新開始 位置變成初始位置
function Ball:reset()
    self.x = self.initX
    self.y = self.initY

    self.dx = math.random(2) == 1 and 100 or -100
    self.dy = math.random(-50, 50)
end

-- 更新球的位置
function Ball:update(dt)
    self.x = self.x + self.dx * dt
    self.y = self.y + self.dy * dt
end

-- 繪製球
function Ball:render()
    love.graphics.rectangle('fill', self.x, self.y, self.width, self.height)
end
```
* 最後修改`main.lua`中的程式將對應程式換為呼叫物件方法
```lua
-- main.lua 

-- ...
-- import library
push = require 'push'
Class = require 'class'
-- 使用其他檔案的程式
require 'Paddle'
require 'Ball'

function love.load()
    -- ...
    -- 初始化球拍及球改調用class init方法
    paddle1 = Paddle(5, 20, 5, 20)
    paddle2 = Paddle(VIRTUAL_WIDTH - 10, VIRTUAL_HEIGHT - 40, 5, 20)
    ball = Ball(VIRTUAL_WIDTH / 2 - 2, VIRTUAL_HEIGHT / 2 - 2, 5, 5)
    --...
end


function love.update(dt)
		-- =======控制球拍=======

    -- update()會修改y軸位置
    paddle1:update(dt)
    paddle2:update(dt)

    -- 按鍵動作改為設置dy值 負值往上 正值往下
    if love.keyboard.isDown('w') then
        paddle1.dy = - PADDLE_SPEED
    elseif love.keyboard.isDown('s') then
        paddle1.dy = PADDLE_SPEED
    else
        -- 其他按鍵時dy要設回0否則會繼續移動
        paddle1.dy = 0
    end

    if love.keyboard.isDown('up') then
        paddle2.dy = - PADDLE_SPEED
    elseif love.keyboard.isDown('down') then
        paddle2.dy = PADDLE_SPEED
    else
        paddle2.dy = 0
    end
    -- ======控制球======
    if gamestat == 'play' then
        ball:update(dt)
    end
end

-- user 按下按鍵時觸發
function love.keypressed(key)
    -- 按下esc鍵時觸發關閉螢幕
    if key == 'escape' then
        love.event.quit()
    elseif key == 'enter' or key == 'return' then
        -- 按下enter時開始或停止遊戲
        if gamestat == 'play' then
            gamestat = 'start'
        elseif gamestat == 'start' then
            -- 重新開始遊戲
            gamestat = 'play'
            ball:reset()
        end
    end
end

function love.draw()
    -- start end包圍的區塊以push設置的格式來顯示 這裡表示用虛擬螢幕的方式渲染畫面
    push:apply('start')
		-- ...
    -- 繪製球
    ball:render()
    -- 繪製球拍
    paddle1:render()
    paddle2:render()
		-- ...
    push:apply('end')
end
```

### FPS
* 這章節中我們要設置遊戲標題及顯示我們的FPS,FPS是衡量效能的指標之一
* 在這章中會用到幾種新的函數
	* `love.window.setTitle(title)` 設置視窗標題
	* `love.timer,getFPS()` 返回FPS的數值
	* `tostring(obj)` 數值轉換成字串

```lua
function love.draw()
    push:apply('start')
		-- ...
    displayFPS()
    push:apply('end')
end

-- 繪製FPS顯示文字及標題
function displayFPS()
    -- 設為綠色
    love.graphics.setColor(0, 1, 0, 1)
    -- 設為小字體
    love.graphics.setFont(smallFont)
    -- 顯示FPS 字串相加用'..' getFPS()返回數字,需先轉為文字再相加
    love.graphics.print('FPS: ' .. tostring(love.timer.getFPS()), 40, 20)
    love.window.setTitle('Pong')
    love.graphics.setColor(1, 1, 1, 1)
end
```

### 碰撞
* 在這章中我們要處理球的碰撞, 包括兩個球拍及上下邊界
* 所有元件位置皆以左上角頂點座標來計算 所以計算碰撞時需注意是否要增減元件的寬度及高度
* 當球碰到左右球拍時 y軸移動方向不變 x軸移動方向相反
* 當球碰到上下邊界時 x軸移動方向不變 y軸移動方向相反

* 在`ball.lua`中新增偵測碰撞球拍函數
```lua
--[[
    判斷邊緣碰撞
    所有元件x,y位置皆以左上角為主 box為球拍元件
    return true:有碰撞 false:無碰撞
]]
function Ball:collides(box)
    if self.x > box.x + box.width or self.x + self.width < box.x then
        return false
    end
    if self.y > box.y + box.height or self.y + self.height < box.y then
        return false
    end

    return true
end
```
* 在`main.lua`中控制碰撞時要如何動作
```lua
function love.update(dt)
    -- =======控制碰撞=======
    if ball:collides(paddle1) then
        ball.dx = -ball.dx
    end

    if ball:collides(paddle2) then
        ball.dx = -ball.dx
    end

    if ball.y <= 0 then
        ball.dy = -ball.dy
        ball.y = 0
    end

    if ball.y >= VIRTUAL_HEIGHT - ball.height then
        ball.dy = -ball.dy
        ball.y = VIRTUAL_HEIGHT - ball.height
    end
		-- ...
end
```

### 遊戲規則
* 在這章中,我們要為pong遊戲設置規則,讓遊戲可以順利的進行
* **得分** : 當球碰到兩側邊界時,我們要在對應的玩家分數上加1
* **輸家發球** : 為了遊戲的公平性,我們設置了當玩家A得分時 下球開始時必定往玩家A發球,同時螢幕上顯示這次由誰發球(往另一方向開始移動) 
* **勝利** : 最後要制定勝利條件, 當某一方超過10分時,要在螢幕上顯示誰是勝利玩家,並且可以按Enter後重新開始新遊戲

```lua
-- 將移動速度存在ball class中 x = ball.speedX y = ball.speedY

function love.load()
		-- ...
    -- 勝利字體
    victoryFont = love.graphics.newFont(FONTPATH, 42)
    -- 分數
    player1Scroe = 0
    player2Scroe = 0

    --儲存贏家
    winnerPlayer = 0

    -- 判斷由誰發球
    servingPlayer = math.random(2) == 1 and 1 or 2

    if servingPlayer == 1 then
        ball.dx = ball.speedX
    elseif servingPlayer == 2 then
        ball.dx = -ball.speedX
    end
		-- ...
end

function love.update(dt)
    -- =======控制碰撞=======
    if gamestat == 'play' then
        -- 球碰到兩側時
        if ball.x <= 0 then
            -- 對應玩家分數增加
            player2Scroe = player2Scroe + 1
            ball:reset()
            -- 設置這球由誰發球
            servingPlayer = 1
            -- 設置球往贏家開始
            ball.dx = ball.speedX
            -- 超過勝利分數時設置勝利贏家
            if player2Scroe >= 3 then
                gamestat = 'victory'
                winnerPlayer = 2
            else
                gamestat = 'serve'
            end
        elseif ball.x >= VIRTUAL_WIDTH - ball.width then
            player1Scroe = player1Scroe + 1
            ball:reset()
            servingPlayer = 2
            ball.dx = -ball.speedX
            if player1Scroe >= 3 then
                gamestat = 'victory'
                winnerPlayer = 1
            else
                gamestat = 'serve'
            end
        end
        if ball:collides(paddle1) then
            ball.dx = -ball.dx
        end

        if ball:collides(paddle2) then
            ball.dx = -ball.dx
        end

        if ball.y <= 0 then
            ball.dy = -ball.dy
            ball.y = 0
        end

        if ball.y >= VIRTUAL_HEIGHT - ball.height then
            ball.dy = -ball.dy
            ball.y = VIRTUAL_HEIGHT - ball.height
        end
    end
		-- ...
end

-- user 按下按鍵時觸發
function love.keypressed(key)
    -- 按下esc鍵時觸發關閉螢幕
    if key == 'escape' then
        love.event.quit()
    elseif key == 'enter' or key == 'return' then
        -- 按下enter時開始遊戲
        if gamestat == 'start' then
            gamestat = 'serve'
        elseif gamestat == 'victory' then
            -- 勝利時返回開始狀態
            gamestat = 'start'
            -- 分數歸0
            player1Scroe = 0
            player2Scroe = 0
        elseif gamestat == 'serve' then
            gamestat = 'play'
        end
    end
end

function love.draw()
    -- start end包圍的區塊以push設置的格式來顯示 這裡表示用虛擬螢幕的方式渲染畫面
    push:apply('start')

		-- 設置各種狀態時顯示的文字
    if gamestat == 'start' then
        love.graphics.printf('Welcome to Pong!', 0, 20, VIRTUAL_WIDTH, 'center')
        love.graphics.printf('Press Enter to Play', 0, 32, VIRTUAL_WIDTH, 'center')
    elseif gamestat == 'serve' then
        love.graphics.printf("Player " .. tostring(servingPlayer) .. "'s turn!", 0, 20, VIRTUAL_WIDTH, 'center')
        love.graphics.printf('Press Enter to Serve', 0, 32, VIRTUAL_WIDTH, 'center')
    elseif gamestat == 'victory' then
        love.graphics.setFont(victoryFont)
        love.graphics.printf("Player " .. tostring(winnerPlayer) .. " win!", 0, 20, VIRTUAL_WIDTH, 'center')
        love.graphics.setFont(smallFont)
        love.graphics.printf('Press Enter to Serve', 0, 42, VIRTUAL_WIDTH, 'center')
    end
		-- ...
		push:apply('end')
end
```

### 音樂
* 在音樂項目中,我們要為遊戲加入效果音
* 首先下載[Bfxr](https://www.bfxr.net/),這是一款提供各種遊戲音效的軟體,我們將音效下載為`.wav`檔後放到程式目錄中
* 接著使用`love.audio.newSource(path, [type])`來建立音檔物件,`path`是檔案路徑 `type`是資源類型, 要播放音檔使用`sourceObj:play()`來播放,其中`sourceObj`是`newSource()`方法建立出來的物件
* 在程式中我們分別在下面3種時機播放音效
	1. 觸碰球拍時
	2. 碰撞上下邊界時
	3. 碰撞左右邊界時 

```lua
function love.load()
	-- ...
	--[[
        建立音效物件 透過sounds['paddle_hit']:play()播放音效
        paddle_hit: 撞擊球拍, wall_hit:撞擊上下, point_scored:撞擊左右
    ]]
    sounds = {
        ['paddle_hit'] = love.audio.newSource('paddle_hit.wav', 'static'),
        ['wall_hit'] = love.audio.newSource('wall_hit.wav', 'static'),
        ['point_scored'] = love.audio.newSource('point_scored.wav', 'static')
    }
end

function love.update(dt)
    -- 在碰撞物件時播放音效
    if gamestat == 'play' then
        -- 球碰到兩側時
        if ball.x <= 0 then
            -- 播放撞擊音效
            sounds['point_scored']:play()
            -- 對應玩家分數增加
            player2Scroe = player2Scroe + 1
            ball:reset()
            -- 設置這球由誰發球
            servingPlayer = 1
            -- 設置球往贏家開始
            ball.dx = ball.speedX
            -- 超過勝利分數時設置勝利贏家
            if player2Scroe >= 3 then
                gamestat = 'victory'
                winnerPlayer = 2
            else
                gamestat = 'serve'
            end
        elseif ball.x >= VIRTUAL_WIDTH - ball.width then
            sounds['point_scored']:play()
            player1Scroe = player1Scroe + 1
            ball:reset()
            servingPlayer = 2
            ball.dx = -ball.speedX
            if player1Scroe >= 3 then
                gamestat = 'victory'
                winnerPlayer = 1
            else
                gamestat = 'serve'
            end
        end
        if ball:collides(paddle1) then
            sounds['paddle_hit']:play()
            ball.dx = -ball.dx
        end

        if ball:collides(paddle2) then
            sounds['paddle_hit']:play()
            ball.dx = -ball.dx
        end

        if ball.y <= 0 then
            sounds['wall_hit']:play()
            ball.dy = -ball.dy
            ball.y = 0
        end

        if ball.y >= VIRTUAL_HEIGHT - ball.height then
            sounds['wall_hit']:play()
            ball.dy = -ball.dy
            ball.y = VIRTUAL_HEIGHT - ball.height
        end
    end
		-- ...
end
```

### 打包成執行檔
* 最後遊戲完成時,握們需要讓其他朋友在沒有love2D的環境下也能遊玩,所以要將程式打包成exe執行檔
* 步驟參考[Love官網](https://love2d.org/wiki/Game_Distribution_(%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87))
	1. 將有`main.lua`所在的資料夾所有程式打包成zip檔(注意是壓縮所有程式不是該資料夾,且除了zip外其他壓縮檔格式都不可以)
	2. 修改zip檔名成`xxx.love`
	3. 將檔案放到`love.exe`所在目錄
	4. 執行`$ copy /b love.exe+xxx.love gameName.exe`
* 產生出的exe檔及可在不同環境下執行

