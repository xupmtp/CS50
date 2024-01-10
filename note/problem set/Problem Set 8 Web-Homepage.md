# [Problem Set 8 Web-Homepage](https://cs50.harvard.edu/x/2020/tracks/web/homepage/)
---

### 題目說明

在Homepage作業中,我們須使用HTML，CSS和JavaScript構建一個簡單的主頁,其中需包含以下幾點條件
* 包含至少四個不同的.html頁面，其中至少一個是`index.html`（網站的主頁）並且應該可以通過一個或多個超鏈接從網站上的任何頁面轉到任何其他頁面
* 至少使用十個不同種類的HTML tag
* 將Bootstrap的一項或多項功能集成到網站中,[Bootstrap](https://getbootstrap.com/docs/4.1/getting-started/introduction/)是一個受歡迎的美化網站library
* 至少擁有一個自己創建的樣式表文件styles.css，其中使用至少五個不同的CSS選擇器，並且在樣式表中總共使用了至少五個不同的CSS屬性
* 將JavaScript的一項或多項功能集成到您的網站中，以使網站更具互動性
* 最後,確保您的網站在移動設備,筆記本電腦和桌上型電腦上的瀏覽器上看起來都不錯

### Answer
這裡製作了一個日文學習網站,總共包含4個頁面及其他js&css檔

* `index.html`首頁,可點選前往其他頁面
* `50.html`片假名50音表格
* `phrase.html`片語列表,包含搜尋特定句子功能
* `Reference.html`參考文獻,內含參考的網站連結
* 剩下文件為js&css以及圖片(`img/`資料夾下)

##### HTML

index.html
```html
<!DOCTYPE html>
<html lang="zh-TW">
    <head>
        <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css" rel="stylesheet">
        <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
        <link href="styles.css" rel="stylesheet">
        <title>My Study Website</title>
    </head>
    <body>
        <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
          <a class="navbar-brand" href="index.html">
            <img src="img/home.png" width="30" height="30" alt="home">
            Home
          </a>
          <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav">
              <li class="nav-item">
                <a class="nav-link" href="50.html">五十音</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" href="phrase.html">常用片語</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" href="Reference.html">參考來源</a>
              </li>
            </ul>
          </div>
        </nav>
        <div class="container contain">
            <div class="row">
                <a href="50.html" class="col content" id="fiftyWord">
                    <div>五十音</div>
                </a>
                <a href="phrase.html" class="col content" id="phrase">
                    <div>常用片語</div>
                </a>
            </div>
        </div>
    </body>
</html>
```

50.html
```html
<!DOCTYPE html>

<html lang="UTF-8">
    <head>
        <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css" rel="stylesheet">
        <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
        <link href="styles.css" rel="stylesheet">

        <title>50 hiragana</title>
    </head>
    <body>
        <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
          <a class="navbar-brand" href="index.html">
            <img src="img/home.png" width="30" height="30" alt="home">
            Home
          </a>
          <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav">
              <li class="nav-item">
                <a class="nav-link" href="50.html">五十音</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" href="phrase.html">常用片語</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" href="Reference.html">參考來源</a>
              </li>
            </ul>
          </div>
        </nav>
        <div class="container">
            <table class="table table-bordered" id="wordTable">
              <thead class="table-dark">
                <tr>
                  <th scope="col">平假名</th>
                  <th scope="col">a</th>
                  <th scope="col">i</th>
                  <th scope="col">u</th>
                  <th scope="col">e</th>
                  <th scope="col">o</th>
                </tr>
              </thead>
              <tbody id="wordTbody">
              </tbody>
            </table>
        </div>
        <script src="50.js"></script>
    </body>
</html>
```

phrase.html
```html
<!DOCTYPE html>

<html lang="en">
    <head>
        <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css" rel="stylesheet">
        <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
        <link href="styles.css" rel="stylesheet">
        <title>phrase</title>
    </head>
    <body>
        <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
          <a class="navbar-brand" href="index.html">
            <img src="img/home.png" width="30" height="30" alt="home">
            Home
          </a>
          <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav">
              <li class="nav-item">
                <a class="nav-link" href="50.html">五十音</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" href="phrase.html">常用片語</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" href="Reference.html">參考來源</a>
              </li>
            </ul>
          </div>
        </nav>
        <div class="container contain">
          <input type="text" class="form-control" placeholder="search rome word" id="search">
          <div class="table-responsive-lg">
            <table class="table contain">
              <thead class="table-dark">
                <tr>
                  <th scope="col"></th>
                  <th scope="col">片語</th>
                  <th scope="col">翻譯</th>
                </tr>
              </thead>
              <tbody id="pbody">

              </tbody>
            </table>
          </div>
        </div>
        <script src="phrase.js"></script>
    </body>
</html>
```

Reference.html
```html
<!DOCTYPE html>

<html lang="en">
    <head>
        <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css" rel="stylesheet">
        <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
        <link href="styles.css" rel="stylesheet">
        <title>Word</title>
    </head>
    <body>
        <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
          <a class="navbar-brand" href="index.html">
            <img src="img/home.png" width="30" height="30" alt="home">
            Home
          </a>
          <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav">
              <li class="nav-item">
                <a class="nav-link" href="50.html">五十音</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" href="phrase.html">常用片語</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" href="Reference.html">參考來源</a>
              </li>
            </ul>
          </div>
        </nav>
        <div class="container">
          <ul>
            <li><a href="https://zh.wikipedia.org/wiki/五十音">wikipedia</a></li>
            <li></li>
          </ul>
          <a href="https://www.hitutor.com.tw/free-japanese-conversation.php">日文會話</a>
        </div>
    </body>
</html>
```

#### CSS

style.css
```css
.contain{
    margin-top : 5%;
}
.content{
    height : 25vw;
    margin : 10px;
    text-align : center;
    line-height:25vw;
    color : black;
    font-size : 6rem;
}
#fiftyWord{
    background-color : #00FFFF;
}
#fiftyWord:hover{
    background-color : #00CACA;
}
#phrase{
    background-color : #FFDC35;
}
#phrase:hover{
    background-color : #EAC100;
}

a:hover{
    text-decoration:none;
    color : black;
}
#wordTable{
    margin : 20px;
}
table{
    text-align : center;
}
```

#### JS

50.js
```javaScript
var words = [{
    あ:'a',
    い:'i',
    う:'u',
    え:'e',
    お:'o',
},{
    か:'ka',
    き:'ki',
    く:'ku',
    け:'ke',
    こ:'ko'
},{
    さ:'sa',
    し:'shi',
    す:'su',
    せ:'se',
    そ:'so'
},{
    た:'ta',
    ち:'chi',
    つ:'tsu',
    て:'te',
    と:'to'
},{
    な:'na',
    に:'ni',
    ぬ:'nu',
    ね:'ne',
    の:'no'
},{
    は:'ha',
    ひ:'hi',
    ふ:'fu',
    へ:'he',
    ほ:'ho'
},{
    ま:'ma',
    み:'mi',
    む:'mu',
    め:'me',
    も:'mo'
},{
    や:'ya',
    '':'',
    ゆ:'yu',
    ' ':'',
    よ:'yo'
},{
    ら:'ra',
    り:'ri',
    る:'ru',
    れ:'re',
    ろ:'ro'
},{
    わ:'wa',
    '':'',
    ' ':'',
    '  ':'',
    を:'o'
},{
    ん:'n',
    '':'',
    ' ':'',
    ' ':'',
    '   ':'',
    '    ':'',
}]

var $tbody = $("#wordTbody")
for (let i in words) {
    let j = 0;
    let obj = words[i]
    let tr = $('<tr>')
    for (let w in obj) {
        if (j === 0) {
            tr.append($(`<th scope="row">${obj[w]}行</th>`))
        }
        let a = $("<a>").append(`<div>${w}<br>${obj[w]}</div>`);
        let href = !(w.trim()) ? "#" : `https://zh.wikipedia.org/wiki/${w}`;
        a.attr("href", href)
        tr.append($('<td>').append(a))
        j++;
    }
    $tbody.append(tr)
}
```

phrase.js
```javaScript
var words = [['おはようごぢいます', '(早上好)', 'o ha you go za i ma su'], ['こんにちは', '(您好,am.10:00~pm.5:00)', 'konnijiwa'],
         ['こんばんは', '(晚上好)', 'ko n ba n wa'], ['お休（やす）みなさい', '(晚安)', 'o yasu mi nasai'],
         ['どうもありがとうございます', '(謝謝)', ' do mo a li ga to go zayi ma su'], ['さようなら', '(再見)', 'sayona la'],
         ['はじめまして', '(你好,[初次見面])', 'ha ji me ma xi de'], ['どうぞよろしく', '(請多多關照)', 'do zoyo lo xi ku'],
         ['ありがとう', '(謝謝)', 'a li ga do (u)'], ['ごめんなさい', '(對不起)', ' go men nasai'], ['あしたまた', '(明天見)', 'a xi ta ma ta'],
         ['いいぇ,まだまだです', '(哪裏哪裏,還差得遠呢)', 'i i e ma da ma da de su'],
         ['大丈夫（だいじょぅぶ）です', '(沒關係的，沒事的)', ' da i jyou bu de su'], ['殘念（ざんねん）です', '(真可惜)', ' zannian de su'],
         ['おいしいですょ', '(很好吃啊)', 'o yi xi yi de suyo'], ['ちょっと待（ま）って', '(等一下)', ' jio to ma te'],
         ['はい、分（わか）りました', '(好的,沒問題/知道了/明白了)', ' hai, waka li ma xi ta'], ['もしもし', '(喂,你好[電話用語])', 'mo xi mo xi'],
         ['どなたですか', '(你是哪位啊? [電話用語])', 'do na da di suka'],
         ['もぅ一度（いちど）言（い）ってください', '(請再說一遍)', ' mo u i chi do i tte ku da sa i'],
         ['そぅしましぅょ', '(一言爲定)', ' so (u) xi ma xuo'], ['いらっしゃぃませ', '(歡迎光臨)', ' yi la xai ma se'],
         ['綺麗(きれい)です', '(很漂亮)', ' ki lie di su'], ['これは何（なん）ですか', '(這是什麽)', 'ko lie wa nan di suka'],
         ['どうしましたか', '(你怎麽啦?)', 'do xi ma xi ta ka'], ['知（し）りません', '(不知道)', 'xi li ma san'],
         ['お願（ねが）いします', '(那就拜托了)', 'o negaisi ma su'], ['かまいません', '（沒關 係）', 'ka ma yi ma sen'],
         ['失禮（しつれい）しました', '（失禮了）', 'xi zi lie xi ma xi ta'],
         ['こんなことは二度（にど）とおこりません', '（這樣的事不會再發生了）', 'konnako to wani do to oko li ma se n'],
         ['心配（しんぱい）しないでください、すべて昔（むかし）のことで', '（不要擔心，一切都會過去的）',
          'si n pai si na i de ku da sa i su be te mu ka si no ko to de'],
         ['元気（げんき）を出（だ）して', '（振作起來）', 'gen ki o da xi te'], [' おなかがすきましたか。', '（肚子餓嗎？）', 'o nakagasuki ma xi ta ka'],
         ['寒（さむ）くないですか', '（不冷嗎？）', 'sa mu gunai di suka'], ['お疲（ つか）れではないですか', '（不累嗎？）', 'o zika le de wanai di suka'],
         ['あのレストラン（restaurant）は美味（おい）しそうですよ。行ってみましょうか', '（那家餐館的飯菜好像挺好吃，我們去看看吧）',
          'a no le su to lanwa o yi xi sou de suyo、yi de mi ma xuoka'],
         ['また私（わたし）のそばに戻（もど）れないか。', '（你還會回到我的身邊嗎？）', 'ma to wa ta xi no so banimo do lie naika'],
         ['取（と）り替（か）えたほうがいいですね', '(該換新的了)', 'to li ka e ta ho gayi de su ne'],
         ['一生懸命勉强（いっしょうけんめいべんきょう）さえすれば、別（べつ）に難（むずか）しくはありません', '（只要努力學習，沒什麽特別難的）',
          'yixio ken mei ben kyosa e su lie ba、be zini mu zhuka xi kuwa a li ma sen'],
         ['私（わたし）はすでに伝（つた）えました。', ' （我已經告訴他了）', 'wo ta xi wasu de ni zit a e ma xi ta'],
         ['あなたは今（いま）どこにいらっしゃいますか？〔或（どこ？）〕', '（你現在在哪裏）', 'a na ta wayi ma do koniyi la xai ma suka'],
         ['褒（ほ）めすぎです', '（您過獎了）', 'ho me sugi de su'],
         ['読（よ）むだけで、話（はな）すことができません', '（只會看，不會講）', 'yo mud a ke de、ha nasuko to ga de ki ma sen'],
         ['いつも綺麗（きれい）に掃除（そうじ）してあります。', '（經常打掃得乾乾淨淨）', 'yi xi moki lie ni so u zhi xi te a li ma su'],
         ['私（わたし）には向（む）いていそうです。', '（好像適合我）', 'wa ta xi niwa mu yiteyi so u de su'],
         ['お役(やく)に立(た)つかどうかわかりませんが。', '(不知道您能不能用上)', 'o yakuni ta zika do u kawaka li ma senga'],
         ['お元気(げんき) ですか。', '（您還好吧）', 'o gen ki de suka'], ['いくらですか。', '（多少錢？）', 'yiku la de suka'],
         ['すみません。', '（不好意思/麻煩你）', 'su mi ma sen'], ['ごめんなさい。', '（對不起）', 'go men nasai'],
         ['どういうことですか。', '（什麽意思呢/怎麽回事？）', 'do u yuk o to de suka'], ['まだまだです。', '（差得遠了/一般/還不行）', 'ma ta ma ta de su'],
         ['どうしたの/どうしたんですか。', '（發生了什麽事啊？）', 'do u xi ta no/do u xi tan de suka'], ['なんでもない。', '（沒什麽事）', 'an de monai'],
         ['ちょっと待ってください。', '（請稍等一下）', 'juo to ma teku da sai'], ['約束(やくそく)します。', '（就這麽說定了）', 'yak u so ku xi ma su'],
         ['これでいいですか。', '（這樣可以嗎？）', 'ko lie de yi i de suka'], ['けっこうです/もういいです。', '（不用了）', 'keko de su/moyi i de su'],
         ['どうして/なぜ', '（爲什麽啊？）', 'do xi te/naze'], ['いただきます', '（那我開動了〔吃飯動 筷子前〕）', 'yi ta da ki ma su'],
         ['ごちそうさまでした。', '（我吃飽了/多謝款待）', 'go ji so sa ma de xi ta'], ['どういたしまして。', '（別客氣）', 'do yi ta xi ma xi te'],
         ['本當(ほんとう)ですか。', '（真的？）', 'hon to de suka'], ['嬉（うれし）い。', '（我好高興〔女性用語〕）', 'u lie xi i'],
         ['よし、いくぞ。', '（好！出發/行動〔男性用語〕）', 'yo xi yikuzo'], ['いってきます。', '（我走了/我出去了）', 'yiteki ma su'],
         ['いってらしゃい 。', '（您好走）', 'yite la xai'], ['また、どうぞお越(こ) しください。', '（歡迎下次光臨）', 'ma ta do zo o ko xi ku da sai'],
         ['じゃ、またね/では、また。', '（再見）', 'jia ma ta nie/de wama ta'], ['信(しん) じられない。', '（真令人難以相信）', 'xinji la lie nai'],
         ['どうも。', '（該詞意思模糊 。有問候、多謝、不好意思、對不起……）', 'do mo'], ['あ、そうだ。', '（啊，對了〔男性用語居多〕）', 'a so da'],
         ['えへ？', '（表示輕微驚訝的感嘆語）', 'e~'], ['うん、いいわよ。', '（恩，好的〔女性用語〕）', 'un yi i wayo'],
         ['ううん、そうじゃない。', '（不，不是那樣的〔女性用語〕）', 'um~ so jianai'], ['がんばってください。', '（加油）', 'ganbateku da sai'],
         ['がんばります。', '（我會加油的/我會努力的）', 'ganba li ma su'], ['ご苦労(くろう)  さま。', '（辛苦了〔用于上級對下級〕）', 'go ku lo sa ma'],
         ['お疲(つか)れさま。', '（辛苦了〔用于下級對上級和平級間〕）', 'o zika lie sa ma'], ['どうぞ遠慮(えんりょ) なく。', '（請別客氣）', 'do zo en liunaku'],
         ['おひさしぶりです/しばらくですね/お久（さ）しぶりです', '（好久不見了）', 'o hi sa xi bu li de su/xi ba la ku de sun e/o sa xi bu li de su'],
         ['お帰（かえ）り。', '（您回來啦）', 'o kai li'], ['ただいま。', '（我回來了）', 'tad a yi ma'],
         ['いよいよ僕（ぼく）の本番(ほんばん)だ。', '（總算到我正式出場了〔男性用語〕）', 'yiyoyiyoboku no hon ban da'],
         ['関系(かんけい) ないでしょう。', '（這和你沒關係吧？）', 'kankeinai de xuo'],
         ['電話番號(でんわばんごう) を教（おし）えてください', '（請告訴我您的電話號碼）', 'den wa ban go o o xi e teku da sai'],
         ['日本語(にほんご) はむずかしいことばがはなせませんが、やさしいことばがなんとかはなせます。', '（日語難的說不上來，簡單的還能對付幾句）',
          'Ni hon go wa mu suka xi ko to bag a wan a se ma senga,yasa xi ko to baga nan to kawana se ma su'],
         ['たいへん！', '（不得了啦）', 'tai hen'], ['おじゃまします。', '（打攪了。到別人的住所時進門時說的話。）', 'o jia ma xi ma su'],
         ['おじゃましました。', '（打攪了。離開別人的處所時 講的話。）', 'o jia ma xi ma xi ta'],
         ['いままでお世話（せわ）になにました/いままでありがとうございます。', '（多謝您長久以來的關照）',
          'yima ma de o se waninani ma xi ta/yima ma de a li go do go zayi ma su'],
         ['お待（ま）たせいたしました。', '（讓您久等了。）', 'o ma ta sei ta xi ma xi ta'],
         ['別(べつ)に。', '（沒什麽。當別人問你發生了什麽事時你的回答。）', 'be zini'],
         ['冗談(じょうだん) を言（い）わないでください。', '（請別開玩笑）', 'juodan o yiwanai de ku da sai'],
         ['お願（ねが）いします。', '（拜托了）', 'o niegai xi ma su'], ['そのとおりです。', '（說的對）', 'so no to o li de su'],
         ['なるほど。', '（原來如此啊）', 'nalu ho do'], ['どうしようかな/どうすればいい', '（我該怎麽辦啊？）', 'do xi yokana/do su lie bayi i']]

$("#search").on('input', () => {
 let char = $("#search").val().trim()
 wordFilter = words.filter(arr => arr[2].indexOf(char) != -1)
 $("#pbody").html('')
 createTable(wordFilter)
});

var createTable = (arr) => {
 let $pbody = $("#pbody")
 let n = 1
 for (let i in arr) {
  let ar = arr[i]
  let tr = $('<tr>')
  tr.append($(`<th scope="row">${n++}</th>`))
  tr.append($(`<td>${ar[0]}<br>${ar[2]}</td>`))
  tr.append($(`<td>${ar[1]}</td>`))
  $pbody.append(tr)
 }
};

createTable(words)
```

#### img/
* 在img資料夾內包含一`home.png`圖片