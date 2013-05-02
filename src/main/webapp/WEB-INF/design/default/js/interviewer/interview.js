$(document).ready(function() {
	
    getInterviewersAndDetailsSections();

    getCreateInterviewersSection();

    // -----------------------------------------------------------------------------------------
    // Add interviewer
    // -----------------------------------------------------------------------------------------
    $('#assignInterviewersToAppSection').on('click', '#addInterviewerBtn', function() {
        var selectedReviewers = $('#programInterviewers').val();
        if (selectedReviewers) {
            for ( var i in selectedReviewers) {
                var id = selectedReviewers[i];
                var $option = $("#programInterviewers option[value='" + id + "']");
                var selText = $option.text();
                var category = $option.attr("category");
                $("#programInterviewers option[value='" + id + "']").addClass('selected').removeAttr('selected').attr('disabled', 'disabled');
                $("#applicationInterviewers").append('<option value="' + id + '" category="' + category + '">' + selText + '</option>');
            }
            $('#applicationInterviewers').attr("size", $('#applicationInterviewers option').size() + 1);
            resetInterviwersErrors();
        }
    });

    // -----------------------------------------------------------------------------------------
    // Create interviewer button.
    // -----------------------------------------------------------------------------------------
    $('#createinterviewersection').on('click', '#createInterviewer', function() {
        $('#ajaxloader').show();
        var postData = {
            applicationId : $('#applicationId').val(),
            firstName : $('#newInterviewerFirstName').val(),
            lastName : $('#newInterviewerLastName').val(),
            email : $('#newInterviewerEmail').val()
        };
        
        $.ajax({
            type : 'POST',
            statusCode : {
                401 : function() {
                    window.location.reload();
                },
                500 : function() {
                    window.location.href = "/pgadmissions/error";
                },
                404 : function() {
                    window.location.href = "/pgadmissions/404";
                },
                400 : function() {
                    window.location.href = "/pgadmissions/400";
                },
                403 : function() {
                    window.location.href = "/pgadmissions/404";
                }
            },
            url : "/pgadmissions/interview/createInterviewer",
            data : $.param(postData),
            success : function(data) {
                var newInterviewer;
                try {
                    newInterviewer = jQuery.parseJSON(data);
                } catch (err) {
                    $('#createinterviewersection').html(data);
                    addToolTips();
                    return;
                }
                if (newInterviewer.isNew) {
                    $('#previous').append('<option value="' + newInterviewer.id + '" category="previous" disabled="disabled">' + newInterviewer.firstname + ' ' + newInterviewer.lastname + '</option>');
                    $('#applicationInterviewers').append('<option value="' + newInterviewer.id + '">' + newInterviewer.firstname + ' ' + newInterviewer.lastname + '</option>');
                    $('#applicationInterviewers').attr("size", $('#applicationInterviewers option').size() + 1);

                } else {
                    addExistingUserToInterviewersLists(newInterviewer);
                }
                resetInterviwersErrors();
                getCreateInterviewersSection();
                addToolTips();
            },
            complete : function() {
                $('#ajaxloader').fadeOut('fast');
				addCounter();
            }
        });

    });

    // -----------------------------------------------------------------------------------------
    // Remove interviewer
    // -----------------------------------------------------------------------------------------
    $('#assignInterviewersToAppSection').on('click', '#removeInterviewerBtn', function() {
        var selectedReviewers = $('#applicationInterviewers').val();
        if (selectedReviewers) {
            for ( var i in selectedReviewers) {
                var id = selectedReviewers[i];
                $("#applicationInterviewers option[value='" + id + "']").remove();
                $("#programInterviewers option[value='" + id + "']").removeClass('selected').removeAttr('disabled');
            }
            $('#applicationInterviewers').attr("size", $('#applicationInterviewers option').size() + 1);
            resetInterviwersErrors();
        }
    });

    // -----------------------------------------------------------------------------------------
    // Submit button.
    // -----------------------------------------------------------------------------------------
    $('#moveToInterviewBtn').click(function() {
        $('#interviewsection div.alert-error').remove();
        $('#ajaxloader').show();
        var url = "/pgadmissions/interview/move";

        $('#applicationInterviewers option').each(function() {
            $('#postInterviewData').append("<input name='interviewers' type='text' value='" + $(this).val() + "'/>");
        });

        var stage = null;
        if($('input:radio[name=interviewStatus]:checked').length > 0){
        	stage = $('input:radio[name=interviewStatus]:checked').val();
        }
        
        var postData = {
            applicationId : $('#applicationId').val(),
            interviewers : '',
            interviewTime : $('#hours').val() + ":" + $('#minutes').val(),
            interviewDueDate : $('#interviewDate').val(),
        };
        
        if(stage != null){
        	postData.stage = stage;
        }
        
        if(stage == 'SCHEDULED'){
        	postData.furtherDetails = $('#furtherDetails').val();
        	postData.furtherInterviewerDetails = $('#furtherInterviewerDetails').val();
        	postData.locationURL = $('#interviewLocation').val();
        }
        
        
        
        $.ajax({
            type : 'POST',
            statusCode : {
                401 : function() {
                    window.location.reload();
                },
                500 : function() {
                    window.location.href = "/pgadmissions/error";
                },
                404 : function() {
                    window.location.href = "/pgadmissions/404";
                },
                400 : function() {
                    window.location.href = "/pgadmissions/400";
                },
                403 : function() {
                    window.location.href = "/pgadmissions/404";
                }
            },
            url : url,
            data : $.param(postData) + "&" + $('input[name="interviewers"]').serialize(),
            success : function(data) {
                if (data == "OK") {
                    window.location.href = '/pgadmissions/applications?messageCode=move.interview&application=' + $('#applicationId').val();

                } else {
                    $('#temp').html(data);
                    $('#assignInterviewersToAppSection').html($('#section_1').html());
                    $('#interviewStatus').html($('#section_interview_status').html());
                    $('#interviewdetailsSection').html($('#section_2').html());
                    $('#temp').empty();
                    $('#postInterviewData').empty();
                    addToolTips();
                    $('#interviewDate').attr("readonly", "readonly");
                    $('#interviewDate').datepicker({
                        dateFormat : 'dd M yy',
                        changeMonth : true,
                        changeYear : true,
                        yearRange : '1900:+20'
                    });
                    
                    	
                    bindDatePicker('#availableDates');
                    
                    var interviewStatus = $('input[name=interviewStatus]:radio');
                	interviewStatus.change(showProperInterviewArrangements);
                	interviewStatus.change();
                }
                addToolTips();
            },
            complete : function() {
                $('#ajaxloader').fadeOut('fast');
				addCounter();
            }
        });
    });
});

function getInterviewersAndDetailsSections() {
    $('#ajaxloader').show();

    var url = "/pgadmissions/interview/interviewers_section";

    $.ajax({
        type : 'GET',
        statusCode : {
            401 : function() {
                window.location.reload();
            },
            500 : function() {
                window.location.href = "/pgadmissions/error";
            },
            404 : function() {
                window.location.href = "/pgadmissions/404";
            },
            400 : function() {
                window.location.href = "/pgadmissions/400";
            },
            403 : function() {
                window.location.href = "/pgadmissions/404";
            }
        },
        url : url + "?applicationId=" + $('#applicationId').val(),
        success : function(data) {
            $('#temp').html(data);
            $('#assignInterviewersToAppSection').html($('#section_1').html());
            
            $('#interviewStatus').html($('#section_interview_status').html());
            
            $('#interviewdetailsSection').html($('#section_2').html());
            
            $('#temp').empty();

            addToolTips();
            $('#interviewDate').attr("readonly", "readonly");
            $('#interviewDate').datepicker({
                dateFormat : 'dd M yy',
                changeMonth : true,
                changeYear : true,
                yearRange : '1900:+20'
            });

            bindDatePicker('#availableDates');
            
            // Interview status.
        	var interviewStatus = $('input[name=interviewStatus]:radio');
        	
        	interviewStatus.change(showProperInterviewArrangements);
        	interviewStatus.change();
        },
        complete : function() {
            $('#ajaxloader').fadeOut('fast');
			addCounter();
        }
    });
}

function getCreateInterviewersSection() {
    $('#ajaxloader').show();

    $.ajax({
        type : 'GET',
        statusCode : {
            401 : function() {
                window.location.reload();
            },
            500 : function() {
                window.location.href = "/pgadmissions/error";
            },
            404 : function() {
                window.location.href = "/pgadmissions/404";
            },
            400 : function() {
                window.location.href = "/pgadmissions/400";
            },
            403 : function() {
                window.location.href = "/pgadmissions/404";
            }
        },
        url : "/pgadmissions/interview/create_interviewer_section?applicationId=" + $('#applicationId').val(),
        success : function(data) {
            $('#createinterviewersection').html(data);
        },
        complete : function() {
            $('#ajaxloader').fadeOut('fast');
        }
    });
}

function addExistingUserToInterviewersLists(newInterviewer) {

    if ($('#applicationInterviewers option[value="' + newInterviewer.id + '"]').length > 0) {

        return;
    }

    if ($('#default option[value="' + newInterviewer.id + '"]').length > 0) {
        $('#default option[value="' + newInterviewer.id + '"]').attr("selected", 'selected');
        $('#addInterviewerBtn').trigger('click');

        return;
    }

    if ($('#previous option[value="' + newInterviewer.id + '"]').length > 0) {
        $('#previous option[value="' + newInterviewer.id + '"]').attr("selected", 'selected');
        $('#addInterviewerBtn').trigger('click');

        return;
    }

    $('#previous').append('<option value="' + newInterviewer.id + '" category="previous" disabled="disabled">' + newInterviewer.firstname + ' ' + newInterviewer.lastname + '</option>');
    $('#applicationInterviewers').append('<option value="' + newInterviewer.id + '">' + newInterviewer.firstname + ' ' + newInterviewer.lastname + '</option>');
    $('#applicationInterviewers').attr("size", $('#applicationInterviewers option').size() + 1);

}

function resetInterviwersErrors() {
    if ($('#applicationInterviewers option').size() > 0) {
        $('#interviewersErrorSpan').remove();
    }
}

function showProperInterviewArrangements() {
	var interviewStatus = $('input[name=interviewStatus]:radio:checked').val();
	
	$('.interview-happened').hide();
	$('.interview-scheduled').hide();
	$('.interview-to-schedule').hide();
	
	switch (interviewStatus) {
	case 'TAKEN_PLACE':
		$('.interview-happened').show();
		break;
	case 'SCHEDULED':
		$('.interview-scheduled').show();
		break;
	default:
		$('.interview-to-schedule').show();
		break;
	}
}