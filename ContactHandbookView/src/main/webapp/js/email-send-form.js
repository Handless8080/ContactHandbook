var addressees;
var selectedAddressees;

var emailXHR = new XMLHttpRequest();
emailXHR.onreadystatechange = function() {
    if (emailXHR.readyState === XMLHttpRequest.DONE) {
        var object = JSON.parse(emailXHR.responseText);
        addressees = [];
        selectedAddressees = [];

        for (var i = 0; i < object.length; i++) {
            var addressee = {};
            addressee.fullName = object[i].surname + ' ' + object[i].name + ' ' + object[i].patronymic;
            addressee.id = object[i].id;
            addressee.email = object[i].email;

            addressees.push(addressee);
            selectedAddressees.push(false);
        }
    }
};

var sendEmailXHR = new XMLHttpRequest();

function showAddresseeForm() {
    var template = Mustache.render(
        '<div class="modal" id="modal-addressees-window">' +
        	'<div class="modal-content">' +
        	    '<div class="close flex flex-row end" id="close-addresses-form">&times;</div>' +
        		'<table class="w-100">' +
                    '<thead>' +
                        '<tr>' +
                            '<th class="cbox"></th>' +
                            '<th>Полное имя</th>' +
                            '<th>E-Mail</th>' +
                        '</tr>' +
                    '</thead>' +
                    '<tbody>' +
                        '{{#.}}' +
                        '<tr>' +
                            '<td><input type="checkbox" value="{{id}}" name="select-emails" {{#selected}}checked{{/selected}}></td>' +
                            '<td><a>{{fullName}}</a></td>' +
                            '<td>{{email}}</td>' +
                        '</tr>' +
                        '{{/.}}' +
                    '</tbody>' +
                '</table>' +
                '<div class="flex flex-row end">' +
                    '<button type="button" id="select-addressee">Выбрать</button>' +
                '</div>' +
        	'</div>' +
        '</div>',
        addressees
    );

    document.body.insertAdjacentHTML('beforeend', template);
    document.getElementById('close-addresses-form').addEventListener('click', function() {
    	document.getElementById('modal-addressees-window').remove();
    });

    for (var i = 0; i < addressees.length; i++) {
        if (addressees[i].selected) {
            selectedAddressees[i] = true;
        } else {
            selectedAddressees[i] = false;
        }
    }

    var a = document.getElementsByName('select-emails');
    for (var i = 0; i < a.length; i++) {
        a[i].addEventListener('change', function() {
            var id = this.value;
            for (var i = 0; i < addressees.length; i++) {
                if (addressees[i].id == id) {
                    if (this.checked) {
                        selectedAddressees[i] = true;
                    } else {
                        selectedAddressees[i] = false;
                    }
                    break;
                }
            }
        });
    }

    document.getElementById('select-addressee').addEventListener('click', function() {
        var list = document.getElementById('addressees');
        list.innerHTML = '';

        for (var i = 0; i < addressees.length; i++) {
            if (selectedAddressees[i]) {
                var p = document.createElement('div');
                p.innerHTML = addressees[i].email;
                list.appendChild(p);

                addressees[i].selected = true;
            } else {
                addressees[i].selected = null;
            }
        }
        validAllEmailFields();
        document.getElementById('modal-addressees-window').remove();
    });
}

function showEmailForm() {
    var template = Mustache.render(
        '<div class="modal" id="modal-email-window">' +
        	'<div class="modal-content">' +
        		'<div class="close flex flex-row end" id="close-email-form">&times;</div>' +
                '<div class="flex flex-row end">' +
                    '<button type="button" id="add-addressee">Выбрать адресатов</button>' +
                '</div>' +
                '<div class="flex flex-row end">' +
                    '<div class="flex flex-column" id="addressees">' +
                    '</div>' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<div class="header">Тема</div>' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<input type="text" id="subject" class="w-100">' +
                '</div>' +
                '<div class="flex flex-row center descr-wrong">' +
                    'Тема не может быть пустой' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<div class="header">Сообщение</div>' +
                '</div>' +
                '<div class="flex flex-row">' +
                    '<span>Шаблоны: </span>' +
                    '<button class="ml-10px" type="button" id="st-surname">Фамилиия</button>' +
                    '<button class="ml-10px" type="button" id="st-name">Имя</button>' +
                    '<button class="ml-10px" type="button" id="st-patronymic">Отчество</button>' +
                '</div>' +
                '<div class="flex flex-row center mb-10px">' +
                    '<textarea class="w-100" id="text"></textarea>' +
                '</div>' +
                '<div class="flex flex-row center descr-wrong">' +
                    'Сообщение не может быть пустым' +
                '</div>' +
                '<div class="flex flex-row end">' +
                    '<button type="button" id="send-email-btn" disabled>Отправить</button>' +
                '</div>' +
        	'</div>' +
        '</div>',
        {}
    );

    document.body.insertAdjacentHTML('beforeend', template);
    document.getElementById('add-addressee').addEventListener('click', showAddresseeForm);
    document.getElementById('close-email-form').addEventListener('click', function() {
    	var modalWindow = document.getElementById('modal-email-window');
    	modalWindow.remove();
    });
    document.getElementById('send-email-btn').addEventListener('click', sendEmails);

    document.getElementById('subject').addEventListener('keyup', validSubjectAndMessage);
    document.getElementById('text').addEventListener('keyup', validSubjectAndMessage);

    document.getElementById('text').rows = 20;

    document.getElementById('st-name').addEventListener('click', function() { insertTemplate(' <name>') });
    document.getElementById('st-surname').addEventListener('click', function() { insertTemplate(' <surname>') });
    document.getElementById('st-patronymic').addEventListener('click', function() { insertTemplate(' <patronymic>') });

    emailXHR.open('POST', url + '/emails', true);
    emailXHR.send();
}

function sendEmails() {
    this.disabled = true;

    var subject = document.getElementById('subject').value;
    var text = document.getElementById('text').value;

    sendEmailXHR.open('POST', url + '/send-messages', true);

    var formData = new FormData();
    formData.append('subject', subject);
    formData.append('text', text);

    var j = 0;
    for (var i = 0; i < addressees.length; i++) {
        if (addressees[i].selected) {
            formData.append('email-id' + j, addressees[i].id);
            formData.append('email' + j++, addressees[i].email);
        }
    }

    document.getElementById('modal-email-window').remove();
    sendEmailXHR.send(formData);

    var message = document.getElementById('message');
    message.style.animationName = 'fade';
    setTimeout(function() { document.getElementById('message').removeAttribute('style') }, 5000);
}

function validSubjectAndMessage() {
    if (this.value.length === 0) {
        this.classList.add('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'flex';
    } else {
        this.classList.remove('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'none';
    }

    validAllEmailFields();
}

function validAllEmailFields() {
    var btn = document.getElementById('send-email-btn');

    var text = document.getElementById('text').value;
    var subject = document.getElementById('subject').value;

    var selected = false;
    for (var i = 0; i < addressees.length; i++) {
        if (addressees[i].selected) {
            selected = true;
            break;
        }
    }

    if (selected && text.length !== 0 && subject.length !== 0) {
        btn.disabled = false;
    } else {
        btn.disabled = true;
    }
}

function insertTemplate(template) {
    var textarea = document.getElementById('text');
    var start = textarea.selectionStart;
    var end = textarea.selectionEnd;

    if (textarea.textLength !== 0 && textarea.textLength !== start) {
        textarea.value = textarea.value.substring(0, start) + template + textarea.value.substring(end, textarea.value.length);
    } else {
        textarea.value += template;
    }

    var event = new Event('keyup');
    textarea.dispatchEvent(event);

    textarea.focus();
    var caretPos = start + template.length;
    textarea.setSelectionRange(caretPos, caretPos);
}