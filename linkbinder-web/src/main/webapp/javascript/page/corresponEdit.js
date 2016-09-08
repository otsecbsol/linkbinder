var ID_VALUES = '#page-values';
var tempIdx;

/**
 * set value to custom field.
*/
function dataInput(dialogId, keyId, keyValue, id, value) {
    document.getElementById(keyValue).value = value;
    document.getElementById(keyId).value = id;
    Dialog.close(dialogId);
}

/* Attachment */
function getAttachmentCount() {
    var elem = document.getElementById('form:attachedCount');
    if (!elem) return -1;
    return Number(elem.value);
}

function getAttachedCountByDelete() {
    var elem = document.getElementById('form:attachedCountByDelete');
    if (!elem) return -1;
    return Number(elem.value);
}

function setupAttachments() {
    var maxAttachmentCount = getJsonValues(ID_VALUES).maxAttachmentCount;
    var attachedCount = getAttachmentCount();
    if (attachedCount < maxAttachmentCount) {
        if ((attachedCount + 1) == maxAttachmentCount) {
            showAttachment(0, false);
        } else {
            showAttachment(0, true);
        }
    } else {
        hideAttachment(0, true);
    }
    hideAttachment(1, true);
    hideAttachment(2, true);
    hideAttachment(3, true);
    hideAttachment(4, true);
}

function setupAttachmentsForErrorBack() {
    var attachedCount = getAttachmentCount();
    var deletedCount = getAttachedCountByDelete();
    if (0 == attachedCount || 0 == deletedCount) {
        setupAttachments();
    } else {
        var maxAttachmentCount = getJsonValues(ID_VALUES).maxAttachmentCount;
        var latestAttachedCount = attachedCount - deletedCount;
        for (var i = 0; i < maxAttachmentCount; i++) {
            if (i < latestAttachedCount) {
                hideAttachment(i, true);
            } else {
                showAttachment(i, false);
            }
        }
    }
}

function hideAttachment(index, withAction) {
    var id = 'form:attachment';
    var aid = 'form:addAttachment';
    var attachment = document.getElementById(id + (index + 1));
    if (attachment) {
        attachment.style.display = 'none';
    }
    if (!withAction) return;

    var action = document.getElementById(aid + (index + 1));
    if (action) {
        action.style.display = 'none';
    }
}

function showAttachment(index, withAction) {
    if (index < 0) return;

    var id = 'form:attachment';
    var aid = 'form:addAttachment';
    var attachment = document.getElementById(id + (index + 1));
    if (attachment) {
        attachment.style.display = 'inline';
    }
    if (!withAction) return;

    var action = document.getElementById(aid + (index + 1));
    if (action) {
        action.style.display = 'inline';
    }
}

function showAttachmentAddAction() {
    var id = 'form:attachment';
    var lastIndex = -1;
    var maxAttachmentCount = getJsonValues(ID_VALUES).maxAttachmentCount;
    for (var i = 0; i < maxAttachmentCount; i++) {
        var attachment = document.getElementById(id + (i + 1));
        if (attachment.style.display != 'none') {
            lastIndex = i;
        }
    }

    var attachedCount = getAttachmentCount() - getAttachedCountByDelete();
    var maxAddAttachment = maxAttachmentCount - attachedCount;
    for (var i = 0; i < maxAddAttachment; i++) {
        if (i == lastIndex && maxAddAttachment > (lastIndex + 1)) {
            document.getElementById('form:addAttachment' + (Number(i) + 1)).style.display = 'inline';
        } else {
            document.getElementById('form:addAttachment' + (Number(i) + 1)).style.display = 'none';
        }
    }
}

function deleteAttachment(deleteAction, index) {
    var baseId = 'attachmentName';
    var baseDeletedId = 'attachmentDeleted';
    var id = deleteAction.id.substring(0, deleteAction.id.lastIndexOf(':') + 1) + baseId;
    var attachment = document.getElementById(id);
    if (!attachment) return false;

    var deletedId = deleteAction.id.substring(0, deleteAction.id.lastIndexOf(':') + 1) + baseDeletedId;
    var deleted = document.getElementById(deletedId);
    if (!deleted) return false;

    attachment.style.display = 'none';
    deleteAction.style.display = 'none';
    deleted.value = true;
    return true;
}

function getAddAttachmentIndex() {
    var maxAttachmentCount = getJsonValues(ID_VALUES).maxAttachmentCount;
    for (var i = 0; i < maxAttachmentCount; i++) {
        var attachment = document.getElementById('form:attachment' + (i + 1));
        if (attachment.style.display == 'none') {
            return i;
        }
    }
    return -1;
}

function addAttachment(addAction, index) {
    addAction.style.display = 'none';

    showAttachment(index + 1, false);
    showAttachmentAddAction();
}

function setFileId(val, no) {
    var fileId = document.getElementById('form:fileId');
    var fileNo = document.getElementById('form:fileNo');
    fileId.value = val;
    fileNo.value = no;
}

function processSubmit() {
    var deadlineForReply        = document.getElementById('form:deadlineForReply');
    var deadlineForReplyCurrent = document.getElementById('form:deadlineForReplyCurrent');
    deadlineForReplyCurrent.value = deadlineForReply.value;

    var result = disableSubmit();
    return result;
}

function changeReplyRequired() {
    var replyRequired           = document.getElementById('form:replyRequired');
    var deadlineForReply        = document.getElementById('form:deadlineForReply');
    var deadlineForReplyCurrent = document.getElementById('form:deadlineForReplyCurrent');

    var editableReplyRequired = getJsonValues(ID_VALUES).editableReplyRequired;
    if (!editableReplyRequired.include(replyRequired.value)) {
        deadlineForReplyCurrent.value = deadlineForReply.value;
        deadlineForReply.value = '';
        deadlineForReply.readOnly = true;
    } else if (deadlineForReply.readOnly) {
        deadlineForReply.value = deadlineForReplyCurrent.value;
        deadlineForReply.readOnly = false;
    }
}

function detectedAddressType(type) {
    document.getElementById('form:detectedAddressType').value=type;
}

function detectedAddressIndex(idx) {
    document.getElementById('form:detectedAddressIndex').value=idx;
}

function detectedAddress(type, idx) {
    detectedAddressType(type);
    detectedAddressIndex(idx);
}

function showDialog(source, to, idx, add) {
    detectedAddressIndex(idx);
    detectedAddressType(to ? 1 : 2);

    var tableId    = to ? 'form:toAddressTable' : 'form:ccAddressTable';
    var key        = to ? 'AddTo'               : 'AddCc';
    var okButtonId = to ? ':editAddressToOK'    : ':editAddressCcOK';
    var selectedGroupElement;
    var selectedValuesElement;

    if (idx != -1) {
        key = key + idx;
        okButtonId = tableId + ':' + idx + okButtonId;
        selectedGroupElement = document.getElementById(tableId + ':' + idx + (to ? ':to-group' : ':cc-group'));
        selectedValuesElement = document.getElementById(tableId + ':' + idx + (to ? ':to-users' : ':cc-users'));
    } else {
        okButtonId = 'form' + okButtonId;
    }

    AddressInput.showDialog({
        action:source,
        tableId:tableId,
        key:key,
        to:to,
        okButtonId:okButtonId,
        selectedGroupId:document.getElementById('form:corresponGroupId'),
        selectedUserId:document.getElementById('form:addressUserValues'),
        selectedGroupElement:selectedGroupElement,
        selectedValuesElement:selectedValuesElement,
        add:add
    });
}

function loadJsonValues() {
    var group = document.getElementById('form:groupJSONString');
    var mapping = document.getElementById('form:groupUserMappingsJSONString');
    var user = document.getElementById('form:groupUserJSONString');
    var mode = document.getElementById('form:updateModeJSONString');

    AddressInput.setupDataSource(
        JSON.parse(group.value),
        JSON.parse(mapping.value),
        JSON.parse(user.value),
        JSON.parse(mode.value),
        getJsonValues(ID_VALUES).parentCorresponNo
    );

    group.value = '';
    mapping.value = '';
    user.value = '';
    mode.value = '';
}

function setJsonValuesLoaded(val) {
    document.getElementById('form:jsonValuesLoaded').value = val;
}

function preProcess(e) {
    setActionName(e);
    setAjaxRequest(true);
}

function postProcess() {
    enableAction();
}

function compleatePostProcess(data) {
    Spinner.onStatusChange(data);
    if (data.status == 'complete') {
        postProcess();
    }
}

function compleatePostProcessByDeleteAttachment(data){
    Spinner.onStatusChange(data);
    if (data.status == 'complete') {
        postProcess();
    } else if (data.status == 'success') {
        showAttachment(getAddAttachmentIndex(), false);
        showAttachmentAddAction();
    }
}

function clearAttachmentDeletedFlag() {
    var maxAttachmentCount = getJsonValues(ID_VALUES).maxAttachmentCount;
    for (var i = 0; i < maxAttachmentCount; i++) {
        var idName = "form:j_idt116:" + i + ":attachmentDeleted";
        var obj = document.getElementById(idName);
        if (null != obj) {
            obj.value = false;
        }
    }
    return true;
}

$(document).ready(function() {
    // Default skin
    tinyMCE.init({
        language: "ja",
        mode : "textareas",
        elements : "form:body",
        theme : "modern",
        plugins: [
            'advlist autolink lists link image charmap preview hr anchor pagebreak',
            'searchreplace visualblocks visualchars code fullscreen',
            'insertdatetime media nonbreaking save table contextmenu directionality',
            'emoticons paste textcolor colorpicker textpattern imagetools'
        ],
        toolbar1: 'insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image | preview media | forecolor backcolor emoticons',
        image_advtab: true,
        content_css : "../stylesheet/content.css",
        editor_deselector: "ignoreTinyEditor"
    });

    setupLearningTaggingElement({
        elementId: 'learningCorresponLabel',
        candidateId: 'form:candidateLearningLabels',
        selectedId: 'form:selectedLearningLabels',
        triggerId: 'form:learningCorresponLabelTrigger'
    });
    setupLearningTaggingElement({
        elementId: 'learningCorresponTag',
        candidateId: 'form:candidateLearningTags',
        selectedId: 'form:selectedLearningTags',
        triggerId: 'form:learningCorresponTagTrigger'
    });

    var toggleForLearning  = function() {
        if ($('.forLearning').prop('checked')) {
            $('#forLearningParts').show();
        } else {
            $('#forLearningParts').hide();
        }
    };

    // 初期化
    $('.forLearning').on('click', toggleForLearning);
    toggleForLearning();

    if (document.getElementById('form:initialDisplaySuccess').value == 'true') {
        setupAttachmentsForErrorBack();
        changeReplyRequired();
    } else {
        document.getElementById('viewArea').style.display = 'none';
    }
    if (document.all) {
        document.getElementById('form:attachment1').oncontextmenu = function() { return false; };
        document.getElementById('form:attachment2').oncontextmenu = function() { return false; };
        document.getElementById('form:attachment3').oncontextmenu = function() { return false; };
        document.getElementById('form:attachment4').oncontextmenu = function() { return false; };
        document.getElementById('form:attachment5').oncontextmenu = function() { return false; };
        if (null != document.getElementById('form:importFile')) {
            document.getElementById('form:importFile').oncontextmenu = function() { return false; };
        }
    }
    var maxAttachmentCount = getJsonValues(ID_VALUES).maxAttachmentCount;
    for (var i = 0; i < maxAttachmentCount; i++) {
        document.getElementById("form:attachmentKey" + (Number(i) + 1)).value = '';
        document.getElementById("form:attachmentName" + (Number(i) + 1)).value = '';
        document.getElementById("form:attachmentSize" + (Number(i) + 1)).value = '';
    }
    clearAttachmentDeletedFlag();
    if (null != document.getElementById("form:importFileKey")) {
        document.getElementById("form:importFileKey").value = '';
        document.getElementById("form:importFileName").value = '';
        document.getElementById("form:importFileSize").value = '';
    }

});

function setupLearningTaggingElement(option) {

    var id = '#' + option.elementId;
    var candidateId = '#' + $.escape(option.candidateId);
    var selectedId = '#' + $.escape(option.selectedId);
    var triggerId = '#' + $.escape(option.triggerId);

    $(id).select2({
        tags: true,
        data: JSON.parse($(candidateId).val())
    });

    var counter = -1;
    $(id).on('select2:select', function(e) {
        var data = e.params.data;
        if (data.id === data.text) {
            data.id = counter--;
        }

        var hidden = $(selectedId);
        var selectedValues = JSON.parse(hidden.val());
        selectedValues.push(data);

        hidden.val(JSON.stringify(selectedValues));
        $(triggerId).click();
    });

    $(id).on('select2:unselect', function(e) {
        var data = e.params.data;

        var hidden = $(selectedId);
        var selectedValues = JSON.parse(hidden.val());
        for (var i = 0; i < selectedValues.length; i++) {
            if (selectedValues[i].text === data.text) {
                selectedValues.splice(i, 1);
                break;
            }
        }

        hidden.val(JSON.stringify(selectedValues));
        $(triggerId).click();
    });
}

function oncompleteAdd(data) {
    Spinner.onStatusChange(data);
    if (data.status == 'complete') {
        postProcess();
    } else if (data.status == 'success') {
        loadJsonValues();
        showDialog(document.getElementById('form:addToLink'), true, -1, true);
    }
}

function oncompleteAdd2(data) {
    Spinner.onStatusChange(data);
    if (data.status == 'complete') {
        postProcess();
    } else if (data.status == 'success') {
        loadJsonValues();
        showDialog(document.getElementById('form:addCcLink'), false, -1, true);
    }
}

function setIdx(idx) {
    tempIdx = idx
}

function oncompleteEdit(data) {
    Spinner.onStatusChange(data);
    if (data.status == 'complete') {
        postProcess();
    } else if (data.status == 'success') {
        loadJsonValues();
        showDialog(document.getElementById('form:toAddressTable:' + tempIdx + ':editToLink'), true, tempIdx, false);
    }
}

function oncompleteEdit2(data) {
    Spinner.onStatusChange(data);
    if (data.status == 'complete') {
        postProcess();
    } else if (data.status == 'success') {
        loadJsonValues();
        showDialog(document.getElementById('form:ccAddressTable:' + tempIdx + ':editCcLink'), false, tempIdx, false);
    }
}

function oncompleteEditAddressToOK(data) {
    Spinner.onStatusChange(data);
    if (data.status == 'complete') {
        postProcess();
        Dialog.close('editAddressform:toAddressTableAddTo');
    }
}

function oncompleteEditAddressToOK2(data) {
    Spinner.onStatusChange(data);
    if (data.status == 'complete') {
        postProcess();
        Dialog.close('editAddressform:toAddressTableAddTo' + tempIdx);
    }
}

function oncompleteEditAddressCcOK(data) {
    Spinner.onStatusChange(data);
    if (data.status == 'complete') {
        postProcess();
        Dialog.close('editAddressform:ccAddressTableAddCc');
    }
}

function oncompleteEditAddressCcOK2(data) {
    Spinner.onStatusChange(data);
    if (data.status == 'complete') {
        postProcess();
        Dialog.close('editAddressform:ccAddressTableAddCc' + tempIdx);
    }
}

function isApplyTemplate() {
    // グループが1件でも選択されている場合は確認メッセージを表示する
    var toGroup1 = document.getElementById('form:toAddressTable:0:to-group');
    var ccGroup1 = document.getElementById('form:ccAddressTable:0:cc-group');
    if (null != toGroup1 || null != ccGroup1) {
        var message = 'テンプレートを適用すると設定済みのTo/Ccが上書きされます。\n'
        + 'テンプレートを適用しますか？';
        return confirm(message);
    } else {
        return true;
    }
}
