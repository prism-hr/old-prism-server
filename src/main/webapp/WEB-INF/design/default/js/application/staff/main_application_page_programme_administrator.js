$(document).ready(function() {
    
    addToolTips();
    
    showQualificationAndReferenceSection();
    
    showFirstRefereeEntry();
    
    showFirstQualificationEntry();
    
    // --------------------------------------------------------------------------------
    // SHOW SELECTED QUALIFICATION
    // --------------------------------------------------------------------------------
    $('a[name="showQualificationLink"]').on("click", function() {
        $('a[name="showQualificationLink"]').each(function() {
            $("#" + $(this).attr("toggles")).hide();
        });
        $("#" + $(this).attr("toggles")).show();
    });
    
    // --------------------------------------------------------------------------------
    // SHOW SELECTED REFEREE
    // --------------------------------------------------------------------------------
    $('a[name="showRefereeLink"]').live("click", function() {
        $('a[name="showRefereeLink"]').each(function() {
            $("#" + $(this).attr("toggles")).hide();
        });
        $("#" + $(this).attr("toggles")).show();
        $('#editedRefereeId').val($(this).attr("toggles").replace("referee_", "")); // set the id of the referee we are looking at
        clearRefereeFormErrors();
        clearRefereeForm();
    });
    
    // --------------------------------------------------------------------------------
    // ONLY ALLOW A MAXIMUM OF 2 REFEREES TO BE SELECTED AT THE SAME TIME
    // --------------------------------------------------------------------------------
    $('input[name="refereeSendToUcl"]:checkbox').live("change", function() {
        var maxAllowed = 2;
        var checked = $('input[name="refereeSendToUcl"]:checked').size();
        if (checked > maxAllowed) {
            $(this).attr("checked", false);
        }
    });
    
    // --------------------------------------------------------------------------------
    // ONLY ALLOW A MAXIMUM OF 2 QUALIFICATIONS TO BE SELECTED AT THE SAME TIME
    // --------------------------------------------------------------------------------
    $('input[name="qualificationSendToUcl"]:checkbox').on("change", function() {
        var maxAllowed = 2;
        var checked = $('input[name="qualificationSendToUcl"]:checked').size();
        if (checked > maxAllowed) {
            $(this).attr("checked", false);
        }
    });

    // --------------------------------------------------------------------------------
    // POST REFEREE DATA
    // --------------------------------------------------------------------------------
    $('#refereeSaveButton').live("click", function() {
        postRefereesData();
    });
    
    // --------------------------------------------------------------------------------
    // POST QUALIFICATION DATA
    // --------------------------------------------------------------------------------
    $('#qualificationSaveButton').on("click", function() {
        postQualificationsData();
    });
    
    $("input:file").each(function() {
    	refereeId = $(this).attr('id').replace("commentDocument_", "");
    	watchUploadComment($(this), $("#commentUploadedDocument_" + refereeId));
    });
});

function clearRefereeFormErrors() {
    $("#referencesSection").find('span.invalid').remove(); // remove all previous form errors
}

function clearRefereeForm() {
    $("input:text").each(function() {
        $(this).val("");
    });
    
    $("textarea").each(function() {
        $(this).val("");
    });
    
    $("input:radio").each(function() {
        $(this).attr('checked', false);
    });
}

function showFirstRefereeEntry() {
    $('a[name="showRefereeLink"]').each(function() {
        $("#" + $(this).attr("toggles")).show();
        $('#editedRefereeId').val($(this).attr("toggles").replace("referee_", ""));
        return false;
    });
}

function showFirstQualificationEntry() {
    $('a[name="showQualificationLink"]').each(function() {
        $("#" + $(this).attr("toggles")).show();
        return false;
    });
}

function showQualificationAndReferenceSection() {
    $('section.folding h2').each(function() {
        if ($(this).attr('id') === "qualifications-H2" || $(this).attr('id') === "referee-H2") {
            $(this).addClass('open');
            $(this).next('div').show();
        } else {
            $(this).removeClass('open');
            $(this).next('div').hide();
        }
    });
}

function postRefereesData() {
    var refereeId = $('#editedRefereeId').val();
    
    var sendToUclData = {
            referees : new Array()
    };
    
    $('input[name="refereeSendToUcl"]:checkbox').each(function() {
        var checked = $(this).attr("checked");
        if (checked) {
            sendToUclData.referees.push($(this).val());
        }
    });
    
    var suitableUCL = "";
    if ($('input:radio[name=suitableUCL_' + refereeId + ']:checked').length > 0) {
        suitableUCL = $('input:radio[name=suitableUCL_' + refereeId + ']:checked').val();
    }

    var suitableForProgramme = "";
    if ($('input:radio[name=suitableProgrammeRadio_' + refereeId + ']:checked').length > 0) {
        suitableForProgramme = $('input:radio[name=suitableProgrammeRadio_' + refereeId + ']:checked').val();
    }
    
    var documents = new Array();
    $("#commentUploadedDocument_" + refereeId).find('input.file').each(function(){
    	documents.push($(this).val());
    });
    documents = documents.join(",");
    
//    var $ref_doc_upload_field = $('input:file[id=referenceDocument_' + refereeId + ']');
//    var $ref_doc_container  = $ref_doc_upload_field.parent('div.field');
//    var $ref_doc_hidden     = $ref_doc_container.find('span input');
    
    $('#referencesSection > div').append('<div class="ajax" />');
    $.ajax({
        type : 'POST',
        statusCode : {
            401 : function() { window.location.reload(); },
            500 : function() { window.location.href = "/pgadmissions/error"; },
            404 : function() { window.location.href = "/pgadmissions/404"; },
            400 : function() { window.location.href = "/pgadmissions/400"; },
            403 : function() { window.location.href = "/pgadmissions/404"; }
        },
        url : "/pgadmissions/editApplicationFormAsProgrammeAdmin/postRefereesData",
        data :  {
            applicationId : $('#applicationId').val(),
            email : $('#refereeEmail_' + refereeId).val(),
            telephone : $('#refereeTelephone_' + refereeId).val(),
            skype: $('#refereeSkype_' + refereeId).val(),
            comment: $('#refereeComment_' + refereeId).val(),
            documents: documents,
            "suitableForUCL" : suitableUCL,
            "suitableForProgramme" : suitableForProgramme, 
            editedRefereeId : $('#editedRefereeId').val(),
            sendToUclData: JSON.stringify(sendToUclData),
            cacheBreaker: new Date().getTime()
        },
        success : function(data) {
            $("#referencesSection").html(data);
            $("#referee_" + $("#editedRefereeId").val()).show();
        },
        complete : function() {
            $('#referencesSection div.ajax').remove();
        }
    });
}

function postQualificationsData() {
    $('#qualificationsSection > div').append('<div class="ajax" />');
    var sendToUclData = {
            qualifications : new Array(),
    };
    
    $('input[name="qualificationSendToUcl"]:checkbox').each(function() {
        var checked = $(this).attr("checked");
        if (checked) {
        	sendToUclData.qualifications.push($(this).val());
        }
    });
    
    $.ajax({
        type : 'POST',
        statusCode : {
            401 : function() { window.location.reload(); },
            500 : function() { window.location.href = "/pgadmissions/error"; },
            404 : function() { window.location.href = "/pgadmissions/404"; },
            400 : function() { window.location.href = "/pgadmissions/400"; },
            403 : function() { window.location.href = "/pgadmissions/404"; }
        },
        url : "/pgadmissions/editApplicationFormAsProgrammeAdmin/postQualificationsData",
        data :  {
            applicationId : $('#applicationId').val(),
            sendToUclData: JSON.stringify(sendToUclData),
            cacheBreaker: new Date().getTime()
        },
        success : function(data) {
        },
        complete : function() {
            $('#qualificationsSection div.ajax').remove();
        }
    });
}