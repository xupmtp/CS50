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

-- 建立球拍物件
function Paddle:render()
    love.graphics.rectangle('fill', self.x, self.y , self.width, self.height)
end