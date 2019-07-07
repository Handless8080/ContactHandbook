<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Main Page</title>
    <script src="js/main-form.js"></script>
    <script src="js/contact-form.js"></script>
    <script src="js/mustache.js"></script>
    <script src="js/validation.js"></script>
    <script src="js/phone-validation.js"></script>
    <script src="js/filter-validation.js"></script>
    <script src="js/phone-form.js"></script>
    <script src="js/file-form.js"></script>
    <script src="js/email-send-form.js"></script>
    <link rel="stylesheet" type="text/css" href="css/styles.css">
    <meta charset="utf-8">
</head>
<body>
    <div class="m-30px">
        <div class="fade-window" id="message">Почта отправлена успешно!</div>
        <div class="flex center">
            <h1>Список контактов</h1>
        </div>
        <button class="collapsible" id="show-filters-pane">Показать фильтры</button>
        <div class="collapse">
            <div class="flex flex-column basis-100 grow-1">
                <div class="flex flex-row center">
                    <div class="header">Полное имя</div>
                </div>
                <div class="flex flex-row center">
                    <label for="f-name">Имя</label>
                    <input type="text" id="f-name">
                </div>
                <div class="flex flex-row center">
                    <label for="f-surname">Фамилия</label>
                    <input type="text" id="f-surname">
                </div>
                <div class="flex flex-row center mb-10px">
                    <label for="f-patronymic">Отчество</label>
                    <input type="text" id="f-patronymic">
                </div>
                <div class="flex flex-row center">
                    <div class="header">Дата рождения</div>
                </div>
                <div class="flex flex-row center">
                    <input id="dt-1" type="radio" name="f-date-type" value="0" checked>
                    <label for="dt-1" class="lbl-flex">Больше чем</label>
                    <input id="dt-2" type="radio" name="f-date-type" value="1">
                    <label for="dt-2" class="lbl-flex">Меньше чем</label>
                </div>
                <div class="flex flex-row center">
                    <label for="f-year">Год</label>
                    <input type="text" id="f-year">
                </div>
                <div class="flex flex-row center">
                    <label for="f-month">Месяц</label>
                    <input type="text" id="f-month">
                </div>
                <div class="flex flex-row center mb-10px">
                    <label for="f-day">День</label>
                    <input type="text" id="f-day">
                </div>
                <div class="flex flex-row center descr-wrong">
                   Дата рождения введена некорректно
                </div>
                <div class="flex flex-row center">
                    <div class="header">Пол</div>
                </div>
                <div class="flex flex-row center mb-10px">
                    <input id="g-1" type="checkbox" name="f-gender" value="1">
                    <label for="g-1" class="lbl-flex">Мужской</label>
                    <input id="g-2" type="checkbox" name="f-gender" value="2">
                    <label for="g-2" class="lbl-flex">Женский</label>
                </div>
                <div class="flex flex-row center">
                    <div class="header">Семейное положение</div>
                </div>
                <div class="flex flex-row center">
                	<div class="flex flex-column">
                	    <div class="flex flex-row">
                	        <input id="fs-1" type="checkbox" name="f-family-status" value="1">
                	        <label for="fs-1" class="lbl-flex">Не замужем/не женат</label>
                	    </div>
                	    <div class="flex flex-row">
                	        <input id="fs-2" type="checkbox" name="f-family-status" value="2">
                	        <label for="fs-2" class="lbl-flex">Разведен/разведена</label>
                	    </div>
                	    <div class="flex flex-row">
                	        <input id="fs-3" type="checkbox" name="f-family-status" value="3">
                	        <label for="fs-3" class="lbl-flex">Замужем/женат</label>
                	    </div>
                	    <div class="flex flex-row mb-10px">
                	        <input id="fs-4" type="checkbox" name="f-family-status" value="4">
                	        <label for="fs-4" class="lbl-flex">Состоит в гражданском браке</label>
                	    </div>
                	</div>
                </div>
                <div class="flex flex-row center">
                    <div class="header">Адрес</div>
                </div>
                <div class="flex flex-row center">
                    <label for="f-country">Страна</label>
                    <input type="text" id="f-country">
                </div>
                <div class="flex flex-row center">
                    <label for="f-town">Город</label>
                    <input type="text" id="f-town">
                </div>
                <div class="flex flex-row center">
                    <label for="f-street">Улица</label>
                    <input type="text" id="f-street">
                </div>
                <div class="flex flex-row center">
                    <label for="f-house">Дом</label>
                    <input type="text" id="f-house">
                </div>
                <div class="flex flex-row center descr-wrong">
                   Номер дома введен некорректно
                </div>
                <div class="flex flex-row center">
                    <label for="f-flat">Квартира</label>
                    <input type="text" id="f-flat">
                </div>
                <div class="flex flex-row center descr-wrong">
                   Гомер квартиры введен некорректно
                </div>
                <div class="flex flex-row center mb-10px">
                    <label for="f-index">Индекс</label>
                    <input type="text" id="f-index">
                </div>
                <div class="flex flex-row center descr-wrong">
                   Почтовый индекс введен некорректно
                </div>
                <div class="flex flex-row center">
                    <div class="header">Прочая информация</div>
                </div>
                <div class="flex flex-row center mb-10px">
                    <label for="f-citizenship">Гражданство</label>
                    <input type="text" id="f-citizenship">
                </div>
                <div class="flex end mb-10px">
                    <button type="button" id="filter-del-btn" class="mr-10px">Сбросить фильтры</button>
                    <button type="button" id="filter-btn">Найти</button>
                </div>
            </div>
		</div>
        <div class="flex end mb-10px">
            <button id="email-btn" class="mr-10px">Отправить письма</button>
            <button id="delete-btn" class="mr-10px" disabled>Удалить контакты</button>
            <button id="add-form-btn">Добавить контакт</button>
        </div>
        <table class="w-100">
            <thead>
                <tr>
                    <th class="cbox"></th>
                    <th>Полное имя</th>
                    <th>Дата рождения</th>
                    <th>Адрес</th>
                    <th>Место работы</th>
                </tr>
            </thead>
            <tbody id="table-body">
            </tbody>
        </table>
    </div>
</body>
</html>