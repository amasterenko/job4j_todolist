/*functions for index.html*/
function sendItem(json) {
    let fullPath = window.location.href;
    let rootPath = fullPath.substring(0, fullPath.lastIndexOf('/')) + '/';
    $.ajax({
        type: 'POST',
        url: rootPath + 'items',
        data: JSON.stringify(json),
        dataType: 'json'
    }).done(function(data) {
        fillInTable();
    });
}
function createItem() {
    if ($('#description').val() ==="") {
        return false;
    }
    let json ={};
    json.id = 0;
    json.description = $('#description').val();
    json.created = Date.now();
    json.done = false;
    sendItem(json);
    $('#description').val('');
    return true;
}
function changeItemStatus(checkbox) {
    let json = {};
    const $values = $(checkbox).parent().parent().find('td');
    json.id = $values.eq(0).text();
    json.description = $values.eq(1).text();
    json.created = Date.parse($values.eq(2).text());
    json.done = (checkbox.is(':checked'));
    sendItem(json);
}
function fillInTable() {
    let fullPath = window.location.href;
    let rootPath = fullPath.substring(0, fullPath.lastIndexOf('/')) + '/';
    $.ajax({
        type: 'GET',
        url: rootPath + 'items',
        dataType: 'json',
    }).done(function (data) {
        $("#tbody").empty();
        let items = data;
        items.sort(function(a, b) {
            return parseInt(a.id) - parseInt(b.id);
        });
        for (let j = 0; j <= items.length; j++) {
            if ($("#showAll").is(':checked') || !items[j].done) {
                addItemToTable(items[j]);
            }
        }
    });
    return true;
}
function addItemToTable(data) {
    let checked = data.done ? ' checked="checked" ' : "";
    $('#tbody').append('<tr>\n'
        + '<td>' + data.id + '</td>\n'
        + '<td>' + data.description + '</td>\n'
        + '<td>' + data.created + '</td>\n'
        + '<td>' + localStorage.getItem("todouser") + '</td>\n'
        + '<td><input type="checkbox"' + checked + 'onclick="changeItemStatus($(this));">'
        + '</td>\n</tr>"')
}
function logout() {
    let fullPath = window.location.href;
    let rootPath = fullPath.substring(0, fullPath.lastIndexOf('/'));
    localStorage.removeItem("todouser");
    window.location.replace(rootPath + '/auth');
    return true;
}
/*functions for signin.html*/
function setSignMode() {
    let inpType, btnText;
    if ($("#signInUp").is(":checked")) {
        inpType = "text";
        btnText = "Sign up";
    } else {
        inpType = "password";
        btnText = "Sign in";
    }
    $("#inputPassword").prop('type', inpType);
    $("#submit").contents().eq(0).replaceWith(btnText);
    $("#msg").empty();
    return true;
}
function subm() {
    let isSignUp = $("#signInUp").is(':checked');
    let fullPath = window.location.href;
    let rootPath = fullPath.substring(0, fullPath.lastIndexOf('/'));
    let json = {
        username: $("#inputName").val(),
        password: $("#inputPassword").val()
    };
    let target = isSignUp ? "/reg" : "/auth";
    $.ajax({
        type: "POST",
        url: rootPath + target,
        data: JSON.stringify(json),
        dataType: "json"
    }).done(function(data) {
        if (!isSignUp && data.code===1) {
            localStorage.setItem("todouser", $("#inputName").val());
            window.location.replace(rootPath + '/index.html');
            return true;
        }
        let msg = data.message;
        let msgClass = data.code===1 ? 'alert alert-success' : 'alert alert-warning';
        $('#msg').html('<div class="' + msgClass + '">' + msg + '</div>');
    });
    return true;
}