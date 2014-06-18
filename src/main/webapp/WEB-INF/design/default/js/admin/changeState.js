var originalPostUrl;

$(document).ready(function() {
    refreshControls();

    // ------------------------------------------------------------------------------
    // Submit button to change the nextStatus of the application.
    // ------------------------------------------------------------------------------
    $('#changeStateButton').click(function(e) {
    	$('#stateChangeForm').submit();
    });

    // ------------------------------------------------------------------------------
    // Update the check-box confirmation label nextStatus value.
    // ------------------------------------------------------------------------------
    $('#nextStatus').change(function() {
    	refreshControls(); 
        if ($('#confirmNextStageLabel').length > 0) {
        	var state = $('#nextStatus option:selected').text();
            var stateDisplay = state.toLowerCase().capitalize();
            if (stateDisplay.length == 0 || stateDisplay == 'Select...') {
                stateDisplay = 'Next';
            }
            var message = 'Confirm that you want to move this application to the ' + stateDisplay + ' stage.';
            $('#confirmNextStageLabel').html(message);
            var customQuestionCoverage = new Array();
            $('input:hidden[name=customQuestionCoverage]').each(function() {
            	customQuestionCoverage.push($(this).val());
            });
            if(jQuery.inArray(state.toUpperCase(), customQuestionCoverage) >= 0) {
            	$('#customQuestionSection').show();
            } else {
            	$('input:radio[name=useCustomQuestions]').each(function() {
                	$(this).prop('checked', false);
                });
            	$('#customQuestionSection').hide();
            }
            
        }
    });

    $('#nextStatus').change();

    // ------------------------------------------------------------------------------
    // Delegate application processing radio buttons.
    // ------------------------------------------------------------------------------
    $('input:radio[name=delegate]').change(function() {
        refreshControls();
    });
    
});

// ------------------------------------------------------------------------------
// Refresh the controls in the delegation section.
// ------------------------------------------------------------------------------
function refreshControls() {
	if ($('#nextStatus').val() == 'REVIEW' ||
	    $('#nextStatus').val() == 'INTERVIEW' ||
	    $('#nextStatus').val() == 'APPROVAL') {
        $("#fastTrack").show();
        $("#interviewDelegation").show();
        if ($('input:radio[name=delegate]:checked').val() == 'true') {
            $('#delegateFirstName').removeAttr('disabled');
            $('#delegateLastName').removeAttr('disabled');
            $('#delegateEmail').removeAttr('disabled');
            $('#delegateFristName').removeAttr('disabled');
            $('#delegateFirstNameLabel').removeClass('grey-label').parent().find('.hint').removeClass("grey");
            $('#delegateLastNameLabel').removeClass('grey-label').parent().find('.hint').removeClass("grey");
            $('#delegateEmailLabel').removeClass('grey-label').parent().find('.hint').removeClass("grey");
        } else {
            $("#interviewDelegation").find("div.alert").remove();
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
    } else if ($('#nextStatus').val() == 'APPROVED' || 
    		$('#nextStatus').val() == 'REJECTED') {
        $("#fastTrack").hide();
        $("#interviewDelegation").hide();
    }
}