# [Problem Set 8 Games-Pong](https://cs50.harvard.edu/x/2020/tracks/games/pong/)
---
### Pong: The AI Update
* 在Pong的作業中,我們要為player1(或player2)實施基本的AI控制
* 我們要嘗試在球移動讓球拍自己動起來,AI的難度取決於自己,只要確保AI也能勝利即可

#### Answer
* 這裡我設置了多種遊戲模式, 並在開始遊戲時(gamestate == 'start')可以選擇要玩哪種
	* **PVP** 玩家1 vs 玩家2
	* **PVC EASY** 玩家1 vs AI 簡單版
	* **PVC NEVER WIN** 玩家1 vs AI 你永遠不會贏
* 藉由新增`gamePattern`變數來控制遊玩模式,在開始時可以用`up`或`down`來改變變數值
