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
