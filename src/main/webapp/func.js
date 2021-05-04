/*functions for index.html*/

/**
 * Function fills in the select control with categories.
 */
function fillInCategorySelectCtrl() {
    let fullPath = window.location.href;
    let rootPath = fullPath.substring(0, fullPath.lastIndexOf('/')) + '/';
    $.ajax({
        type: 'GET',
        url: rootPath + 'categories',
        dataType: 'json',
    }).done(function (data) {
        let categories = data;
        categories.sort(function(a, b) {
            return parseInt(a.id) - parseInt(b.id);
        });
        let selectItems;
        for (let i = 0; i < categories.length; i++) {
            selectItems += "<option value='" + categories[i].id + "'>" + categories[i].name + "</option>";
        }
        $("#categorySelect").append(selectItems);
    });
    return true;
}

/**
 * Function sends json with Item(task) data to the server and runs refreshing tasks' table
 * on the page if required.
 *
 * @param json JSON with Item data.
 * @param noRefreshTable Boolean parameter for further items' table refreshing.
 */
function sendItem(json, noRefreshTable) {
    let fullPath = window.location.href;
    let rootPath = fullPath.substring(0, fullPath.lastIndexOf('/')) + '/';
    $.ajax({
        type: 'POST',
        url: rootPath + 'items',
        data: JSON.stringify(json),
        dataType: 'json'
    }).done(function(data) {
        if (!noRefreshTable) {
            fillInTable();
        }
    });
}

/**
 * Function prepares JSON with new Item data and passes it to sendItem() function.
 * @returns {boolean}
 */
function createItem() {
    if ($('#description').val() ==="" || $('#categorySelect').val() ==="") {
        return false;
    }
    let categoryIds = $('#categorySelect').val();
    let json = {
        id: 0,
        description: $('#description').val(),
        done: false,
        categories: categoryIds
    };
    sendItem(json, false);
    $('#description').val('');
    $("#categorySelect option:selected").prop("selected", false);
    return true;
}

/**
 * Function prepares JSON with Item's data and passes it to sendItem() function.
 * If ShowAll checkbox is checked the items' table won't be refreshed.
 * @param showAll Checkbox object - "Show All" on the index page.
 */
function changeItemStatus(checkBox) {
    const $values = $(checkBox).parent().parent().find('td');
    const $categNames = $values.eq(5)
        .find('.dropdown-item')
        .map(function () {
                return $(this).text();
            }
        ).get();
    let catIdsArray = [];
    $("#categorySelect option").each(function()
    {
        if (jQuery.inArray($(this).text(), $categNames) !== -1) {
            catIdsArray.push($(this).val())
        }
    });
    let json = {
        id: $values.eq(0).text(),
        description: $values.eq(1).text(),
        created: Date.parse($values.eq(2).text()),
        done: (checkBox.is(':checked')),
        categories: catIdsArray
    }
    sendItem(json, $("#showAll").is(':checked'));
}

/**
 * Function requests all items(tasks) from the server as JSON array and fills in the table.
 * If "Show All" checkbox is not checked only items with no done status are shown,
 * otherwise all items are shown.
 * The items are sorting by id before adding to the table.
 *
 * @returns {boolean}
 */

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

/**
 * Function receives an item and builds a row for the table.
 * @param data Item object.
 */
function addItemToTable(data) {
    let checked = data.done ? ' checked="checked" ' : "";
    $('#tbody').append('<tr>\n'
        + '<td class="align-middle">' + data.id + '</td>\n'
        + '<td class="align-middle">' + data.description + '</td>\n'
        + '<td class="align-middle">' + data.created + '</td>\n'
        + '<td class="align-middle">' + localStorage.getItem("todouser") + '</td>\n'
        + '<td class="align-middle"><input type="checkbox"' + checked + 'onclick="changeItemStatus($(this));"></td>\n'
        + '<td><div class = "btn-group dropright">\n'
        + '<a class = "btn btn-link dropdown-toggle align-middle" href="#" role = "button" id = "dropdownMenuLink"'
        + ' data-toggle = "dropdown" aria-haspopup = "true" aria-expanded = "false" ></a>\n'
        + '<div class="dropdown-menu" aria-labelledby="dropdownMenuLink" id="ddm-' + data.id + '">\n'
        + '</div>\n'
        + '</div>\n'
        + '<td></tr>');
    data.categories.forEach(function(categ){
        $('#ddm-' + data.id).append('<a class="dropdown-item" href="">' + categ.name + '</a>\n')
    });
}
/**
 * Function for logging out of logged user.
 * @returns {boolean}
 */
function signout() {
    let fullPath = window.location.href;
    let rootPath = fullPath.substring(0, fullPath.lastIndexOf('/'));
    localStorage.removeItem("todouser");
    window.location.replace(rootPath + '/auth');
    return true;
}

/*functions for signin.html*/

/**
 * Functions sets the button's label and the input's type on the sign-in/up page.
 * @returns {boolean}
 */
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

/**
 * Function validates the inputs on index.html page, sends the credentials as JSON and sets info messages
 * on the page depending on the response's type.
 *
 * @returns {boolean}
 */
function subm() {
    let isSignUp = $("#signInUp").is(':checked');
    if (isSignUp && ($("#inputName").val().length) === 0 || $("#inputPassword").val().length === 0) {
        return false;
    }
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