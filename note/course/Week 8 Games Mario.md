# Week 8 Games Mario
---
### 目錄
1. [Map](#Map)
2. [控制移動](#控制移動)
3. [建立隨機物件](#建立隨機物件)
4. [建立角色](#建立角色)
5. [動畫](#動畫)
6. [Jump](#Jump)
7. [碰撞檢測](#碰撞檢測)
8. [音效](#音效)


### Map
* 在Map中, 我們要先切割由各個元件組成的`spritesheet.png`並取得所有切割後的圖片(切割由util完成並返回table),table中編號1開始每個編號代表我們縮需的各種圖片物件(EX:1是地板,4是透明塊)
* 接著建立一table並將它模擬成二維陣列(指定長寬),該陣列上每個值對應我們要渲染到背景上的物件代號
```lua
-- map中0可能代表地板物件,1是空白物件,2是障礙物...
map = {
	1, 1, 1, 1,
	1, 2, 1, 1,
	0, 0, 0, 0
}
```
* 最後遍歷`map` table,並且從座標(0, 0)開始依map中所代表的物件渲染到畫面上

* `util.lua` 將大圖拆分為多個小圖,`generateQuads()`返回小圖table
```lua 
--[[
    切割圖形取得所有切割後的圖片table
    atlas : 圖片物件
    tileWidth, tileHeight 要切割的小圖寬高
]]
function generateQuads(atlas, tileWidth, tileHeight)
    -- 計算切割出的圖片寬,高數量
    local sheetWidth = atlas:getWidth() / tileWidth
    local sheetHeight = atlas:getHeight() / tileHeight

    -- 索引從1開始
    local sheetCounter = 1
    local quads = {}

    for y = 0, sheetHeight - 1 do
        for x = 0, sheetWidth - 1 do
            --[[
                getDimensions()獲取圖像的寬高 width, height = Image:getDimensions( )
                newQuad()切割一個圖片 https://love2d.org/wiki/love.graphics.newQuad
            ]]
            quads[sheetCounter] = love.graphics.newQuad(x * tileWidth, y * tileHeight, tileWidth, tileHeight, atlas:getDimensions())
            sheetCounter = sheetCounter + 1
        end
    end

    return quads
end
```
* `Map.lua`加載畫面地圖圖案
```lua
Map = Class{}
-- 要使用util的內容所有須import
require 'util'

-- 在spritesheet圖片切割後,第1格為磁磚,第4格為空白
TILE_BRILK = 1
TILE_EMPTY = 4

function Map:init()
    self.spritesheet = love.graphics.newImage('graphics/spritesheet.png')
    self.tileWidth = 16
    self.tileHeight = 16
    -- 代表要繪製到畫面上的元件寬度/高度的數量 目前不知(未切割)所以先隨便設個值
    self.mapWidth = 30
    self.mapHeight = 28
    --[[
        模擬地圖上的物件,table只有一維陣列 這裡要模擬成2維陣列
        map = {
            0,0,0,
            0,0,0,
            1,1,1}
        透過固定寬的數量模擬2維,但實際上是一維的table
        其中0是空白圖片,1是瓷磚圖片,模擬地上的磁磚
        整個二維map代表畫面上的所有圖片物件
    ]]
    self.tiles = {}
    
    -- 取得切割後的圖片table
    self.tileSprites = generateQuads(self.spritesheet, self.tileWidth, self.tileHeight)

    -- 設置畫面上半部為空白
    for y = 1, self.mapHeight / 2 do
        for x = 1, self.mapWidth do
            self:setTile(x, y, TILE_EMPTY)
        end
    end

    -- 設置畫面下半部為磁磚
    for y = self.mapHeight / 2, self.mapHeight do
        for x = 1, self.mapWidth do
            self:setTile(x, y, TILE_BRILK)
        end
    end
end

-- 設置元素值 因為是一維陣列 所以設值要注意位置
function Map:setTile(x, y, tile)
    self.tiles[(y - 1) * self.mapWidth + x] = tile
end

-- 取得元素值
function Map:getTile(x, y)
    return self.tiles[(y - 1) * self.mapWidth + x]
end

-- 繪製地圖畫面
function Map:render()
    for y = 1, self.mapHeight do
        for x = 1, self.mapWidth do
            love.graphics.draw(self.spritesheet, self.tileSprites[self:getTile(x, y)],
            (x - 1) * self.tileWidth, (y - 1) * self.tileHeight)
        end
    end
end
```
* `main.lua` 加載map class來渲染畫面
```lua
-- 定義螢幕長寬 大寫表示為常數
WINDOW_WIDTH = 800
WINDOW_HEIGHT = 600

-- 定義虛擬螢幕長寬比
VIRTUAL_WIDTH = 400
VIRTUAL_HEIGHT = 300

Class = require 'class'
push = require 'push'

require 'Map'

-- 字體路徑
FONTPATH = 'font2.ttf'

function love.load()
    map = Map()
    love.graphics.setDefaultFilter('nearest', 'nearest')
    push:setupScreen(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, WINDOW_WIDTH, WINDOW_HEIGHT, {
        fullscreen = false,
        resizbale = false,
        vsync = true
    })
end

function love.update(dt)

end

function love.draw()
    push:apply('start')
    -- 背景設為藍天
    love.graphics.clear(108 / 255, 140 / 255, 1, 1)
    -- 渲染所有圖片物件
    map:render()
    push:apply('end')
end
```

### 控制移動
* 建完地圖後,我們要模擬人物移動讓整個畫面動起來
* `love.graphics.translate(dx, dy)` 讓畫面整個移動
* `Map.lua` 動態設置要移動的變數值大小
```lua
-- 人物移動速率
local SCROLL_SPEED = 62
function Map:init()
	-- ...
	-- 傳入translate()方法的參數值,告訴畫面要移動的距離
	self.camX = 0
  self.camY = -3
	-- 紀錄生成物件的總長寬 人物移動需在此範圍內
	self.mapWidthPixels = self.mapWidth * self.tileWidth
	self.mapHeightPixels = self.mapHeight * self.tileHeight
end

-- update函數中每當按下按鍵就不斷讓畫面移動
function Map:update(dt)
    -- 鍵盤移動螢幕 並且不能超出建立的物件寬高
    if love.keyboard.isDown('w') then
        self.camY = math.max(0, math.floor(self.camY + -SCROLL_SPEED * dt))
    elseif love.keyboard.isDown('a') then
        self.camX = math.max(0, math.floor(self.camX + -SCROLL_SPEED * dt))
    elseif love.keyboard.isDown('s') then
        self.camY = math.min(self.mapHeightPixels - VIRTUAL_HEIGHT, math.floor(self.camY + SCROLL_SPEED * dt))
    elseif love.keyboard.isDown('d') then
        self.camX = math.min(self.mapWidthPixels - VIRTUAL_WIDTH, math.floor(self.camX + SCROLL_SPEED * dt))
    end
end
```
* `main.lua` 呼叫`translate()`函數移動畫面

```lua
function love.draw()
    push:apply('start')
    --[[
        translate(dx,dy) 畫面向dx,dy方向整個移動,若要模擬人物向右移動則畫面要向左移動(dx為負數)
        使用push渲染x,y位置有小數值時,會發生失真的現象,所以這裡要將小數值轉整數
    ]]
    love.graphics.translate(-math.floor(map.camX), -math.floor(map.camY))
    -- 背景設為藍天
    love.graphics.clear(108 / 255, 140 / 255, 1, 1)
    -- 渲染所有圖片物件
    map:render()
    push:apply('end')
end
```

### 建立隨機物件
* 現在在地圖中,我們擁有天空跟地面(磁磚),接著我們要建立遊戲中的其他物件
* 其他所有的物件都是隨機出現的,每次開起遊戲時出現的位置都不相同
* `main.lua` 要讓隨機數變化需要設置seed,設置位置需在map物件建立前
```lua
function love.load()
    math.randomseed(os.time())
    map = Map()
		-- ...
end
```
* `Map.lua` 將原本建立磁磚改為隨機建立地圖物件

```lua
-- 宣告所有用到的隨機物件

-- 人物移動速率
SCROLL_SPEED = 62
-- 雲
CLOUD_LEFT = 6
CLOUD_RIGHT = 7
-- 草
BUSH_LEFT = 2
BUSH_RIGHT = 3
-- 蘑菇柱子
MUSHROOM_TOP = 10
MUSHROOM_BOTTOM = 11
-- 驚嘆號磚
JUMP_BLOCK = 5
-- ...
function Map:init()
	--...
	   -- 設置畫面上半部為空白
    for y = 1, self.mapHeight do
        for x = 1, self.mapWidth do
            self:setTile(x, y, TILE_EMPTY)
        end
    end

    local x = 1
    -- 原本只設地板,現在改隨機物件
    while x < self.mapWidth do
        -- 建立雲物件 至少剩2格寬才擺的下雲 1/20機率出現
        if x < self.mapWidth - 2 and math.random(20) == 1 then
            -- 隨機高度
            local cloudStart = math.random(self.mapHeight / 2 - 6)
						-- 建立雲
            self:setTile(x, cloudStart, CLOUD_LEFT)
            self:setTile(x + 1, cloudStart, CLOUD_RIGHT)
        end
        -- =============下面建立地面上的物件=============

        -- 5%機率出現蘑菇柱子
        if math.random(20) == 1 then
            self:setTile(x, self.mapHeight / 2 - 2, MUSHROOM_TOP)
            self:setTile(x, self.mapHeight / 2 - 1, MUSHROOM_BOTTOM)
            -- 建立地板
            self:createFloor(x)
            x = x + 1

        -- 10%機率生成灌木
        elseif math.random(10) == 1 and x < self.mapHeight - 3 then
            local bushLevel = self.mapHeight / 2 - 1

            -- 左邊灌木
            self:setTile(x, bushLevel, BUSH_LEFT)
            self:createFloor(x)
            x = x + 1

            -- 右邊灌木
            self:setTile(x, bushLevel, BUSH_RIGHT)
            self:createFloor(x)
            x = x + 1

        elseif math.random(10) ~= 1 then
            -- 建地板
            self:createFloor(x)
            -- 90% * 1/15的機率生成驚嘆號磚
            if math.random(15) == 1 then
                self:setTile(x, self.mapHeight / 2 - 4, JUMP_BLOCK)
            end
            x = x + 1
        -- 10%機率什麼都不做,空白空間(陷阱)
        else
            x = x + 2
        end
    end
end

-- 建立隨機物件下面的地板,常用到所以拉成函數
function Map:createFloor(x)
    for y = self.mapHeight / 2, self.mapHeight do
        self:setTile(x, y, TILE_BRICK)
    end
end
```

### 建立角色
* 有了地圖物件後,接著要新增一個可以操作的角色
* 角色須出現在地板上且可以左右移動
* 當角色移動到畫面的一半時畫面應該跟著角色移動
* 當畫面到地圖尾端時角色才可以走到畫面的最右邊
* `Map.lua` 新增角色屬性,原本按鍵控制螢幕移動改成角色移動

```lua
function Map:init()
--...
-- 新增player物件,並傳入自身map給player用
self.player = Player(self)
--...
end

-- 原本畫面移動改成根據角色位置移動畫面
-- 下面self.camX這段邏輯較複雜,可以拆成多個if..else..會較好理解
function Map:update(dt)
    -- 這裡邏輯要從最下層的min()往上看 
                -- 當從起點(0)到x距離不到畫面一半 畫面不動 camX值為0
    self.camX = math.max(0, 
                    -- 若x沒超出地圖 則當x超出畫面一半 畫面開始移動 camX值為 player.x - VIRTUAL_WIDTH / 2
                    math.min(self.player.x - VIRTUAL_WIDTH / 2,
                    -- 當x超出地圖, 畫面不會超出地圖最右邊 camX永遠為self.mapWidthPixels - VIRTUAL_WIDTH
                    math.min(self.mapWidthPixels - VIRTUAL_WIDTH, self.player.x)))
		-- 角色左右移動
    self.player:update(dt)
end

-- 繪製地圖畫面
function Map:render()
		-- ...
		-- 渲染角色
    self.player:render()
end
```

### 動畫
* 接著我們要為剛剛建立的角色加上移動動畫
* 當角色移動時要有走路動畫
* 當角色向左/右移動時人物需轉向該方向
* 當停止時要面向前方且停止走路動畫
* `Animation.lua` 加入此類別來建立並播放我們的動畫

```lua
Animation = Class{}

function Animation:init(params)
    -- 圖片
    self.texture = params.texture
    -- 切割後圖片
    self.frames = params.frames
    -- 動畫要執行的時間 也表示到下次動畫執行的間隔 or用法跟js一樣 a || b a沒值則b
    self.interval = params.interval or 0.05
    -- 經過時間
    self.timer = 0
    -- 當前的圖片
    self.currentFrame = 1
end

-- 取得目前動畫圖片
function Animation:getCurrentFrame()
    return self.frames[self.currentFrame]
end

-- 重設動畫
function Animation:reset()
    self.timer = 0
    self.currentFrame = 1
end



function Animation:update(dt)
    self.timer = self.timer + dt

    -- 若圖片table只有1張則沒有動畫
    if #self.frames == 1 then
        return self.currentFrame
    end

    -- 動畫執行時間為self.interval值到下個按鍵按下
    while self.timer > self.interval do
        self.timer = self.timer - self.interval

        -- 重複frames內的每個圖片
        self.currentFrame = (self.currentFrame + 1) % (#self.frames + 1)
        -- 防止currentFrame為0
        if self.currentFrame == 0 then self.currentFrame = 1 end
    end
end
```
* `Player.lua` 在人物動作時播放動畫

```lua
Player = Class{}

local MOVE_SPEED = 120

function Player:init(map)
    self.width = 16
    self.height = 20

    -- 出現在地圖的第10格位置
    self.x = map.tileWidth * 10
    -- map.mapHeight / 2 - 1 因為人物要從地上1格開始  - self.height則是要多減人物的高度
    self.y = map.tileHeight * (map.mapHeight / 2 - 1) - self.height

    self.texture = love.graphics.newImage('graphics/blue_alien.png')
    self.frames = generateQuads(self.texture, self.width, self.height)

    -- 判斷角色停止或移動
    self.state = 'idle'

    self.animations = {
        -- 若建構子傳入的參數是table一般寫法為Animation({}), lua允許其簡化為Animation{}
        ['idle'] = Animation {
            texture = self.texture,
            frames = {
                -- 第一張圖為停止時圖片
                self.frames[1]
            },
            -- 移動->停止->移動 在停止時會至少停留1秒
            interval = 1
        },
        ['walking'] = Animation {
            texture = self.texture,
            frames = {
                -- 這三張連起來是個走路的畫面
                self.frames[9], self.frames[10], self.frames[11]
            },
            -- 角色移動->停止時間較短
            interval = 0.15
        }
    }

    -- 渲染時顯示的畫面
    self.animation = self.animations['idle']

    self.behaviors = {
        -- 用法同js匿名函數
        ['idle'] = function(dt)
            if love.keyboard.isDown('a') then
                self.x = self.x - MOVE_SPEED * dt
                -- 按下按鍵時變成走路動畫
                self.animation = self.animations['walking']
                -- 角色往左走
                self.direction = 'left'
            elseif love.keyboard.isDown('d') then
                self.x = self.x + MOVE_SPEED * dt
                -- 按下按鍵時變成走路動畫
                self.animation = self.animations['walking']
                -- 角色往右走
                self.direction = 'right'
            else
                -- 沒按按鍵時變成停止動畫
                self.animation = self.animations['idle']
            end
        end,
        ['walking'] = function(dt)
            if love.keyboard.isDown('a') then
                self.x = self.x - MOVE_SPEED * dt
                self.animation = self.animations['walking']
                self.direction = 'left'
            elseif love.keyboard.isDown('d') then
                self.x = self.x + MOVE_SPEED * dt
                self.animation = self.animations['walking']
                self.direction = 'right'
            else
                self.animation = self.animations['idle']
            end
        end
    }
end

function Player:update(dt)
    -- 不同動作時執行不同行為
    self.behaviors[self.state](dt)
    -- 播放動畫
    self.animation:update(dt)
end

function Player:render()
    local scaleX = self.direction == 'right' and 1 or -1

    --[[
        https://love2d.org/wiki/love.graphics.draw
        渲染的動畫改成根據當前圖片(走或停)
        角色x, y設為整數是防止移動時模糊化
        根據官方文件sx,sy參數可以調整比例
        若縮放比例為負數則圖片就可以翻轉,-1可以整個翻轉圖片

        ox,oy是偏移的位置,預設是圖片左上角
        但這樣翻轉時會根據左上位置翻轉,看起來角色在閃現一樣,
        所以我們要根據角色的中心位置翻轉

        因為角色的中心位置改變了(ox,oy的設置)所以x,y也要改變位置
    ]]
    love.graphics.draw(self.texture, self.animation:getCurrentFrame(),
                     math.floor(self.x + self.width / 2), math.floor(self.y + self.height / 2),
                    0, scaleX, 1, self.width / 2, self.height / 2)
end
```

### Jump
* 接著我們要幫人物時現跳躍功能,讓它可以在遇到障礙物時跳過去
* 跳躍的重點是高度及重力,我們須設置一個角色跳躍的高度,當角色跳起後,我們要設置重力速率,讓它每幀以固定速率往下掉落,到達地面高度時便停止
* `main.lua` 我們要新增一個`wasPressed()`函數,該函數取代`isDown()`偵測按下的按鍵,而且每次按下後直到再次按下否則不會連續觸發事件
```lua
function love.load()
		-- ...
    --[[在love.keyboard下面新增自訂的屬性及功能
		在keypressed()儲存按下的按鍵給wasPressed()函數使用]]
    love.keyboard.keysPressed = {}
end

function love.keypressed(key)
    if key == 'escape' then
        love.event.quit()
    end
    -- 避免重複按下 所以在keypressed()事件中儲存按下的按鍵
    love.keyboard.keysPressed[key] = true
end

--[[在love.keyboard下面新增自訂的屬性及功能
    若在keypressed()事件中沒按下則返回的是false]]
function love.keyboard.wasPressed(key)
    return love.keyboard.keysPressed[key]
end

function love.update(dt)
    map:update(dt)

    -- 每過一幀便清空按過按鍵,這樣只會在按下按鍵的那一幀觸發事件
    love.keyboard.keysPressed = {}
end
```
* `Player.lua` 設置jumping動作,原本改變x,y值來移動改成設置dx,dy
```lua
Player = Class{}

local MOVE_SPEED = 120
local JUMP_VELOCITY = 400
local GRAVITY = 40

function Player:init(map)
   	-- ...
    -- 設置速率
    self.dx = 0
    self.dy = 0

    self.texture = love.graphics.newImage('graphics/blue_alien.png')
    self.frames = generateQuads(self.texture, self.width, self.height)

    -- 判斷角色停止或移動
    self.state = 'idle'

    self.animations = {
        -- ...
        -- 新增jump動畫
        ['jumping'] = Animation {
            texture = self.texture,
            frames = {
                -- 跳躍動畫圖片
                self.frames[3]
            },
            interval = 1
        }
    }

    -- 渲染時顯示停止的畫面
    self.animation = self.animations['idle']

    self.behaviors = {
        -- 用法同js匿名函數
        ['idle'] = function(dt)
            -- 這裡需要按鍵不會一直觸發,所以在main.lua自訂一個按鍵函數來實現
            if love.keyboard.wasPressed('space') then
                -- 往空中跳
                self.dy = -JUMP_VELOCITY
                -- 變為跳躍狀態
                self.state = 'jumping'
                -- 跳躍動畫
                self.animation = self.animations['jumping']
            elseif love.keyboard.isDown('a') then
                -- 之前改變self.x值,現在改成設置dx為正或負
                self.dx = -MOVE_SPEED
                -- 按下按鍵時變成走路動畫
                self.animation = self.animations['walking']
                -- 角色往左走
                self.direction = 'left'
            elseif love.keyboard.isDown('d') then
                self.dx = MOVE_SPEED
                -- 按下按鍵時變成走路動畫
                self.animation = self.animations['walking']
                -- 角色往右走
                self.direction = 'right'
            else
								-- 沒按按鍵時停止動作
                self.dx = 0
                -- 沒按按鍵時變成停止動畫
                self.animation = self.animations['idle']
            end
        end,
        ['walking'] = function(dt)
            if love.keyboard.wasPressed('space') then
                self.dy = -JUMP_VELOCITY
                self.state = 'jumping'
                self.animation = self.animations['jumping']
            elseif love.keyboard.isDown('a') then
                self.dx = -MOVE_SPEED
                self.animation = self.animations['walking']
                self.direction = 'left'
            elseif love.keyboard.isDown('d') then
                self.dx = MOVE_SPEED
                self.animation = self.animations['walking']
                self.direction = 'right'
            else
                self.dx = 0
                self.animation = self.animations['idle']
            end
        end,
        ['jumping'] = function(dt)
            -- 在空中也可以移動
            if love.keyboard.isDown('a') then
                self.direction = 'left'
                self.dx = -MOVE_SPEED
            elseif love.keyboard.isDown('d') then
                self.direction = 'right'
                self.dx = MOVE_SPEED
            else
                self.dx = 0
            end

            -- 在空中持續的落下
            self.dy = self.dy + GRAVITY

            -- 當到達地面時改成停止狀態
            if self.y >= map.tileHeight * (map.mapHeight / 2 - 1) - self.height then
                self.dy = 0
                self.state = 'idle'
                self.y = map.tileHeight * (map.mapHeight / 2 - 1) - self.height
                self.animation = self.animations['idle']
            end
        end
    }
end

function Player:update(dt)
    -- 不同動作時執行不同行為
    self.behaviors[self.state](dt)
    -- 播放動畫
    self.animation:update(dt)
    -- 之前在self.behaviors改變self.x的值來實現走動,現在改用dx(速度)
    self.x = self.x + self.dx * dt
    self.y = self.y + self.dy * dt
end
```

### 碰撞檢測
* 接著我們要對障礙物進行碰撞檢測
	* 當角色遇到障礙物時必須停止移動
	* 遇到空洞時必須掉下去
	* 撞到驚嘆號磚時必須改變其圖案
* `map.lua` 我們要修改tileAt()函數並新增一檢測碰撞用的函數collides()

```lua
-- 修改tileAt()函數的回傳值為table,加入幾樣我們需要的資訊
function Map:tileAt(x, y)
    -- x,y各加1是因為索引從1開始,floor()會自動去掉小數
    return {
        -- 在table中的x,y方向中分別是第x,y個
        x = math.floor(x / self.tileWidth) + 1,
        y = math.floor(y / self.tileHeight) + 1,
        -- 該圖案代表的數字
        id = self:getTile(math.floor(x / self.tileWidth) + 1, math.floor(y / self.tileHeight) + 1)
    }
end

-- 檢查碰撞的物件是否為障礙物,是回傳true
function Map:collides(tile)
    -- 障礙物的table集合
    local collidables = {
        TILE_BRICK, JUMP_BLOCK, JUMP_BLOCK_HIT,
        MUSHROOM_TOP, MUSHROOM_BOTTOM
    }

    --迭代collidables,若tile物件是障礙物回傳true
    for _, v in ipairs(collidables) do
        if tile.id == v then
            return true
        end
    end

    return false
end
```
* `Player.lua` 我們必須分別對角色的4個頂點(上2下2)進行碰撞檢測,並且在左右移動及跳躍時都要做檢測,遇到障礙物時須停止動作

```lua
-- ...

function Player:init(map)
    -- ...
    self.behaviors = {
        -- 停止狀態下不用進行碰撞檢測
        ['idle'] = function(dt)
            -- 這裡需要按鍵不會一直觸發,所以在main.lua自訂一個按鍵函數來實現
            if love.keyboard.wasPressed('space') then
                -- 往空中跳
                self.dy = -JUMP_VELOCITY
                -- 變為跳躍狀態
                self.state = 'jumping'
                -- 跳躍動畫
                self.animation = self.animations['jumping']
            elseif love.keyboard.isDown('a') then
                -- 之前改變self.x值,現在改成設置dx為正或負
                self.dx = -MOVE_SPEED
                self.state = 'walking'
                -- 按下按鍵時變成走路動畫
                self.animation = self.animations['walking']
                -- 角色往左走
                self.direction = 'left'
            elseif love.keyboard.isDown('d') then
                self.dx = MOVE_SPEED
                self.state = 'walking'
                -- 按下按鍵時變成走路動畫
                self.animation = self.animations['walking']
                -- 角色往右走
                self.direction = 'right'
            else
                self.dx = 0
            end
        end,
        ['walking'] = function(dt)
            if love.keyboard.wasPressed('space') then
                self.dy = -JUMP_VELOCITY
                self.state = 'jumping'
                self.animation = self.animations['jumping']
            elseif love.keyboard.isDown('a') then
                self.dx = -MOVE_SPEED
                self.animation = self.animations['walking']
                self.direction = 'left'
            elseif love.keyboard.isDown('d') then
                self.dx = MOVE_SPEED
                self.animation = self.animations['walking']
                self.direction = 'right'
            else
                self.dx = 0
                self.state = 'idle'
                self.animation = self.animations['idle']
            end

            -- check for collisions moving left and right
            self:checkRightCollision()
            self:checkLeftCollision()

            -- 檢查腳下是否有磁磚
            if not self.map:collides(self.map:tileAt(self.x, self.y + self.height)) and
                not self.map:collides(self.map:tileAt(self.x + self.width - 1, self.y + self.height)) then
                    self.state = 'jumping'
                    self.animation = self.animations['jumping']
            end

        end,
        ['jumping'] = function(dt)
            -- 在空中也可以移動
            if love.keyboard.isDown('a') then
                self.direction = 'left'
                self.dx = -MOVE_SPEED
            elseif love.keyboard.isDown('d') then
                self.direction = 'right'
                self.dx = MOVE_SPEED
            else
                self.dx = 0
            end

            -- 在空中持續的落下
            self.dy = self.dy + GRAVITY

            -- 如果落下時有碰到障礙物或地板才停止
            if self.map:collides(self.map:tileAt(self.x, self.y + self.height)) or
                self.map:collides(self.map:tileAt(self.x + self.width - 1, self.y + self.height)) then
                self.dy = 0
                self.state = 'idle'
                self.y = (self.map:tileAt(self.x, self.y + self.height).y - 1) * self.map.tileHeight - self.height
                self.animation = self.animations['idle']
            end

            -- 檢測左右碰撞
            self:checkRightCollision()
            self:checkLeftCollision()
        end
    }
end

function Player:update(dt)
    -- 不同動作時執行不同行為
    self.behaviors[self.state](dt)
    -- 播放動畫
    self.animation:update(dt)
    -- 之前在self.behaviors改變self.x的值來實現走動,現在改用dx(速度)
    self.x = math.max(0, self.x + self.dx * dt)
    self:calculateJumps()
    self.y = self.y + self.dy * dt

    -- 掉下去後過一陣子重新開始
    if self.y >= map.tileHeight * (map.mapHeight / 2 + 30) then
        self:restart()
    end
end

-- 處理跳躍時的碰撞
function Player:calculateJumps()
    -- 角色跳起時檢查碰撞
    if self.dy < 0 then
        -- 如果角色左側頂點和右側頂點(左右可能同時頂到不同磚塊)撞到障礙物
        if self.map:collides(self.map:tileAt(self.x, self.y)) or
            self.map:collides(self.map:tileAt(self.x + self.width - 1, self.y)) then
            -- 動作停止
            self.dy = 0

            -- 左右頂點撞到時都要改變驚嘆磚圖案
            if self.map:tileAt(self.x, self.y).id == JUMP_BLOCK then
                self.map:setTile(math.floor(self.x / self.map.tileWidth) + 1,
                    math.floor(self.y / self.map.tileHeight) + 1, JUMP_BLOCK_HIT)
            end
            if self.map:tileAt(self.x + self.width -1, self.y).id == JUMP_BLOCK then
                self.map:setTile(math.floor((self.x + self.width -1) / self.map.tileWidth) + 1,
                    math.floor(self.y / self.map.tileHeight) + 1, JUMP_BLOCK_HIT)
            end
        end
    end
end

-- 左邊碰撞檢測
function Player:checkLeftCollision()
    if self.dx < 0 then
        -- 若上下有一邊遇到障礙物
        if self.map:collides(self.map:tileAt(self.x - 1, self.y)) or
            self.map:collides(self.map:tileAt(self.x - 1, self.y + self.height - 1)) then
                -- 停止動作
                self.dx = 0
                -- x位置變為障礙物右邊位置
                self.x = self.map:tileAt(self.x - 1, self.y).x * self.map.tileWidth
        end
    end
end

-- 右邊碰撞檢測
function Player:checkRightCollision()
    if self.dx > 0 then
        -- 右邊檢測加上角色寬度
        if self.map:collides(self.map:tileAt(self.x + self.width, self.y)) or
            self.map:collides(self.map:tileAt(self.x + self.width, self.y + self.height - 1)) then
                self.dx = 0
                self.x = (self.map:tileAt(self.x + self.width, self.y).x - 1) * self.map.tileWidth - self.width
        end
    end
end


-- 重新開始
function Player:restart()
    -- 出現在地圖的第10格位置
    self.x = map.tileWidth * 10
    -- map.mapHeight / 2 - 1 因為人物要從地上1格開始  - self.height則是要多減人物的高度
    self.y = map.tileHeight * (map.mapHeight / 2 - 1) - self.height
end
-- ...
```

### 音效
* 最後我們要幫遊戲加上音效,在上一個遊戲中已經介紹過如何加入,這裡直接來看要加入的位置
* `Map.lua` 在地圖上我們要加入背景音樂,該音樂需要不斷循環播放
```lua
function Map:init()
    self.spritesheet = love.graphics.newImage('graphics/spritesheet.png')
    self.music = love.audio.newSource('sounds/music.wav', 'static')
		
		-- ...
		
		-- 設定循環播放
    self.music:setLooping(true)
		-- 音量變小,該參數值需為0~1之間
    self.music:setVolume(0.25)
    self.music:play()
end
```
* `Player.lua` 在人物音效上我們加入跳躍及撞到磚塊時的音效

```lua
function Player:init(map)
    self.width = 16
    self.height = 20
    self.map = map

		-- 加入音效
    self.sounds = {
        ['jump'] = love.audio.newSource('sounds/jump.wav', 'static'),
        ['hit'] = love.audio.newSource('sounds/hit.wav', 'static'),
        ['coin'] = love.audio.newSource('sounds/coin.wav', 'static')
    }
		
		-- ...
		
		['idle'] = function(dt)
            -- 這裡需要按鍵不會一直觸發,所以在main.lua自訂一個按鍵函數來實現
            if love.keyboard.wasPressed('space') then
                -- 往空中跳
                self.dy = -JUMP_VELOCITY
                -- 變為跳躍狀態
                self.state = 'jumping'
                -- 跳躍動畫
                self.animation = self.animations['jumping']
								--播放跳躍音效
                self.sounds['jump']:play()
            elseif love.keyboard.isDown('a') then
                -- 之前改變self.x值,現在改成設置dx為正或負
                self.dx = -MOVE_SPEED
                self.state = 'walking'
                -- 按下按鍵時變成走路動畫
                self.animation = self.animations['walking']
                -- 角色往左走
                self.direction = 'left'
            elseif love.keyboard.isDown('d') then
                self.dx = MOVE_SPEED
                self.state = 'walking'
                -- 按下按鍵時變成走路動畫
                self.animation = self.animations['walking']
                -- 角色往右走
                self.direction = 'right'
            else
                self.dx = 0
            end
        end,
        ['walking'] = function(dt)
            if love.keyboard.wasPressed('space') then
                self.dy = -JUMP_VELOCITY
                self.state = 'jumping'
                self.animation = self.animations['jumping']
								--播放跳躍音效
                self.sounds['jump']:play()
								
				-- ...
				
-- 處理跳躍時的碰撞
function Player:calculateJumps()
    -- 角色跳起時檢查碰撞
    if self.dy < 0 then
        -- 金色磚:coin音效 棕色磚:hit音效
        local playCoin = false
        local playHit = false

        -- 如果角色左側頂點和右側頂點(左右可能同時頂到不同磚塊)撞到障礙物
        if self.map:collides(self.map:tileAt(self.x, self.y)) or
            self.map:collides(self.map:tileAt(self.x + self.width - 1, self.y)) then
            -- 動作停止
            self.dy = 0

            -- 左右頂點撞到時都要改變驚嘆磚圖案
            if self.map:tileAt(self.x, self.y).id == JUMP_BLOCK then
                self.map:setTile(math.floor(self.x / self.map.tileWidth) + 1,
                    math.floor(self.y / self.map.tileHeight) + 1, JUMP_BLOCK_HIT)
                    playCoin = true
            else
                playHit = true
            end
            if self.map:tileAt(self.x + self.width -1, self.y).id == JUMP_BLOCK then
                self.map:setTile(math.floor((self.x + self.width -1) / self.map.tileWidth) + 1,
                    math.floor(self.y / self.map.tileHeight) + 1, JUMP_BLOCK_HIT)
                    playCoin = true
            else
                playHit = true
            end

						-- 有撞到磚塊時播放音效
            if playCoin then
                self.sounds['coin']:play()
            elseif playHit then
                self.sounds['hit']:play()
            end
        end
    end
end
```

