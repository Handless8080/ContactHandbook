var files;
var filesBody;
var fileId;

var downloadXHR = new XMLHttpRequest();
downloadXHR.responseType = 'blob';
downloadXHR.onreadystatechange = function() {
    if (downloadXHR.readyState === XMLHttpRequest.DONE) {
        var a = document.createElement('a');
        a.style.display = 'none';
        document.body.appendChild(a);

        var url = window.URL.createObjectURL(downloadXHR.response);
        a.href = url;
        var headers = downloadXHR.getAllResponseHeaders();
        var filename = decodeURI(downloadXHR.getResponseHeader('Content-disposition').slice(21));
        filename = filename.replace(/\+/g, ' ');
        a.download = filename;
        a.click();
        window.URL.revokeObjectURL(url);
    }
};

function showFileForm(object, isEdit) {
    var template = Mustache.render(
        '<div class="modal" id="modal-attachment-window">' +
        	'<div class="modal-content">' +
        		'<div class="close flex flex-row end" id="close-attachment-form">&times;</div>' +
                '<div class="flex flex-row center">' +
                    '<label for="file" class="btn">Выбрать файл</label>' +
                    '<input type="file" id="file">' +
                '</div>' +
                '<div class="flex flex-row center" id="filename">' +
                    '{{fileName}}' +
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
                    '<button type="button" id="add-file-btn">Добавить файл</button>' +
                '</div>' +
        	'</div>' +
        '</div>',
        object
    );
    document.getElementById('modal-window').insertAdjacentHTML('beforeend', template);

    if (isEdit) {
        document.getElementById('add-file-btn').innerHTML = 'Сохранить изменения';
        document.getElementById('add-file-btn').disabled = false;
        document.getElementById('add-file-btn').addEventListener('click', editAttachment);
    } else {
        document.getElementById('add-file-btn').addEventListener('click', addAttachment);
        document.getElementById('add-file-btn').disabled = true;
    }

    document.getElementById('file').addEventListener('change', function() {
        if (this.files[0]) {
            document.getElementById('filename').innerHTML = this.files[0].name;
            document.getElementById('add-file-btn').disabled = false;
        }
    });

    document.getElementById('comment').rows = 10;

    document.getElementById('close-attachment-form').addEventListener('click', function() {
        var modalWindow = document.getElementById('modal-attachment-window');
        modalWindow.remove();
    });
}

function addAttachment() {
    var f = document.getElementById('file').files[0];
    var fName = document.getElementById('file').value.split('\\');
    fName = fName[fName.length - 1];
    var comm = document.getElementById('comment').value;

    var file = {};
    var date = new Date();

    filesBody.push(f);

    file.fileName = fName;
    file.year = date.getFullYear();
    file.month = parseInt(date.getMonth(), 10) + 1;
    file.day = date.getDate();
    file.comment = comm;
    file.enabled = true;

    if (files.length === 0) {
        file.id = 1;
    } else {
        file.id = parseInt(files[files.length - 1].id, 10) + 1;
    }

    files.push(file);

    var table = document.getElementById('attachments');

    var template = Mustache.render(
        '<tr>' +
            '<td><input type="checkbox" value="{{id}}" name="select-files"></td>' +
            '<td><a id="next-a">{{fileName}}</a><input type="hidden" value="{{id}}"></td>' +
            '<td>{{day}}.{{month}}.{{year}}</td>' +
            '<td>{{comment}}</td>' +
            '<td></td>' +
        '</tr>',
        file
    );

    table.insertAdjacentHTML('beforeend', template);

    var boxes = document.querySelectorAll('input[name="select-files"]');
    for (var i = 0; i < boxes.length; i++) {
        boxes[i].addEventListener('change', checkSelectedFiles);
    }

    document.getElementById('next-a').addEventListener('click', function() {
        for (var i = 0; i < files.length; i++) {
            if (files[i].id == this.nextElementSibling.value) {
                fileId = files[i].id;
                showFileForm(files[i], true);
                return;
            }
        }
    });
    document.getElementById('next-a').id = null;

    document.getElementById('modal-attachment-window').remove();
}

function editAttachment() {
    var f = document.getElementById('file').files[0];
    var fName = document.getElementById('filename').innerHTML;
    var comm = document.getElementById('comment').value;

    var date = new Date();

    for (var i = 0; i < files.length; i++) {
        if (files[i].id == fileId) {
            filesBody[i] = f;

            files[i].fileName = fName;
            files[i].year = date.getFullYear();
            files[i].month = parseInt(date.getMonth(), 10) + 1;
            files[i].day = date.getDate();
            files[i].comment = comm;
            files[i].enabled = true;

            break;
        }
    }

    var row = document.getElementById('attachments').firstChild;

    while (row !== null) {
        if (row.firstChild.firstChild.value == fileId) {
            var element = row.firstChild.nextElementSibling;

            element.firstChild.innerHTML = fName;

            element = element.nextElementSibling;

            element = element.nextElementSibling;
            element.innerHTML = comm;
        }
        row = row.nextElementSibling;
    }

    document.getElementById('modal-attachment-window').remove();
}

function deleteFiles() {
    var boxes = document.getElementsByName('select-files');

    for (var i = 0; i < boxes.length; i++) {
        if (boxes[i].checked) {
            for (var j = 0; j < files.length; j++) {
                if (files[j].id == boxes[i].value) {
                    if (files[j].enabled === null) {
                        files[j].enabled = false;
                    } else {
                        files.splice(j, 1);
                        filesBody.splice(j, 1);
                    }

                    boxes[i].parentNode.parentNode.remove();
                    i--;

                    break;
                }
            }
        }
    }
}

function checkSelectedFiles() {
    var boxes = document.querySelectorAll('input[name="select-files"]');
    var btn = document.getElementById('delete-file-btn');

    for (var i = 0; i < boxes.length; i++) {
        if (boxes[i].checked) {
            btn.disabled = false;
            return;
        }
    }

    btn.disabled = true;
}