var ID_VALUES = '#page-values';
var idx1;
var idx2;

function setIdx(i1,i2) {
    idx1 = i1;
    idx2 = i2;
}

function closeWindow() {
    (window.open('', '_self').opener = window).close();
}

function replyWithPreviousCorrespon() {
    if (!window.opener) {
        return false;
    }
    var action = window.opener.document.forms[0]['form:dummyReplyWithPreviousCorrespon'];
    action.click();

    closeWindow();
}

function detectAddressUser(id) {
    document.getElementById('form:detectedAddressUserId').value = id;
}

function detectRepliedId(id) {
    document.getElementById('form:detectedRepliedId').value = id;
}

function setTargetAddressType(type) {
    document.getElementById('form:targetAddressType').value = type;
}

function toggleReplyStatus(link, id, replyCount) {
    var replyCorrespons = document.getElementById(id);
    var text = link.innerText ? link.innerText : link.textContent;

    if (text.toLowerCase().indexOf('close') >= 0) {
        replyCorrespons.style.display = 'none';
        text = "返信件数: " + replyCount;
    } else {
        replyCorrespons.style.display = 'block';
        text = "閉じる";
    }
    if (link.innerText) {
        link.innerText = text;
    } else {
        link.textContent = text;
    }
}

function switchDisplay(id, action, val) {
    var area = document.getElementById(id);
    var hidden = document.getElementById(val);

    if (area.style.display == 'none') {
        area.style.display = 'block';
        action.innerHTML = '詳細を隠す';
    } else {
        area.style.display = 'none';
        action.innerHTML = '詳細を表示';
    }
    hidden.value = area.style.display;
}

function setFileId(corresponId, fileId) {
    var idElement = document.getElementById('form:corresponId');
    idElement.value = corresponId;
    var fileIdElement = document.getElementById('form:fileId');
    fileIdElement.value = fileId;
}

function setId(id) {
    var idElement = document.getElementById('form:id');
    idElement.value = id;
}

function setDisplay(name, disp) {
    var displayElement = document.getElementById('form:' + name);
    displayElement.value = disp;
}

function showDialog(id, link) {
    Dialog.show(id);

    var confirmDialog = document.getElementById(id);
    var forwardTitle = document.getElementById("forwardTitle");
    var forwardButton = document.getElementById("forwardButton");
    var copyTitle = document.getElementById("copyTitle");
    var copyButton = document.getElementById("copyButton");

    confirmDialog.style.position = 'absolute';

    if (link == "copy") {
        forwardTitle.style.display = 'none';
        forwardButton.style.display = 'none';
        copyTitle.style.display = 'block';
        copyButton.style.display = 'block';
    }

    if (link == "forward") {
        copyTitle.style.display = 'none';
        copyButton.style.display = 'none';
        forwardTitle.style.display = 'block';
        forwardButton.style.display = 'block';
    }
}

var editableReplyRequired = getJsonValues(ID_VALUES).editableReplyRequired;
function changeReplyRequired() {

    var replyRequired           = document.getElementById('form:replyRequired');
    var deadlineForReply        = document.getElementById('form:deadlineForReply');
    var deadlineForReplyCurrent = document.getElementById('form:deadlineForReplyCurrent');

    if (deadlineForReply != null){
        if (!editableReplyRequired.include(replyRequired.value)) {
            deadlineForReplyCurrent.value = deadlineForReply.value;
            deadlineForReply.value = '';
            deadlineForReply.readOnly = true;
        } else if (deadlineForReply.readOnly) {
            deadlineForReply.value = deadlineForReplyCurrent.value;
            deadlineForReply.readOnly = false;
        }
    }
}

function setupResponseHistory() {
    //tableScroller('form:corresponResponseHistory', 232, 23);
}
function processSubmit() {
    var deadlineForReply        = document.getElementById('form:deadlineForReply');
    if (deadlineForReply) {
        var deadlineForReplyCurrent = document.getElementById('form:deadlineForReplyCurrent');
        deadlineForReplyCurrent.value = deadlineForReply.value;
    }
    var result = disableSubmit();
    return result;
}

function setScrollPosition() {
    var position = document.getElementById('form:scrollPosition');
    if (window.pageYOffset) {
        position.value = window.pageYOffset;
    } else if (document.documentElement && document.documentElement.scrollTop) {
        position.value = document.documentElement.scrollTop;
    } else if (document.body.scrollTop) {
        position.value = document.body.scrollTop;
    }
}

function scrollWindowByScrollPosition() {
    var position = document.getElementById('form:scrollPosition');
    if (position.value != 0) {
        window.scrollTo(0, position.value);
        position.value = 0;
    }
}

var closing = false;
function showWorkflows(def, edit, verification) {
    var workflowDefault = document.getElementById('workflowDefault');
    var workflowEdit = document.getElementById('workflowEdit');
    var workflowVerification = document.getElementById('workflowVerification');

    workflowDefault.style.display = def;
    workflowEdit.style.display = edit;
    workflowVerification.style.display = verification;

    if (workflowEdit.style.display != 'none') {
        var position = document.getElementById('form:scrollPosition');
        window.scrollTo(0, position.value);
        Dialog.show('workflowEdit', null, function(hash) {
            if (closing) {
                return true;
            }
            closing = true;
            $('#workflowEdit .cancel').trigger('click');

            return true;
        });
    } else if (workflowVerification.style.display != 'none') {
        var position = document.getElementById('form:scrollPosition');
        window.scrollTo(0, position.value);
        Dialog.show('workflowVerification', null, function(hash) {
            if (closing) {
                return true;
            }
            closing = true;
            $('#workflowVerification .cancel').trigger('click');

            return true;
        });
    }
}
function resetScroll() {
    document.body.scrollTop = 0;
}
function editWorkflow() {
    showWorkflows('none', 'block', 'none');
}
function verifyWorkflow() {
    showWorkflows('none', 'none', 'block');
}
function cancelWorkflow() {
    Dialog.close('workflowEdit');
    Dialog.close('workflowVerification');
    closing = false;
    showWorkflows('block', 'none', 'none');
}
function cancelWorkflowWhenNoError() {
    if (document.getElementById('form:workflowEditDisplay').value == 'false') {
        cancelWorkflow();
        window.scrollTo(0, 0);
    }
}
function loadJsonValues() {
    var group = document.getElementById('form:groupJSONString');
    var mapping = document.getElementById('form:groupUserMappingsJSONString');
    var user = document.getElementById('form:userJSONString');

    PersonInChargeInput.setupDataSource(
        JSON.parse(group.value),
        JSON.parse(mapping.value),
        JSON.parse(user.value)
    );

    group.value = '';
    mapping.value = '';
    user.value = '';
    document.getElementById('form:jsonValuesLoaded').value = 'false';
}

function preProcess(e) {
    setActionName(e);
    setAjaxRequest(true);
    closing = true;
}

function postProcess(e) {
    enableAction();
}


function oncompleteEdit(data) {
    Spinner.onStatusChange(data);
    if (data.status == 'success') {
        postProcess(event);
        loadJsonValues();

        root = document.getElementById('form:toGroup:0:addressUser').getElementsByTagName('a');
        for( i = 0; i < root.length; i++) {
            act = root[i].id;
            arrayAct = act.split(':');
            if( arrayAct[0] == 'form'
               && arrayAct[1] == 'toGroup'
           && arrayAct[2] == idx1
           && arrayAct[4] == idx2
           && arrayAct[5] == 'editPic') {
               PersonInChargeInput.showDialog(
                   document.getElementById(root[i].id),
                   idx1 + '-' + idx2);
                   break;
           }
        }
    }
}

function oncompleteShowResponseHistory(data) {
    Spinner.onStatusChange(data);
    if (data.status == 'success') {
        var section = document.getElementById('responseHistory');
        if (section) {
            section.style.display = 'block';
            setupResponseHistory();
        }
    }
}

$(document).ready(function() {
    setupResponseHistory();
    var displayReplied = getJsonValues(ID_VALUES).displayReplied;
    if (displayReplied) {
        document.getElementById('header').style.display = 'none';
        document.getElementById('action-panel').style.display = 'none';
    }
    changeReplyRequired();

    if (document.getElementById('form:workflowEditDisplay').value == 'true') {
        editWorkflow();
    } else if (document.getElementById('form:verificationDisplay').value == 'true') {
        verifyWorkflow();
    } else {
        scrollWindowByScrollPosition();
    }
});

