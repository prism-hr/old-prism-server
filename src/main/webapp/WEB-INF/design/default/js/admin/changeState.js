var originalPostUrl;

$(document).ready(function()
{
    originalPostUrl = $('#stateChangeForm').attr('action');
    var suggestions = [];
    var selectedDates = {};
    
    refreshDelegationControls();
    
    // -------------------------------------------------------------------------------
    // Initialise datepicker with highligted dates
    // -------------------------------------------------------------------------------
    $.ajax({
        type: 'GET',
        statusCode: {
                401: function() { window.location.reload(); },
                500: function() { window.location.href = "/pgadmissions/error"; },
                404: function() { window.location.href = "/pgadmissions/404"; },
                400: function() { window.location.href = "/pgadmissions/400"; },                  
                403: function() { window.location.href = "/pgadmissions/404"; }
        },
        url:"/pgadmissions/progress/getClosingDates",
        data: {
        	applicationId: $("#applicationId").val(),
        	cacheBreaker: new Date().getTime()
        }, 
        success: function(data) {
        	selectedDates = [];
            selectedDates = jQuery.parseJSON(data);
        },
        completed: function() {
        }
    });

	$('#closingDate').datepicker({
		dateFormat: 'dd M yy',
		changeMonth: true,
		changeYear: true,
		yearRange: '1900:+20',
		beforeShowDay: function(date) {
			for (var i = 0; i < selectedDates.length; i++) {
	            if (new Date(selectedDates[i]).toString() == date.toString()) {
	                return [true, 'highlighted', ''];
	            }
	        }
			return [true, '', ''];
		}
	});
	
	// -------------------------------------------------------------------------------
    // The autocomplete box for the project title.
    // -------------------------------------------------------------------------------
	$("input#projectTitle").autocomplete({
	    delay:150,
	    source: function(req, add) {
	        
	    	$.ajax({
	            type: 'GET',
	            statusCode: {
	                    401: function() { window.location.reload(); },
	                    500: function() { window.location.href = "/pgadmissions/error"; },
	                    404: function() { window.location.href = "/pgadmissions/404"; },
	                    400: function() { window.location.href = "/pgadmissions/400"; },                  
	                    403: function() { window.location.href = "/pgadmissions/404"; }
	            },
	            url:"/pgadmissions/progress/getProjectTitles",
	            data: {
	            	applicationId: $("#applicationId").val(),
	                term: req.term
	            }, 
	            success: function(data) {
	                suggestions = [];
	                suggestions = jQuery.parseJSON(data);
	            },
	            completed: function() {
	            }               
	        });
	        add(suggestions);
	    }
	});

	// ------------------------------------------------------------------------------
	// Submit button.
	// ------------------------------------------------------------------------------
	$('#changeStateButton').click(function()
	{
		var state = $('#status option:selected').text().toLowerCase().capitalize();
		var message = 'Confirm you want to move this application to the ' + state + ' stage. <b>You will not be able to reverse this decision!</b>';
		modalPrompt(message, changeState);
	});


    // ------------------------------------------------------------------------------
    // Delegate application processing radio buttons.
    // ------------------------------------------------------------------------------
    $('#status').change(function()
    {
        refreshDelegationControls();
        
        if ($('#status').val() == 'REQUEST_RESTART_APPROVAL') {
            $('#stateChangeForm').attr('action', '/pgadmissions/approval/submitRequestRestart');
        } else { 
            $('#stateChangeForm').attr('action', originalPostUrl);
        }
        
    });
    
    $('input:radio[name=switch]').change(function()
    {
        refreshDelegationControls();
    });


	// ------------------------------------------------------------------------------
	// Link to request assistance from the registry.
	// ------------------------------------------------------------------------------
	$('#notifyRegistryButton').click(function()
	{
		$('#commentsection').append('<div class="ajax" />');
		$.ajax({
			type: 'POST',
			 statusCode: {
					401: function() {
						window.location.reload();
					},
					  500: function() {
						  window.location.href = "/pgadmissions/error";
					  },
					  404: function() {
						  window.location.href = "/pgadmissions/404";
					  },
					  400: function() {
						  window.location.href = "/pgadmissions/400";
					  },				  
					  403: function() {
						  window.location.href = "/pgadmissions/404";
					  }
				},
			url:"/pgadmissions/registryHelpRequest",
			data:{
				applicationId : $('#applicationId').val()
			},
			success:function(data)
			{
				$('#emailMessage').html(data);
				$('#notifyRegistryButton').removeAttr('disabled');
				$('#notifyRegistryButton').addClass("blue");

				window.location.href = '/pgadmissions/applications?messageCode=registry.refer&application=' + $('#applicationId').val();
				addToolTips();
			}
		});
		
		return false;
	});
});


// ------------------------------------------------------------------------------
// Save the comment leading to the next stage.
// ------------------------------------------------------------------------------
function saveComment()
{
	$('#nextStatus').val($('#status').val());

	$('#commentField').val($('#comment').val());

	if ($('input:radio[name=qualifiedForPhd]:checked').length > 0) 
	{
		$('#stateChangeForm').append(
				'<input type="hidden" name="qualifiedForPhd" value="' + $('input:radio[name=qualifiedForPhd]:checked').val() + '"/>');
	}
	if ($('input:radio[name=englishCompentencyOk]:checked').length > 0)
	{
		$('#stateChangeForm').append(
				'<input type="hidden" name="englishCompentencyOk" value="' + $('input:radio[name=englishCompentencyOk]:checked').val() + '"/>');
	}
	if ($('input:radio[name=homeOrOverseas]:checked').length > 0)
	{
		$('#stateChangeForm').append(
				'<input type="hidden" name="homeOrOverseas" value="' + $('input:radio[name=homeOrOverseas]:checked').val() + '"/>');
	}
	if ($('#projectTitle').length > 0)
    {
        $('#stateChangeForm').append(
                '<input type="hidden" name="projectTitle" value="' + $('#projectTitle').val() + '"/>');
    }
	if ($('#closingDate').length > 0)
    {
        $('#stateChangeForm').append(
                '<input type="hidden" name="closingDate" value="' + $('#closingDate').val() + '"/>');
    }
	$('input[name="documents"]').each(function()
	{
		$('#stateChangeForm').append(
				'<input type="hidden" name="documents" value="' + $(this).val() + '"/>');
	});
	
	$('#firstName').val($('#newInterviewerFirstName').val());
	$('#lastName').val($('#newInterviewerLastName').val());
	$('#email').val($('#newInterviewerEmail').val());
	
	$('#stateChangeForm').submit();
}

function getCreateInterviewersSection() {
    $('#createInterviewerSection').append('<div class="ajax" />');

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
            $('#createInterviewerSection').html(data);
        },
        complete : function() {
            $('#createInterviewerSection div.ajax').remove();
        }
    });
}

function changeState()
{
	if ($('#status').val() != 'INTERVIEW')
	{
		saveComment();
		return;
	}
	
	if ($('#status').val() == 'INTERVIEW')
	{
		if ($('input:radio[name=switch]:checked').val() != 'yes')
		{
			saveComment();
			return;
		}
		else
		{
			$.ajax({
				type: 'POST',
				statusCode: {
					401: function() {
						window.location.reload();
					},
					  500: function() {
						  window.location.href = "/pgadmissions/error";
					  },
					  404: function() {
						  window.location.href = "/pgadmissions/404";
					  },
					  400: function() {
						  window.location.href = "/pgadmissions/400";
					  },				  
					  403: function() {
						  window.location.href = "/pgadmissions/404";
					  }
				},
				url:"/pgadmissions/delegate",
				data:{
					applicationId : $('#applicationId').val(),
					firstName : $('#newInterviewerFirstName').val(),
					lastName : $('#newInterviewerLastName').val(),
					email : $('#newInterviewerEmail').val(),
				},
				success:function(data)
				{
				    $("#interviewDelegation").find("div.alert").remove();
		            if(data.success == "false"){
	                    if (data.firstName != null) {
	                        $('#newInterviewerFirstName').parent().append('<div class="alert alert-error"> <i class="icon-warning-sign"></i>' + data.firstName + '</div>');
	                    } 
	                    if (data.lastName != null) {
                            $('#newInterviewerLastName').parent().append('<div class="alert alert-error"> <i class="icon-warning-sign"></i>' + data.lastName + '</div>');
                        }
	                    if (data.email != null) {
                            $('#newInterviewerEmail').parent().append('<div class="alert alert-error"> <i class="icon-warning-sign"></i>' + data.email + '</div>');
                        }
		            } else {
    					$('#delegate').val('true');
    					saveComment();
				    }
				}
			});
		}
	}
}

function refreshDelegationControls() {
    if ($('#status').val() == 'INTERVIEW') {
    	$("#interviewDelegation").show();
        if($('input:radio[name=switch]:checked').val() == 'yes'){
        	$('#delegateFirstName').removeAttr('disabled');
        	$('#delegateLastName').removeAttr('disabled');
        	$('#delegateEmail').removeAttr('disabled');
        	$('#delegateFristName').removeAttr('disabled');
        	$('#delegateFirstNameLabel').removeClass('grey-label').parent().find('.hint').removeClass("grey");
        	$('#delegateLastNameLabel').removeClass('grey-label').parent().find('.hint').removeClass("grey");
        	$('#delegateEmailLabel').removeClass('grey-label').parent().find('.hint').removeClass("grey");
        } else {
        	$('#delegateFirstName').attr('disabled', 'disabled');
        	$('#delegateLastName').attr('disabled', 'disabled');
        	$('#delegateEmail').attr('disabled', 'disabled');
        	$('#delegateFirstNameLabel').addClass("grey-label").parent().find('.hint').addClass("grey");
        	$('#delegateLastNameLabel').addClass("grey-label").parent().find('.hint').addClass("grey");
        	$('#delegateEmailLabel').addClass("grey-label").parent().find('.hint').addClass("grey");
        }
    } else {
    	$("#interviewDelegation").hide();
        $('input:radio[name=switch]')[0].checked = true;
    }     
    

}
