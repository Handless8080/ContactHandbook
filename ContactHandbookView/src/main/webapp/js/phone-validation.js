var phoneFlag = false;

function validAllPhoneFields() {
    var btn = document.getElementById('add-phone-btn');

    if (phoneFlag && document.getElementById('phone').value.length !== 0) {
        btn.disabled = false;
    } else {
        btn.disabled = true;
    }
}

function validCountryOrOperatorCode() {
    if (this.value.length === 0 || this.value == parseInt(this.value, 10)) {
        this.classList.remove('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'none';

        phoneFlag = true;
    } else {
        this.classList.add('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'flex';

        phoneFlag = false;
    }

    validAllPhoneFields();
}

function validPhoneNumber() {
    if (this.value.length === 7 && this.value == parseInt(this.value, 10)) {
        this.classList.remove('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'none';

        phoneFlag = true;
    } else {
        this.classList.add('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'flex';

        phoneFlag = false;

        if (this.value.length === 0) {
            this.parentNode.nextElementSibling.innerHTML = 'Номер телефона не может быть пустым';
        } else {
            this.parentNode.nextElementSibling.innerHTML = 'Номер телефона введен некорректно';
        }
    }

    validAllPhoneFields();
}

function setMaxPhoneFieldsLength() {
    document.getElementById('country-code').maxLength = 4;
    document.getElementById('operator-code').maxLength = 2;
    document.getElementById('phone').maxLength = 7;
}