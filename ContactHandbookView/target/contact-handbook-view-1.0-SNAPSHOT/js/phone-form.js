var phones = [];
var phoneId;

function showPhoneForm(object, isEdit) {
    var template = Mustache.render(
        '<div class="modal" id="modal-phone-window">' +
        	'<div class="modal-content">' +
        		'<div class="close flex flex-row end" id="close-phone-form">&times;</div>' +
        		'<div class="flex flex-row center">' +
        		    '<div class="header">Номер телефона</div>' +
        		'</div>' +
        		'<div class="flex flex-row center">' +
        		    '<label for="country-code">Код страны</label>' +
        		    '<input type="text" id="country-code" value="{{countryCode}}">' +
        		'</div>' +
        		'<div class="flex flex-row center descr-wrong">' +
                    'Код страны введен некорректно' +
                '</div>' +
        		'<div class="flex flex-row center">' +
        		    '<label for="operator-code">Код оператора</label>' +
        		    '<input type="text" id="operator-code" value="{{operatorCode}}">' +
        		'</div>' +
        		'<div class="flex flex-row center descr-wrong">' +
                    'Код оператора введен некорректно' +
                '</div>' +
        		'<div class="flex flex-row center mb-10px">' +
        		    '<label for="phone">Телефон *</label>' +
        		    '<input type="text" id="phone" value="{{phoneNumber}}">' +
        		'</div>' +
        		'<div class="flex flex-row center descr-wrong">' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<div class="header">Тип телефона</div>' +
                '</div>' +
                '<div class="flex flex-row center mb-10px">' +
                    '<input id="pt-1" type="radio" name="phone-type" value="1" checked>' +
                    '<label for="pt-1" class="lbl-flex">Мобильный</label>' +
                    '<input id="pt-2" type="radio" name="phone-type" value="2">' +
                    '<label for="pt-2" class="lbl-flex">Домашний</label>' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<div class="header">Комментарий</div>' +
                '</div>' +
                '<div class="flex flex-row center mb-10px">' +
                    '<textarea class="w-100" id="comment">' +
                        '{{comment}}' +
                    '</textarea>' +
                '</div>' +
                '<div class="flex flex-row end">' +
                    '<button type="button" id="add-phone-btn" disabled>Добавить телефон</button>' +
                '</div>' +
        	'</div>' +
        '</div>',
        object
    );
    document.getElementById('modal-window').insertAdjacentHTML('beforeend', template);

    if (isEdit) {
        document.getElementById('add-phone-btn').innerHTML = 'Сохранить изменения';
        document.getElementById('add-phone-btn').disabled = false;
        document.getElementById('add-phone-btn').addEventListener('click', editPhone);

        var phoneTypes = document.getElementsByName('phone-type');
        if (object.phoneType === 'Домашний' || object.phoneType === 2) {
            phoneTypes[1].checked = true;
            phoneTypes[0].checked = false;
        } else {
            phoneTypes[1].checked = false;
            phoneTypes[0].checked = true;
        }
    } else {
        document.getElementById('add-phone-btn').addEventListener('click', addPhone);
    }

    document.getElementById('comment').rows = 10;

    document.getElementById('close-phone-form').addEventListener('click', function() {
        var modalWindow = document.getElementById('modal-phone-window');
        modalWindow.remove();
    });

    document.getElementById('country-code').addEventListener('keyup', validCountryOrOperatorCode);
    document.getElementById('operator-code').addEventListener('keyup', validCountryOrOperatorCode);
    document.getElementById('phone').addEventListener('keyup', validPhoneNumber);

    setMaxPhoneFieldsLength();
}

function addPhone() {
    var country = document.getElementById('country-code').value;
    var operator = document.getElementById('operator-code').value;
    var ph = document.getElementById('phone').value;
    var comm = document.getElementById('comment').value;

    var phType = document.getElementsByName('phone-type');

    var phone = {};

    if (phones.length === 0) {
        phone.id = 1;
    } else {
        phone.id = parseInt(phones[phones.length - 1].id, 10) + 1;
    }
    phone.countryCode = country;
    phone.operatorCode = operator;
    phone.phoneNumber = ph;
    phone.comment = comm;
    phone.enabled = true;

    if (phType[0].checked) {
        phone.phoneType = 'Мобильный';
    } else {
        phone.phoneType = 'Домашний';
    }

    phones.push(phone);

    var table = document.getElementById('phones');

    var template = Mustache.render(
        '<tr>' +
            '<td><input type="checkbox" value="{{id}}" name="select-phones"></td>' +
            '<td><a id="next-a">{{#countryCode}}+{{countryCode}}{{/countryCode}} {{#operatorCode}}({{operatorCode}}){{/operatorCode}} {{phoneNumber}}</a><input type="hidden" value="{{id}}"></td>' +
            '<td>{{phoneType}}</td>' +
            '<td>{{comment}}</td>' +
        '</tr>',
        phone
    );

    table.insertAdjacentHTML('beforeend', template);

    document.getElementById('next-a').addEventListener('click', function() {
        for (var i = 0; i < phones.length; i++) {
            if (phones[i].id == this.nextElementSibling.value) {
                phoneId = phones[i].id;
                showPhoneForm(phones[i], true);
                return;
            }
        }
    });
    document.getElementById('next-a').id = null;

    var boxes = document.querySelectorAll('input[name="select-phones"]');
    for (var i = 0; i < boxes.length; i++) {
        boxes[i].addEventListener('change', checkSelectedPhones);
    }

    document.getElementById('modal-phone-window').remove();
}

function editPhone() {
    var country = document.getElementById('country-code').value;
    var operator = document.getElementById('operator-code').value;
    var ph = document.getElementById('phone').value;
    var comm = document.getElementById('comment').value;

    var phType = document.getElementsByName('phone-type');

    if (phType[0].checked) {
        phType = 'Мобильный';
    } else {
        phType = 'Домашний';
    }

    for (var i = 0; i < phones.length; i++) {
        if (phones[i].id == phoneId) {
            phones[i].countryCode = country;
            phones[i].operatorCode = operator;
            phones[i].phoneNumber = ph;
            phones[i].phoneType = phType;
            phones[i].comment = comm;

            break;
        }
    }

    var row = document.getElementById('phones').firstChild;

    while (row !== null) {
        if (row.firstChild.firstChild.value == phoneId) {
            var element = row.firstChild.nextElementSibling;

            var str = '';
            if (country !== '') {
                str += '+' + country;
            }
            if (operator !== '') {
                str += ' (' + operator + ') ';
            }
            str += ph;

            element.firstChild.innerHTML = str;

            element = element.nextElementSibling;

            element.innerHTML = phType;

            element = element.nextElementSibling;
            element.innerHTML = comm;
        }
        row = row.nextElementSibling;
    }

    document.getElementById('modal-phone-window').remove();
}

function deletePhones() {
    var boxes = document.getElementsByName('select-phones');

    for (var i = 0; i < boxes.length; i++) {
        if (boxes[i].checked) {
            for (var j = 0; j < phones.length; j++) {
                if (phones[j].id == boxes[i].value) {
                    if (phones[j].enabled === null) {
                        phones[j].enabled = false;
                    } else {
                        phones.splice(j, 1);
                    }

                    boxes[i].parentNode.parentNode.remove();
                    i--;

                    break;
                }
            }
        }
    }
}

function checkSelectedPhones() {
    var boxes = document.querySelectorAll('input[name="select-phones"]');
    var btn = document.getElementById('delete-phone-btn');

    for (var i = 0; i < boxes.length; i++) {
        if (boxes[i].checked) {
            btn.disabled = false;
            return;
        }
    }

    btn.disabled = true;
}