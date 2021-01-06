-- 定義螢幕長寬 大寫表示為常數
WINDOW_WIDTH = 800
WINDOW_HEIGHT = 600

-- 定義虛擬螢幕長寬比
VIRTUAL_WIDTH = 400
VIRTUAL_HEIGHT = 300

-- 字體路徑
FONTPATH = 'font2.ttf'
-- 移動的像素距離(PX)
PADDLE_SPEED = 200

-- import library
push = require 'push'
Class = require 'class'
-- 使用其他檔案的程式
require 'Paddle'
require 'Ball'

function love.load()
    -- 設置亂數種子
    math.randomseed(os.time())
    -- 過濾螢幕顯示方式為像素顯示
    love.graphics.setDefaultFilter('nearest', 'nearest')

    -- 建立小字體
    smallFont = love.graphics.newFont(FONTPATH, 16)
    -- 建立大字體
    scoreFont = love.graphics.newFont(FONTPATH, 32)
    -- 勝利字體
    victoryFont = love.graphics.newFont(FONTPATH, 42)
    -- 分數
    player1Scroe = 0
    player2Scroe = 0
    aa = 0

    --[[
        建立音效物件 透過sounds['paddle_hit']:play()播放音效
        paddle_hit: 撞擊球拍, wall_hit:撞擊上下, point_scored:撞擊左右
    ]]
    sounds = {
        ['paddle_hit'] = love.audio.newSource('paddle_hit.wav', 'static'),
        ['wall_hit'] = love.audio.newSource('wall_hit.wav', 'static'),
        ['point_scored'] = love.audio.newSource('point_scored.wav', 'static')
    }

    -- 初始化球拍及球改調用class init方法
    paddle1 = Paddle(5, 20, 5, 20)
    paddle2 = Paddle(VIRTUAL_WIDTH - 10, VIRTUAL_HEIGHT - 40, 5, 20)
    ball = Ball(VIRTUAL_WIDTH / 2 - 2, VIRTUAL_HEIGHT / 2 - 2, 5, 5)

    -- 判斷遊戲是否開始
    gamestat = 'start'
    gamePatterns = {
        'pvp',
        'pvcEasy',
        'pvcDevil'
    }
    -- lua陣列值從1開始
    gamePattern = 1

    -- 設置AI是否移動
    AIMove = true
    -- 設置AI移動/停止間隔
    AIMoveTime = math.random(4)

    --儲存贏家
    winnerPlayer = 0

    -- 判斷由誰發球
    servingPlayer = math.random(2) == 1 and 1 or 2

    if servingPlayer == 1 then
        ball.dx = ball.speedX
    elseif servingPlayer == 2 then
        ball.dx = -ball.speedX
    end

    -- 改調用push的螢幕設置 同時設置虛擬螢幕及原本螢幕的大小
    push:setupScreen(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, WINDOW_WIDTH, WINDOW_HEIGHT, {
        fullscreen = false,
        vsync = true,
        resizable = false
    })
end

--[[
控制按鍵動作
dt表示自上次調用update後過多久(秒)
love會在每一幀調用此函數
]]
function love.update(dt)
    -- AIMoveTime - dt模擬移動/暫停xx秒
    AIMoveTime = AIMoveTime - dt * 2
    if (AIMoveTime <= 0) then
        -- 時間到時切換狀態
        AIMove = not AIMove
        -- 移動時間較長是為了平衡AI強度
        if AIMove then
            AIMoveTime = math.random(4)
        else
            AIMoveTime = math.random(2)
        end
    end
    -- =======控制碰撞=======
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

        -- 設置AI或玩家時的動作
        if gamePatterns[gamePattern] == 'pvp' then
            if love.keyboard.isDown('up') then
                paddle2.dy = - PADDLE_SPEED
            elseif love.keyboard.isDown('down') then
                paddle2.dy = PADDLE_SPEED
            else
                paddle2.dy = 0
            end
        -- AIMove == true 才可以移動
        elseif gamePatterns[gamePattern] == 'pvcEasy' and AIMove then
            if ball.y > paddle2.y + paddle2.height then
                paddle2.dy = PADDLE_SPEED
            elseif ball.y < paddle2.y then
                paddle2.dy = -PADDLE_SPEED
            else
                paddle2.dy = 0
            end
        -- 魔王關 永遠移動到球的Y軸位置
        elseif gamePatterns[gamePattern] == 'pvcDevil' then
            if ball.y > paddle2.y + paddle2.height then
                paddle2.dy = PADDLE_SPEED
            elseif ball.y < paddle2.y then
                paddle2.dy = -PADDLE_SPEED
            else
                paddle2.dy = 0
            end
        end
    else
        paddle2.dy = 0
    end
    -- =======控制球拍=======
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
    elseif key == 'up' then
        if gamestat == 'start' then
            gamePattern = (gamePattern - 1) <= 0 and #gamePatterns or (gamePattern - 1)
        end
    elseif key == 'down' then
        if gamestat == 'start' then
            gamePattern = (gamePattern) % (#gamePatterns) + 1
        end
    end
end

function love.draw()
    -- start end包圍的區塊以push設置的格式來顯示 這裡表示用虛擬螢幕的方式渲染畫面
    push:apply('start')

    -- 重設背景色 參數值介於0~1分255等分
    love.graphics.clear(40 / 255, 45 / 255, 52 / 255, 255 / 255)

    if gamestat == 'start' then
        love.graphics.printf('Welcome to Pong!', 0, 16, VIRTUAL_WIDTH, 'center')
        
        local printTexts = {
            'P vs P',
            'P vs C Easy',
            'P vs C Hard'
        }
        local printHeight = 44
        for i = 1, #printTexts do
            if i == gamePattern then
                love.graphics.setColor(0, 1, 0, 1)
            end
            love.graphics.printf(printTexts[i], 0, printHeight, VIRTUAL_WIDTH, 'center')
            love.graphics.setColor(1, 1, 1, 1)
            printHeight = printHeight + 16
        end
    elseif gamestat == 'serve' then
        love.graphics.printf("Player " .. tostring(servingPlayer) .. "'s turn!", 0, 20, VIRTUAL_WIDTH, 'center')
        love.graphics.printf('Press Enter to Serve', 0, 32, VIRTUAL_WIDTH, 'center')
    elseif gamestat == 'victory' then
        love.graphics.setFont(victoryFont)
        love.graphics.printf("Player " .. tostring(winnerPlayer) .. " win!", 0, 20, VIRTUAL_WIDTH, 'center')
        love.graphics.setFont(smallFont)
        love.graphics.printf('Press Enter to Serve', 0, 62, VIRTUAL_WIDTH, 'center')
    end

    -- 繪製球
    ball:render()

    -- 繪製球拍
    paddle1:render()
    paddle2:render()

    -- 設置小字體 然後渲染標題文字
    love.graphics.setFont(smallFont)

    -- 改設大字體 然後渲染兩個分數
    love.graphics.setFont(scoreFont)
    love.graphics.print(player1Scroe, VIRTUAL_WIDTH / 2 - 50, VIRTUAL_HEIGHT / 3)
    love.graphics.print(player2Scroe, VIRTUAL_WIDTH / 2 + 30, VIRTUAL_HEIGHT / 3)

    displayFPS()

    push:apply('end')
end

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