Player = Class{}

local MOVE_SPEED = 90
local JUMP_VELOCITY = 500
local GRAVITY = 20

-- 是否勝利
WINNER = false

function Player:init(map)
    self.width = 16
    self.height = 20
    self.map = map

    self.sounds = {
        ['jump'] = love.audio.newSource('sounds/jump.wav', 'static'),
        ['hit'] = love.audio.newSource('sounds/hit.wav', 'static'),
        ['coin'] = love.audio.newSource('sounds/coin.wav', 'static')
    }

    -- 出現在地圖的第10格位置
    self.x = map.tileWidth * 10
    -- map.mapHeight / 2 - 1 因為人物要從地上1格開始  - self.height則是要多減人物的高度
    self.y = map.tileHeight * (map.mapHeight / 2 - 1) - self.height

    -- 設置速率
    self.dx = 0
    self.dy = 0

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
        },
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
                self.sounds['jump']:play()
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
        end,
        ['stop'] = function(dt)
            self.dx = 0
            self.dy = 0
            WINNER = true
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

            if playCoin then
                self.sounds['coin']:play()
            elseif playHit then
                self.sounds['hit']:play()
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
        self.state = 'jumping'

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

        if self.map:pillarCollides(self.map:tileAt(self.x + self.width, self.y)) or
            self.map:pillarCollides(self.map:tileAt(self.x + self.width, self.y + self.height - 1)) then
            self.state = 'stop'
            self.animation = self.animations['idle']
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

    if WINNER then
        love.graphics.printf("You win!", self.x - VIRTUAL_WIDTH / 2, 20, VIRTUAL_WIDTH, 'center')
    end
end