Map = Class{}
-- 要使用util的內容所有須import
require 'util'
require 'Player'

-- 在spritesheet圖片切割後,第1格為磁磚,第4格為空白
TILE_BRICK = 1
TILE_EMPTY = 4
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
-- 被撞過的驚嘆磚
JUMP_BLOCK_HIT = 9

-- 勝利旗幟
PILLAR_TOP = 8
PILLAR_BODY = 12
PILLAR_BOTTOM = 16
FLAG = 13

function Map:init()
    self.spritesheet = love.graphics.newImage('graphics/spritesheet.png')
    self.music = love.audio.newSource('sounds/music.wav', 'static')

    self.tileWidth = 16
    self.tileHeight = 16
    -- 代表要繪製到畫面上的元件寬度/高度的數量 目前不知(未切割)所以先隨便設個值
    self.mapWidth = 30
    self.mapHeight = 28

    self.camX = 0
    self.camY = -3
    self.tiles = {}
    self.player = Player(self)
    
    -- 取得切割後的圖片table
    self.tileSprites = generateQuads(self.spritesheet, self.tileWidth, self.tileHeight)

    -- 紀錄生成物件的總長寬
    self.mapWidthPixels = self.mapWidth * self.tileWidth
    self.mapHeightPixels = self.mapHeight * self.tileHeight

    -- 設置畫面上半部為空白
    for y = 1, self.mapHeight do
        for x = 1, self.mapWidth do
            self:setTile(x, y, TILE_EMPTY)
        end
    end

    local x = 1
    -- 建立隨機物件
    while x < self.mapWidth - 12 do
        -- 建立雲物件 至少剩2格寬才擺的下雲 1/20機率出現
        if x < self.mapWidth - 2 and math.random(20) == 1 then
            -- 隨機高度
            local cloudStart = math.random(self.mapHeight / 2 - 6)

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
        -- 10%機率什麼都不做,空白空間
        else
            x = x + 2
        end
    end


    -- 金字塔
    for i = 6, 1, -1 do
        self:createFloor(self.mapWidth - i - 6)
        for j = i, 6 do
            self:setTile(self.mapWidth - i - 6, self.mapHeight / 2 - (6 - j + 1), TILE_BRICK)
        end
    end

    -- 旗幟
    self:setTile(self.mapWidth - 2, self.mapHeight / 2 - 8, FLAG)
    self:setTile(self.mapWidth - 1, self.mapHeight / 2 - 8, PILLAR_TOP)
    self:setTile(self.mapWidth - 1, self.mapHeight / 2 - 7, PILLAR_BODY)
    self:setTile(self.mapWidth - 1, self.mapHeight / 2 - 6, PILLAR_BODY)
    self:setTile(self.mapWidth - 1, self.mapHeight / 2 - 5, PILLAR_BODY)
    self:setTile(self.mapWidth - 1, self.mapHeight / 2 - 4, PILLAR_BODY)
    self:setTile(self.mapWidth - 1, self.mapHeight / 2 - 3, PILLAR_BODY)
    self:setTile(self.mapWidth - 1, self.mapHeight / 2 - 2, PILLAR_BODY)
    self:setTile(self.mapWidth - 1, self.mapHeight / 2 - 1, PILLAR_BOTTOM)

    --最後的地板
    for i = 0, 6 do
        self:createFloor(self.mapWidth - i)
    end


    self.music:setLooping(true)
    self.music:setVolume(0.25)
    self.music:play()
end

-- 建立隨機物件下面的地板
function Map:createFloor(x)
    for y = self.mapHeight / 2, self.mapHeight do
        self:setTile(x, y, TILE_BRICK)
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
        MUSHROOM_TOP, MUSHROOM_BOTTOM, PILLAR_TOP,
        PILLAR_BODY, PILLAR_BOTTOM
    }

    --迭代collidables,若tile物件是障礙物回傳true
    for _, v in ipairs(collidables) do
        if tile.id == v then
            return true
        end
    end

    return false
end

-- 檢查是否有碰到勝利旗
function Map:pillarCollides(tile)
    local collidables = {
        PILLAR_TOP, PILLAR_BODY, PILLAR_BOTTOM
    }

    --迭代collidables,若tile物件是障礙物回傳true
    for _, v in ipairs(collidables) do
        if tile.id == v then
            return true
        end
    end

    return false
end

function Map:update(dt)
    -- 這裡邏輯要從最下層的min()往上看 
                -- 當從起點(0)到x距離不到畫面一半 畫面不動 camX值為0
    self.camX = math.max(0, 
                    -- 若x沒超出地圖 則當x超出畫面一半 畫面開始移動 camX值為 player.x - VIRTUAL_WIDTH / 2
                    math.min(self.player.x - VIRTUAL_WIDTH / 2,
                    -- 當x超出地圖, 畫面不會超出地圖最右邊 camX永遠為self.mapWidthPixels - VIRTUAL_WIDTH
                    math.min(self.mapWidthPixels - VIRTUAL_WIDTH, self.player.x)))
    self.player:update(dt)

end

-- 繪製地圖畫面
function Map:render()
    for y = 1, self.mapHeight do
        for x = 1, self.mapWidth do
            love.graphics.draw(self.spritesheet, self.tileSprites[self:getTile(x, y)],
            (x - 1) * self.tileWidth, (y - 1) * self.tileHeight)
        end
    end
    self.player:render()
end