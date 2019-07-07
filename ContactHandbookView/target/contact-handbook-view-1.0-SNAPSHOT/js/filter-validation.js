var filterFlag = false;

function validAllFilters() {
    var btn = document.getElementById('filter-btn');
    var filterPane = document.getElementById('show-filters-pane').nextElementSibling;

    if (!filterFlag) {
        btn.disabled = false;
    } else {
        btn.disabled = true;
    }

    filterPane.style.maxHeight = filterPane.scrollHeight + 'px';
}

function validFilterHouseNumber() {
    var text = this.value;

    for (var i = 0; i < text.length - 1; i++) {
        if (!Number.isInteger(parseInt(text[i], 10))) {
            this.classList.add('input-wrong');
            this.parentNode.nextElementSibling.style.display = 'flex';

            flag = true;
            validAllFilters();
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

    validAllFilters();
}

function validFilterFlatNumber() {
    if (this.value.length !== 0 && parseInt(this.value, 10) != this.value) {
        this.classList.add('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'flex';
        filterFlag = true;
    } else {
        this.classList.remove('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'none';
        filterFlag = false;
    }

    validAllFilters();
}

function validFilterIndex() {
    if (this.value.length === 0 || (this.value.length === 6 && this.value == parseInt(this.value, 10))) {
        this.classList.remove('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'none';
        filterFlag = false;
    } else {
        this.classList.add('input-wrong');
        this.parentNode.nextElementSibling.style.display = 'flex';
        filterFlag = true;
    }

    validAllFilters();
}

function validFilterDate() {
    var year = document.getElementById('f-year');
    var month = document.getElementById('f-month');
    var day = document.getElementById('f-day');

    try {
        if (year.value !== '' || day.value !== '' || month.value !== '') {
            var date = new Date(year.value + '-' + month.value + '-' + day.value).toISOString();
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
        filterFlag = true;

        validAllFilters();
        return;
    }

    year.classList.remove('input-wrong');
    month.classList.remove('input-wrong');
    day.classList.remove('input-wrong');
    day.parentNode.nextElementSibling.style.display = 'none';

    filterFlag = false;
    validAllFilters();
}