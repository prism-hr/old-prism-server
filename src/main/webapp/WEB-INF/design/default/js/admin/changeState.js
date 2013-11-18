var originalPostUrl;

$(document).ready(function() {
    originalPostUrl = $('#stateChangeForm').attr('action');
    refreshControls();

    // ------------------------------------------------------------------------------
    // Submit button to change the status of the application.
    // Looks for the checkbox in the section to confirm. If it can't find,
    // then falls back to old behaviour.
    // ------------------------------------------------------------------------------
    $('#changeStateButton').click(function(e) {
        var section = $(this).closest('section.form-rows');
        if (section.length == 1 && section.find('#confirmNextStage').length > 0 && section.find('#status').length > 0) {
            changeState();
        } else {
            var state = $('#status option:selected').text().toLowerCase().capitalize();
            var message = 'Confirm that you want to move this application to the ' + state + ' stage.';
            modalPrompt(message, changeState);
        }
    });

    // ------------------------------------------------------------------------------
    // Update the checkbox confirmation label status value.
    // ------------------------------------------------------------------------------
    $('#status').change(function() {
        if ($('#confirmNextStageLabel').length > 0) {
            var state = $('#status option:selected').text().toLowerCase().capitalize();
            if (state.length == 0 || state == 'Select...') {
                state = 'next';
            }

            var message = 'Confirm that you want to move this application to the ' + state + ' stage.';

            $('#confirmNextStageLabel').html(message);
        }
    });

    $('#status').change();

    // ------------------------------------------------------------------------------
    // Delegate application processing radio buttons.
    // ------------------------------------------------------------------------------
    $('#status').change(function() {
        refreshControls();
        $('#stateChangeForm').attr('action', originalPostUrl);
    });

    $('input:radio[name=switch]').change(function() {
        refreshControls();
    });
    
});

// ------------------------------------------------------------------------------
// Save the comment leading to the next stage.
// ------------------------------------------------------------------------------
function saveComment() {
    $('#nextStatus').val($('#status').val());

    $('#commentField').val($('#state_change_comment').val());

    if ($('input:radio[name=fastTrackProcessing]:checked').length > 0) {
    	var fastTrack = $('input:radio[name=fastTrackProcessing]')[1].checked;
    	$('#stateChangeForm').append('<input type="hidden" name="fastTrackApplication" value="'+fastTrack+'"/>');
    }
    
    if ($('input:radio[name=qualifiedForPhd]:checked').length > 0) {
        $('#stateChangeForm').append('<input type="hidden" name="qualifiedForPhd" value="' + $('input:radio[name=qualifiedForPhd]:checked').val() + '"/>');
    }
    if ($('input:radio[name=englishCompentencyOk]:checked').length > 0) {
        $('#stateChangeForm').append('<input type="hidden" name="englishCompentencyOk" value="' + $('input:radio[name=englishCompentencyOk]:checked').val() + '"/>');
    }
    if ($('input:radio[name=homeOrOverseas]:checked').length > 0) {
        $('#stateChangeForm').append('<input type="hidden" name="homeOrOverseas" value="' + $('input:radio[name=homeOrOverseas]:checked').val() + '"/>');
    }
    $('input[name="documents"]').each(function() {
        $('#stateChangeForm').append('<input type="hidden" name="documents" value="' + $(this).val() + '"/>');
    });

    $('#firstName').val($('#delegateFirstName').val());
    $('#lastName').val($('#delegateLastName').val());
    $('#email').val($('#delegateEmail').val());

    if ($('#confirmNextStage').length > 0) {
        $('#confirmNextStageField').val($('#confirmNextStage')[0].checked);
    }

    $('#stateChangeForm').submit();
}

function changeState() {
    if ($('#status').val() == 'REVIEW' ||
    	$('#status').val() == 'INTERVIEW' ||
    	$('#status').val() == 'APPROVAL') {
        if ($('input:radio[name=switch]:checked').val() == 'yes') {
        	saveDelegatedAdministrator();
        	return;
        } 
    }
    saveComment();
    return;
}

function refreshControls() {
	if ($('#status').val() == 'REVIEW' ||
	    $('#status').val() == 'INTERVIEW' ||
	    $('#status').val() == 'APPROVAL') {
        $("#approvedDetails").hide();
        $("#interviewDelegation").show();
        $("#interviewDelegation").find("div.alert").remove();
        
        $("#fastTrackApplicationSection").show();
        if ($('input:radio[name=switch]:checked').val() == 'yes') {
            $('#delegateFirstName').removeAttr('disabled');
            $('#delegateLastName').removeAttr('disabled');
            $('#delegateEmail').removeAttr('disabled');
            $('#delegateFristName').removeAttr('disabled');
            $('#delegateFirstNameLabel').removeClass('grey-label').parent().find('.hint').removeClass("grey");
            $('#delegateLastNameLabel').removeClass('grey-label').parent().find('.hint').removeClass("grey");
            $('#delegateEmailLabel').removeClass('grey-label').parent().find('.hint').removeClass("grey");
        } else {
            $('#delegateFirstName').val("");
            $('#delegateLastName').val("");
            $('#delegateEmail').val("");
            $('#delegateFirstName').attr('disabled', 'disabled');
            $('#delegateLastName').attr('disabled', 'disabled');
            $('#delegateEmail').attr('disabled', 'disabled');
            $('#delegateFirstNameLabel').addClass("grey-label").parent().find('.hint').addClass("grey");
            $('#delegateLastNameLabel').addClass("grey-label").parent().find('.hint').addClass("grey");
            $('#delegateEmailLabel').addClass("grey-label").parent().find('.hint').addClass("grey");
        }
    } else if ($('#status').val() == 'APPROVED') {
        $("#interviewDelegation").hide();
        $("#fastTrackApplicationSection").hide();
    } else if ($('#status').val() == 'REVIEW') {
        $("#fastTrackApplicationSection").show();
        $("#interviewDelegation").hide();
    } else if ($('#status').val() == 'REQUEST_RESTART_APPROVAL') {
    	$("#interviewDelegation").hide();
    	$("#fastTrackApplicationSection").show();
    } else if ($('#status').val() == 'APPROVAL') {
    	$("#interviewDelegation").hide();
    	$("#fastTrackApplicationSection").show();
    } else {
        $("#fastTrackApplicationSection").hide();
        $("#interviewDelegation").hide();
        $("#approvedDetails").hide();
        $('input:radio[name=switch]')[0].checked = true;
    }
}

function isProjectAcceptingApplications(){
	if("$input[name=acceptingApplications]"){
		return acceptingApplications = ($('input:radio[name=acceptingApplications]:checked').val() === "true" ? true : false);
	}
	return null;
}

function saveDelegatedAdministrator() {
	 $.ajax({
         type : 'POST',
         statusCode : {
             401 : function() { window.location.reload(); },
             500 : function() { window.location.href = "/pgadmissions/error"; },
             404 : function() { window.location.href = "/pgadmissions/404"; },
             400 : function() { window.location.href = "/pgadmissions/400"; },
             403 : function() { window.location.href = "/pgadmissions/404"; }
         },
         url : "/pgadmissions/delegate",
         data : {
             applicationId : $('#applicationId').val(),
             firstName : $('#delegateFirstName').val(),
             lastName : $('#delegateLastName').val(),
             email : $('#delegateEmail').val(),
             confirmNextStage : $('#confirmNextStage').val()
         },
         success : function(data) {
             $("#interviewDelegation").find("div.alert").remove();
             if (data.success == "false") {
                 if (data.firstName != null) {
                     $('#delegateFirstName').parent().append('<div class="alert alert-error"> <i class="icon-warning-sign"></i> ' + data.firstName + '</div>');
                 }
                 if (data.lastName != null) {
                     $('#delegateLastName').parent().append('<div class="alert alert-error"> <i class="icon-warning-sign"></i> ' + data.lastName + '</div>');
                 }
                 if (data.email != null) {
                     $('#delegateEmail').parent().append('<div class="alert alert-error"> <i class="icon-warning-sign"></i> ' + data.email + '</div>');
                 }
             } else {
                 $('#delegate').val('true');
                 saveComment();
             }
         }
     });
}