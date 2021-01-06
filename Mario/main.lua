-- 定義螢幕長寬 大寫表示為常數
WINDOW_WIDTH = 800
WINDOW_HEIGHT = 600

-- 定義虛擬螢幕長寬比
VIRTUAL_WIDTH = 400
VIRTUAL_HEIGHT = 300

Class = require 'class'
push = require 'push'

require 'Animation'
require 'Map'

-- 字體路徑
FONTPATH = 'font2.ttf'

function love.load()
    math.randomseed(os.time())

    map = Map()
    love.graphics.setDefaultFilter('nearest', 'nearest')
    push:setupScreen(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, WINDOW_WIDTH, WINDOW_HEIGHT, {
        fullscreen = false,
        resizbale = false,
        vsync = true
    })

    -- 在love.keyboard下面新增自訂的屬性及功能
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