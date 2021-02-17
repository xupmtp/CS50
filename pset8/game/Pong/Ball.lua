Ball = Class{}

-- 建立球物件
function Ball:init(x, y, width, height)
    self.x = x
    self.y = y

    -- 記住初始位置 每次從此位置重新開始
    self.initX = x
    self.initY = y
    self.width = width
    self.height = height

    self.speedX = 150
    self.speedY = 90
    -- 初始方向及速度設為隨機數
    self.dx = math.random(2) == 1 and self.speedX or -self.speedX
    self.dy = math.random(-self.speedY, self.speedY)
end

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