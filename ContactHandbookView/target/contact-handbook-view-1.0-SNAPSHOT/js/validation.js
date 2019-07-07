const emailRegexp = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,6}))$/;

var flag = false;

function validAll() {
    var btn = document.getElementById('add-btn');
    var name = document.getElementById('name');
    var surname = document.getElementById('surname');
    var email = document.getElementById('email');

    if (name.value !== '' && surname.value !== '' && (email.value === '' || (email.value !== '' && emailRegexp.test(email.value))) && !flag) {
        btn.disabled = false;
    } else {
        btn.disabled = true;
    }
}

function validEmptyString() {
    if (this.value === '') {
        this.classList.add('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'flex';
    } else {
        this.classList.remove('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'none';
    }

    validAll();
}

function validEmail() {
    if (this.value === '' || (this.value !== '' && emailRegexp.test(this.value))) {
        this.classList.remove('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'none';
    } else {
        this.classList.add('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'flex';
    }

    validAll();
}

function validHouseNumber() {
    var text = this.value;

    for (var i = 0; i < text.length - 1; i++) {
        if (!Number.isInteger(parseInt(text[i], 10))) {
            this.classList.add('input-wrong');
            this.parentNode.nextElementSibling.style.display = 'flex';

            flag = true;
            validAll();
            return;
        }
    }

    var lastChar = text[text.length - 1];
    if (text.length === 1 && parseInt(text, 10) == text) {
        this.classList.remove('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'none';
        flag = false;
    } else {
        if (parseInt(lastChar, 10) == lastChar || (text.length > 1 && lastChar.toUpperCase() !== lastChar.toLowerCase()) || text.length === 0) {
            this.classList.remove('input-wrong');
            this.parentNode.nextElementSibling.style.display = 'none';
            flag = false;
        } else {
            this.classList.add('input-wrong');
            this.parentNode.nextElementSibling.style.display = 'flex';
            flag = true;
        }
    }

    validAll();
}

function validFlatNumber() {
    if (this.value.length !== 0 && parseInt(this.value, 10) != this.value) {
        this.classList.add('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'flex';
        flag = true;
    } else {
        this.classList.remove('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'none';
        flag = false;
    }

    validAll();
}

function validIndex() {
    if (this.value.length === 0 || (this.value.length === 6 && this.value == parseInt(this.value, 10))) {
        this.classList.remove('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'none';
        flag = false;
    } else {
        this.classList.add('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'flex';
        flag = true;
    }

    validAll();
}

function validDate() {
    var year = document.getElementById('year');
    var month = document.getElementById('month');
    var day = document.getElementById('day');

    try {
        if (year.value !== '' || day.value !== '' || month.value !== '') {
            var date = new Date(year.value + '-' + month.value + '-' + day.value).toISOString();

            var currentDate = new Date().toISOString();
            if (currentDate < date) {
                throw 'Error';
            }
        }

        if (day.value !== '' && (year.value === '' || month.value === '')) {
            throw 'Error';
        }

        if (month.value !== '' && (year.value === '' || day.value === '')) {
            throw 'Error';
        }

        if (year.value !== '' && year.value.length < 4) {
            throw 'Error';
        }
    } catch (e) {
        year.classList.add('input-wrong');
        month.classList.add('input-wrong');
        day.classList.add('input-wrong');
        day.parentNode.nextElementSibling.style.display = 'flex';
        flag = true;

        validAll();
        return;
    }

    year.classList.remove('input-wrong');
    month.classList.remove('input-wrong');
    day.classList.remove('input-wrong');
    day.parentNode.nextElementSibling.style.display = 'none';
    flag = false;

    validAll();
}

function setMaxLength(prefix) {
    prefix = prefix ? 'f-' : '';

    document.getElementById(prefix + 'name').maxLength = 20;
    document.getElementById(prefix + 'surname').maxLength = 20;
    document.getElementById(prefix + 'patronymic').maxLength = 20;

    document.getElementById(prefix + 'year').maxLength = 4;
    document.getElementById(prefix + 'month').maxLength = 2;
    document.getElementById(prefix + 'day').maxLength = 2;

    document.getElementById(prefix + 'country').maxLength = 30;
    document.getElementById(prefix + 'town').maxLength = 30;
    document.getElementById(prefix + 'street').maxLength = 30;
    document.getElementById(prefix + 'house').maxLength = 5;
    document.getElementById(prefix + 'flat').maxLength = 4;
    document.getElementById(prefix + 'index').maxLength = 6;

    document.getElementById(prefix + 'citizenship').maxLength = 30;
}