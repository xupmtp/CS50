var quote = {};

$(function(){
    $("#search").on('click', function(){
        $.ajax({
            url : 'getStock',
            type : 'get',
            data : {
                symbol : $("input[name='symbol']").val()
            },
            dataType : 'json'
        }).done((data) => {
            $("#modalText").html(data.detail)
            $("#stockModal").modal('show')
        }).fail((XMLHttpRequest, textStatus, errorThrown) => {
            window.location.href = "apology?errmsg=INVALID SYMBOL";
        })
    })
})