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