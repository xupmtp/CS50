# [Problem Set 8 Games-Mario](https://cs50.harvard.edu/x/2020/tracks/games/mario/)
---
### Mario : The Goal Update
在mario作業中,我們要完成以下幾項目標

* 理解mario中的所有程式
* 加入最後的勝利旗幟及金字塔(如題目頁面所示)
* 加入勝利畫面或加載新關卡

### Answer
* `Map.lua` 加入金字塔及勝利旗幟
```lua
-- ...
-- 勝利旗幟
PILLAR_TOP = 8
PILLAR_BODY = 12
PILLAR_BOTTOM = 16
FLAG = 13

function Map:init()
		-- ...

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
```
* `Player.lua` 加入winner變數,當玩家碰到勝利旗幟後,將停止所有動作,並且在上方顯示勝利文字

```lua
-- 是否勝利
WINNER = false

self.behaviors = {
	-- ...
	
	-- 新增狀態,勝利時完全停止無法動作
	['stop'] = function(dt)
            self.dx = 0
            self.dy = 0
            WINNER = true
        end
}

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
        self.state = 'jumping'

				-- 若是碰到勝利旗幟則動作暫停
        if self.map:pillarCollides(self.map:tileAt(self.x - 1, self.y)) or
            self.map:pillarCollides(self.map:tileAt(self.x - 1, self.y + self.height - 1)) then
            self.state = 'stop'
            self.animation = self.animations['idle']
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

				-- 若是碰到勝利旗幟則動作暫停
        if self.map:pillarCollides(self.map:tileAt(self.x + self.width, self.y)) or
            self.map:pillarCollides(self.map:tileAt(self.x + self.width, self.y + self.height - 1)) then
            self.state = 'stop'
            self.animation = self.animations['idle']
        end
    end
end

function Player:render()
    local scaleX = self.direction == 'right' and 1 or -1
    love.graphics.draw(self.texture, self.animation:getCurrentFrame(),
                     math.floor(self.x + self.width / 2), math.floor(self.y + self.height / 2),
                    0, scaleX, 1, self.width / 2, self.height / 2)

    -- 若是勝利狀態則渲染出勝利文字
    if WINNER then
				-- 顯示文字在角色上方
        love.graphics.printf("You win!", self.x - VIRTUAL_WIDTH / 2, 20, VIRTUAL_WIDTH, 'center')
    end
end
```
