var id;

function addContact() {
    document.getElementById('add-btn').disabled = true;

    var formData = new FormData();
    formData = fillFormData();

    xhr.open('POST', url + '/add', true);
    xhr.send(formData);

    if (isFiltered) {
        loadContactsByFilters(pagesCount);
    }
}

function editContact() {
    document.getElementById('add-btn').disabled = true;

    var formData = new FormData();
    formData = fillFormData();
    formData.append('id', id);

    xhr.open('POST', url + '/save', true);
    xhr.send(formData);

    if (isFiltered) {
        loadContactsByFilters(page);
    }
}

function formatPhones(phones) {
    for (var i = 0; i < phones.length; i++) {
        if (phones[i].phoneType === 'Мобильный') {
            phones[i].phoneType = 1;
        } else {
            phones[i].phoneType = 2;
        }
    }
}

function fillFormData() {
    var name = document.getElementById('name').value;
    var surname = document.getElementById('surname').value;
    var patronymic = document.getElementById('patronymic').value;

    var year = document.getElementById('year').value;
    var month = document.getElementById('month').value;
    var day = document.getElementById('day').value;

    var gender = document.getElementsByName('gender');

    var familyStatus = document.getElementsByName('family-status');

    var citizenship = document.getElementById('citizenship').value;
    var country = document.getElementById('country').value;
    var town = document.getElementById('town').value;
    var street = document.getElementById('street').value;
    var house = document.getElementById('house').value;
    var flat = document.getElementById('flat').value;
    var index = document.getElementById('index').value;

    var site = document.getElementById('site').value;
    var email = document.getElementById('email').value;
    var jobPlace = document.getElementById('job').value;

    var avatar = document.getElementById('avatar').files[0];

    if (gender[0].checked) {
        gender = gender[0].value;
    } else {
        gender = gender[1].value;
    }

    for (var i = 0; i < familyStatus.length; i++) {
        if (familyStatus[i].checked) {
            familyStatus = familyStatus[i].value;
            break;
        }
    }

    var formData = new FormData();
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

    formData.append('site', site);
    formData.append('email', email);
    formData.append('job', jobPlace);
    formData.append('citizenship', citizenship);

    formData.append('avatar', avatar);

    formData.append('page', page);

    for (var i = 0; i < filesBody.length; i++) {
        formData.append('file' + i, filesBody[i]);
    }

    formData.append('files', JSON.stringify(files));

    formatPhones(phones);
    formData.append('phones', JSON.stringify(phones));

    return formData;
}

function showContactForm(object, isEdit) {
	var template = Mustache.render(
		'<div class="modal" id="modal-window">' +
			'<div class="modal-content">' +
				'<div class="close flex flex-row end" id="close-form">&times;</div>' +
				'<div class="flex flex-row end">' +
                    '<img width="385" src="data:image;base64,{{avatarFile.bytes}}" {{#avatarFile}}class="d-block"{{/avatarFile}} id="avatar-img" />' +
                '</div>' +
                '<div class="flex flex-row end">' +
                    '<label for="avatar" class="btn">Выбрать изображение</label>' +
                    '<input type="file" id="avatar" accept="image/*">' +
                '</div>' +
                '<div class="flex flex-row end">' +
                    '<div class="flex flex-column" id="selected-avatar-fn">' +
                    '</div>' +
                '</div>' +
				'<div class="flex flex-row center">' +
				    '<div class="header">Полное имя</div>' +
				'</div>' +
				'<div class="flex flex-row center">' +
				    '<label for="name">Имя *</label>' +
				    '<input type="text" id="name" value="{{name}}">' +
				'</div>' +
				'<div class="flex flex-row center descr-wrong">' +
                    'Имя не может быть пустым' +
                '</div>' +
				'<div class="flex flex-row center">' +
				    '<label for="surname">Фамилия *</label>' +
				    '<input type="text" id="surname" value="{{surname}}">' +
				'</div>' +
				'<div class="flex flex-row center descr-wrong">' +
                    'Фамилия не может быть пустой' +
                '</div>' +
				'<div class="flex flex-row center mb-10px">' +
				    '<label for="patronymic">Отчество</label>' +
				    '<input type="text" id="patronymic" value="{{patronymic}}">' +
				'</div>' +
                '<div class="flex flex-row center">' +
                    '<div class="header">Дата рождения</div>' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<label for="year">Год</label>' +
                    '<input type="text" id="year" value="{{year}}">' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<label for="month">Месяц</label>' +
                    '<input type="text" id="month" value="{{month}}">' +
                '</div>' +
                '<div class="flex flex-row center mb-10px">' +
                    '<label for="day">День</label>' +
                    '<input type="text" id="day" value="{{day}}">' +
                '</div>' +
                '<div class="flex flex-row center descr-wrong">' +
                    'Дата рождения введена некорректно' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<div class="header">Пол</div>' +
                '</div>' +
                '<div class="flex flex-row center mb-10px">' +
                    '<input id="gc-1" type="radio" name="gender" value="1" checked>' +
                    '<label for="gc-1" class="lbl-flex">Мужской</label>' +
                    '<input id="gc-2" type="radio" name="gender" value="2">' +
                    '<label for="gc-2" class="lbl-flex">Женский</label>' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<div class="header">Семейное положение</div>' +
                '</div>' +
                '<div class="flex flex-row center">' +
                	'<div class="flex flex-column">' +
                	    '<div class="flex flex-row">' +
                	        '<input id="fsc-1" type="radio" name="family-status" value="1" checked>' +
                	        '<label for="fsc-1" class="lbl-flex">Не замужем/не женат</label>' +
                	    '</div>' +
                	    '<div class="flex flex-row">' +
                	        '<input id="fsc-2" type="radio" name="family-status" value="2">' +
                	        '<label for="fsc-2" class="lbl-flex">Разведен/разведена</label>' +
                	    '</div>' +
                	    '<div class="flex flex-row">' +
                	        '<input id="fsc-3" type="radio" name="family-status" value="3">' +
                	        '<label for="fsc-3" class="lbl-flex">Замужем/женат</label>' +
                	    '</div>' +
                	    '<div class="flex flex-row mb-10px">' +
                	        '<input id="fsc-4" type="radio" name="family-status" value="4">' +
                	        '<label for="fsc-4" class="lbl-flex">Состоит в гражданском браке</label>' +
                	    '</div>' +
                	'</div>' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<div class="header">Адрес</div>' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<label for="country">Страна</label>' +
                    '<input type="text" id="country"  value="{{country}}">' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<label for="town">Город</label>' +
                    '<input type="text" id="town" value="{{town}}">' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<label for="street">Улица</label>' +
                    '<input type="text" id="street" value="{{street}}">' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<label for="house">Дом</label>' +
                    '<input type="text" id="house" value="{{house}}">' +
                '</div>' +
                '<div class="flex flex-row center descr-wrong">' +
                    'Номер дома введен некорректно' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<label for="flat">Квартира</label>' +
                    '<input type="text" id="flat" value="{{flat}}">' +
                '</div>' +
                '<div class="flex flex-row center descr-wrong">' +
                    'Номер квартиры введен некорректно' +
                '</div>' +
                '<div class="flex flex-row center mb-10px">' +
                    '<label for="index">Индекс</label>' +
                    '<input type="text" id="index" value="{{index}}">' +
                '</div>' +
                '<div class="flex flex-row center descr-wrong">' +
                    'Почтовый индекс введен некорректно' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<div class="header">Прочая информация</div>' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<label for="site">Веб-сайт</label>' +
                    '<input type="text" id="site" value="{{site}}">' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<label for="email">E-Mail</label>' +
                    '<input type="text" id="email" value="{{email}}">' +
                '</div>' +
                '<div class="flex flex-row center descr-wrong">' +
                    'E-Mail введен некорректно' +
                '</div>' +
                '<div class="flex flex-row center">' +
                    '<label for="job">Место работы</label>' +
                    '<input type="text" id="job" value="{{jobPlace}}">' +
                '</div>' +
                '<div class="flex flex-row center mb-10px">' +
                    '<label for="citizenship">Гражданство</label>' +
                    '<input type="text" id="citizenship" value="{{citizenship}}">' +
                '</div>' +
                '<div class="flex flex-row center mb-10px">' +
                    '<div class="header">Список телефонов</div>' +
                '</div>' +
                '<div class="flex flex-row end mb-10px">' +
                    '<button type="button" id="delete-phone-btn" class="mr-10px" disabled>Удалить телефоны</button>' +
                    '<button type="button" id="show-phone-form-btn">Добавить телефон</button>' +
                '</div>' +
                '<table class="w-100">' +
                    '<thead>' +
                        '<tr>' +
                            '<th class="cbox"></th>' +
                            '<th>Номер телефона</th>' +
                            '<th>Тип</th>' +
                            '<th>Комментарий</th>' +
                        '</tr>' +
                    '</thead>' +
                    '<tbody id="phones">' +
                        '{{#phones}}' +
                        '<tr>' +
                            '<td><input type="checkbox" value="{{id}}" name="select-phones"></td>' +
                            '<td><a name="phone-a">{{#countryCode}}+{{countryCode}}{{/countryCode}} {{#operatorCode}}({{operatorCode}}){{/operatorCode}} {{phoneNumber}}</a><input type="hidden" value="{{id}}"></td>' +
                            '<td>{{phoneType}}</td>' +
                            '<td>{{comment}}</td>' +
                        '</tr>' +
                        '{{/phones}}' +
                    '</tbody>' +
                '</table>' +
                '<div class="flex flex-row end mb-10px">' +
                    '<button type="button" id="delete-file-btn" class="mr-10px" disabled>Удалить файлы</button>' +
                    '<button type="button" id="show-file-form-btn">Добавить файл</button>' +
                '</div>' +
                '<table class="w-100">' +
                    '<thead>' +
                        '<tr>' +
                            '<th class="cbox"></th>' +
                            '<th>Имя файла</th>' +
                            '<th>Дата загрузки</th>' +
                            '<th>Комментарий</th>' +
                            '<th></th>' +
                        '</tr>' +
                    '</thead>' +
                    '<tbody id="attachments">' +
                        '{{#attachmentFiles}}' +
                        '<tr>' +
                            '<td><input type="checkbox" value="{{id}}" name="select-files"></td>' +
                            '<td><a name="file-a">{{fileName}}</a><input type="hidden" value="{{id}}"></td>' +
                            '<td>{{day}}.{{month}}.{{year}}</td>' +
                            '<td>{{comment}}</td>' +
                            '<td><a name="file-download">Скачать</a><input type="hidden" value="{{id}}"></td>' +
                        '</tr>' +
                        '{{/attachmentFiles}}' +
                    '</tbody>' +
                '</table>' +
                '<div class="flex flex-row end">' +
                    '<button type="button" id="add-btn" disabled>Добавить</button>' +
                '</div>' +
			'</div>' +
		'</div>',
		object
	);
	phones = object.phones ? object.phones : [];
	files = object.attachmentFiles ? object.attachmentFiles : [];
	filesBody = [];
	for (var i = 0; i < files.length; i++) {
	    filesBody.push(null);
	}

	document.body.insertAdjacentHTML('beforeend', template);
	document.getElementById('close-form').addEventListener('click', function() {
		var modalWindow = document.getElementById('modal-window');
		modalWindow.remove();
	});

	document.getElementById('avatar').addEventListener('change', function() {
	    if (this.files[0]) {
	        document.getElementById('selected-avatar-fn').innerHTML = this.files[0].name;

	        var img = document.getElementById('avatar-img');
	        img.classList.add('d-block');

	        var reader = new FileReader();
	        reader.onloadend = function() {
	            img.src = reader.result;
	        }
            reader.readAsDataURL(this.files[0]);
	    }
	});

	document.getElementById('show-phone-form-btn').addEventListener('click', function() {
	    showPhoneForm({}, false);
	});

	document.getElementById('delete-phone-btn').addEventListener('click', deletePhones);

	document.getElementById('show-file-form-btn').addEventListener('click', function() {
        showFileForm({}, false);
    });

    document.getElementById('delete-file-btn').addEventListener('click', function() {
        deleteFiles();
        this.disabled = true;
    });

	var boxes = document.querySelectorAll('input[name="select-phones"]');
	for (var i = 0; i < boxes.length; i++) {
	    boxes[i].addEventListener('change', checkSelectedPhones);
	}

	boxes = document.querySelectorAll('input[name="select-files"]');
	for (var i = 0; i < boxes.length; i++) {
	    boxes[i].addEventListener('change', checkSelectedFiles);
	}

	var btn = document.getElementById('add-btn');
	if (isEdit) {
	    btn.addEventListener('click', editContact);
	    btn.innerHTML = 'Сохранить';
	    btn.disabled = false;
	} else {
	    btn.addEventListener('click', addContact);
	}

	var gender = document.getElementsByName('gender');
	if (object.gender === 'Женский') {
	    gender[0].checked = false;
	    gender[1].checked = true;
	}

	var familyStatus = document.getElementsByName('family-status');
	switch (object.familyStatus) {
	    case 'Разведен/разведена':
	        familyStatus[1].checked = true;
            break;
	    case 'Замужем/женат':
	        familyStatus[2].checked = true;
            break;
	    case 'Состоит в гражданском браке':
	        familyStatus[3].checked = true;
            break;
        default:
            familyStatus[0].checked = true;
	}

	var a = document.getElementsByName('phone-a');
	for (var i = 0; i < a.length; i++) {
	    a[i].addEventListener('click', function() {
	        for (var i = 0; i < phones.length; i++) {
	            if (phones[i].id == this.nextElementSibling.value) {
                    phoneId = phones[i].id;
                    showPhoneForm(phones[i], true);

	                return;
	            }
	        }
	    });
	}

	a = document.getElementsByName('file-a');
    for (var i = 0; i < a.length; i++) {
        a[i].addEventListener('click', function() {
            for (var i = 0; i < files.length; i++) {
                if (files[i].id == this.nextElementSibling.value) {
                    fileId = files[i].id;
                    showFileForm(files[i], true);

                    return;
                }
            }
        });
    }

    a = document.getElementsByName('file-download');
    for (var i = 0; i < a.length; i++) {
        a[i].addEventListener('click', function() {
            downloadXHR.open('POST', url + '/download', true);
            var formData = new FormData();
            formData.append('file-id', this.nextElementSibling.value);
            downloadXHR.send(formData);
        });
    }

    setMaxLength(false);
    document.getElementById('site').maxLength = 50;
    document.getElementById('email').maxLength = 50;
    document.getElementById('job').maxLength = 50;

	validEventListeners();
}

function validEventListeners() {
    document.getElementById('name').addEventListener('keyup', validEmptyString);
    document.getElementById('surname').addEventListener('keyup', validEmptyString);
    document.getElementById('email').addEventListener('keyup', validEmail);
    document.getElementById('house').addEventListener('keyup', validHouseNumber);
    document.getElementById('flat').addEventListener('keyup', validFlatNumber);
    document.getElementById('index').addEventListener('keyup', validIndex);

    document.getElementById('year').addEventListener('keyup', validDate);
    document.getElementById('month').addEventListener('keyup', validDate);
    document.getElementById('day').addEventListener('keyup', validDate);
}