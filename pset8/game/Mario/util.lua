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