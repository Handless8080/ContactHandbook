const url = '/contact-handbook-view-1.0-SNAPSHOT';

var page = 1;
var pagesCount = 1;

var isFiltered = false;

var xhr = new XMLHttpRequest();
xhr.onreadystatechange = function() {
    if (xhr.readyState === XMLHttpRequest.DONE) {
        clearTable();
        var object = JSON.parse(xhr.responseText);
        if (object === null) {
            object = {};
        }

        fillTable(object);

        if (document.getElementById('modal-window')) {
            document.getElementById('modal-window').remove();
        }
    }
}

var contactXHR = new XMLHttpRequest();
contactXHR.onreadystatechange = function() {
    if (contactXHR.readyState === XMLHttpRequest.DONE) {
        var object = JSON.parse(contactXHR.responseText);

        showContactForm(object, true);
    }
}

document.addEventListener('DOMContentLoaded', function() {
    loadContacts(1);

    document.getElementById('show-filters-pane').addEventListener('click', showHidden);
    document.getElementById('filter-btn').addEventListener('click', function() {
        loadContactsByFilters(1);
        document.getElementById('pages-pane').remove();
        isFiltered = true;
    });

    document.getElementById('delete-btn').addEventListener('click', deleteContacts);
    document.getElementById('email-btn').addEventListener('click', showEmailForm);

    document.getElementById('add-form-btn').addEventListener('click', function() {
    	showContactForm({}, false);
    });

    document.getElementById('filter-del-btn').addEventListener('click', clearFilters);

    setMaxLength(true);

    document.getElementById('f-year').addEventListener('keyup', validFilterDate);
    document.getElementById('f-month').addEventListener('keyup', validFilterDate);
    document.getElementById('f-day').addEventListener('keyup', validFilterDate);
    document.getElementById('f-house').addEventListener('keyup', validFilterHouseNumber);
    document.getElementById('f-flat').addEventListener('keyup', validFilterFlatNumber);
    document.getElementById('f-index').addEventListener('keyup', validFilterIndex);

    document.getElementById('message').addEventListener('click', function() {
        this.removeAttribute('style');
    });
});

function showHidden() {
    var collapse = this.nextElementSibling;

    if (collapse.style.maxHeight) {
        collapse.style.maxHeight = null;
        collapse.style.borderWidth = 0;
        this.innerHTML = 'Показать фильтры';
    } else {
        collapse.style.maxHeight = collapse.scrollHeight + 'px';
        collapse.style.borderWidth = '1px';
        this.innerHTML = 'Скрыть фильтры';
    }
}

function clearTable() {
    var table = document.getElementById('table-body');

    while(table.firstChild) {
        table.removeChild(table.firstChild);
    }
}

function clearFilters() {
    document.getElementById('f-name').value = '';
    document.getElementById('f-surname').value = '';
    document.getElementById('f-patronymic').value = '';

    document.getElementsByName('f-date-type')[0].checked = true;
    document.getElementById('f-year').value = '';
    document.getElementById('f-month').value = '';
    document.getElementById('f-day').value = '';

    var genders = document.getElementsByName('f-gender');
    for (var i = 0; i < genders.length; i++) {
        genders[i].checked = false;
    }

    var familyStatuses = document.getElementsByName('f-family-status');
    for (var i = 0; i < familyStatuses.length; i++) {
        familyStatuses[i].checked = false;
    }

    document.getElementById('f-country').value = '';
    document.getElementById('f-town').value = '';
    document.getElementById('f-street').value = '';
    document.getElementById('f-house').value = '';
    document.getElementById('f-flat').value = '';
    document.getElementById('f-index').value = '';

    document.getElementById('f-citizenship').value = '';

    isFiltered = false;
    document.getElementById('pages-pane').remove();

    loadContacts(1);
}

function fillTable(object) {
    var template = Mustache.render(
        '{{#contacts}}' +
        '<tr>' +
            '<td><input type="checkbox" value="{{id}}" name="select-contacts"></td>' +
            '<td><a name="edit-a">{{surname}} {{name}} {{patronymic}}</a><input type="hidden" value="{{id}}"></td>' +
            '<td>{{#day}}{{day}}.{{month}}.{{year}}{{/day}}</td>' +
            '<td name="addresses">{{#country}}{{country}}, {{/country}}{{#town}}г. {{town}}, {{/town}}{{#street}}ул. {{street}}, {{/street}}{{#house}}д. {{house}}, {{/house}}{{#flat}}кв. {{flat}}, {{/flat}}</td>' +
            '<td>{{jobPlace}}</td>' +
        '</tr>' +
        '{{/contacts}}',
        object
    );
    document.getElementById('table-body').innerHTML += template;

    pagesCount = object.pagesCount;
    if (page > pagesCount) {
        page = pagesCount;
        loadContacts(page);
        return;
    }
    if (document.getElementById('pages-pane')) {
        document.getElementById('pages-pane').remove();
    }

    showPages();

    var addresses = document.getElementsByName('addresses');
    for (var i = 0; i < addresses.length; i++) {
        var innerText = addresses[i].innerHTML;
        if (innerText.length !== 0) {
            addresses[i].innerHTML = innerText.substring(0, innerText.length - 2);
        }
    }

    var a = document.getElementsByName('edit-a');
    var selects = document.querySelectorAll('input[name=select-contacts]');

    for (var i = 0; i < a.length; i++) {
        a[i].addEventListener('click', loadContactById);
    }

    for (var i = 0; i < selects.length; i++) {
        selects[i].addEventListener('change', checkBoxes);
    }

    a = document.getElementsByName('get-page');
    for (var i = 0; i < a.length; i++) {
        a[i].addEventListener('click', function() {
            clearTable();
            document.getElementById('pages-pane').remove();

            page = this.innerHTML;
            if (page === 'В начало') {
                page = 1;
            } else if (page === 'В конец') {
                page = pagesCount;
            }
            page = parseInt(page, 10);

            if (isFiltered) {
                loadContactsByFilters(page);
            } else {
                loadContacts(page);
            }
        });
    }
}

function showPages() {
    var div = document.createElement('div');
    div.id = 'pages-pane';
    div.classList.add('flex');
    div.classList.add('flex-row');
    div.classList.add('center');

    if (page - 3 > 0) {
        var first = document.createElement('a');
        first.innerHTML = 'В начало';
        first.classList.add('mr-10px');
        first.name = 'get-page';
        div.appendChild(first);
    }

    for (var i = 1; i < pagesCount + 1; i++) {
        if (page - 2 <= i && page + 2 >= i) {
            var a = document.createElement('a');
            a.innerHTML = i;
            a.classList.add('mr-10px');
            if (i === page) {
                a.classList.add('disabled');
            } else {
                a.name = 'get-page';
            }
            div.appendChild(a);
        }
    }

    if (page + 2 < pagesCount) {
        var last = document.createElement('a');
        last.innerHTML = 'В конец';
        last.name = 'get-page';
        div.appendChild(last);
    }

    document.body.appendChild(div);
}

function checkBoxes() {
    var selects = document.querySelectorAll('input[name=select-contacts]');
    var btn = document.getElementById('delete-btn');

    for (var i = 0; i < selects.length; i++) {
        if (selects[i].checked) {
            btn.disabled = false;
            return;
        }
    }

    btn.disabled = true;
}

function loadContactById() {
    var formData = new FormData();

    id = this.nextElementSibling.value;
    formData.append('id', id);

    contactXHR.open('POST', url + '/edit', true);
    contactXHR.send(formData);
}

function loadContacts(selectedPage) {
    xhr.open('POST', url + '/contacts', true);

    var formData = new FormData();
    formData.append('page', selectedPage);

    page = selectedPage;

    xhr.send(formData);
}

function deleteContacts() {
    var selects = document.getElementsByName('select-contacts');

    var formData = new FormData();

    for (var i = 0; i < selects.length; i++) {
        if (selects[i].checked) {
            formData.append('id', selects[i].value);
        }
    }
    formData.append('page', page);

    document.getElementById('delete-btn').disabled = true;

    xhr.open('POST', url + '/delete', true);
    xhr.send(formData);
}

function loadContactsByFilters(selectedPage) {
    var name = document.getElementById('f-name').value;
    var surname = document.getElementById('f-surname').value;
    var patronymic = document.getElementById('f-patronymic').value;

    var year = document.getElementById('f-year').value;
    var month = document.getElementById('f-month').value;
    var day = document.getElementById('f-day').value;

    var gender = document.getElementsByName('f-gender');
    var familyStatus = document.getElementsByName('f-family-status');

    var citizenship = document.getElementById('f-citizenship').value;
    var country = document.getElementById('f-country').value;
    var town = document.getElementById('f-town').value;
    var street = document.getElementById('f-street').value;
    var house = document.getElementById('f-house').value;
    var flat = document.getElementById('f-flat').value;
    var index = document.getElementById('f-index').value;

    var dateType = document.getElementsByName('f-date-type');
    if (dateType[0].checked) {
        dateType = dateType[0].value;
    } else {
        dateType = dateType[1].value;
    }

    var formData = new FormData();

    for (var i = 0; i < gender.length; i++) {
        if (gender[i].checked) {
            var number = parseInt(i, 10) + 1;
            formData.append('g' + number, number);
        }
    }

    for (var i = 0; i < familyStatus.length; i++) {
        if (familyStatus[i].checked) {
            var number = parseInt(i, 10) + 1;
            formData.append('fs' + number, number);
        }
    }

    formData.append('name', name);
    formData.append('surname', surname);
    formData.append('patronymic', patronymic);

    formData.append('gender', gender);

    formData.append('year', year);
    formData.append('month', month);
    formData.append('day', day);

    formData.append('familyStatus', familyStatus);

    formData.append('country', country);
    formData.append('town', town);
    formData.append('street', street);
    formData.append('house', house);
    formData.append('flat', flat);
    formData.append('index', index);

    formData.append('citizenship', citizenship);

    formData.append('date-type', dateType);

    formData.append('page', selectedPage);

    page = selectedPage;

    xhr.open('POST', url + '/filters', true);
    xhr.send(formData);
}