var originalPostUrl;

$(document).ready(function()
{
    originalPostUrl = $('#stateChangeForm').attr('action');
    var suggestions = [];
    var selectedDates = {};
    
    getCreateInterviewersSection();
    
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
	// Next stage dropdown field.
	// ------------------------------------------------------------------------------
	$('#status').change(function()
	{
		if ($('#status').val() == 'INTERVIEW')
		{
			// enable the delegation dropdown box.
			$('#applicationAdministrator').removeAttr('disabled');
			$('#delegateLabel').removeClass('grey-label');
		}
		else if ($('#status').val() == 'REQUEST_RESTART_APPROVAL') 
		{
		    $('#stateChangeForm').attr('action', '/pgadmissions/approval/submitRequestRestart');
		}
		else
		{
			// disable the delegation dropdown box.
			$('#applicationAdministrator').attr('disabled', 'disabled');
			$('#delegateLabel').addClass('grey-label');
		}
		
		if ($('#status').val() != 'REQUEST_RESTART_APPROVAL') { 
		    $('#stateChangeForm').attr('action', originalPostUrl);
		}
		
	});
	
	
    // ------------------------------------------------------------------------------
    // Delegate application processing radio buttons.
    // ------------------------------------------------------------------------------
    $('#status').change(function()
    {
        if ($('#status').val() == 'INTERVIEW')
        {
            // enable the delegation dropdown box.
            $('#applicationAdministrator').removeAttr('disabled');
            $('#delegateLabel').removeClass('grey-label');
        }
        else if ($('#status').val() == 'REQUEST_RESTART_APPROVAL') 
        {
            $('#stateChangeForm').attr('action', '/pgadmissions/approval/submitRequestRestart');
        }
        else
        {
            // disable the delegation dropdown box.
            $('#applicationAdministrator').attr('disabled', 'disabled');
            $('#delegateLabel').addClass('grey-label');
        }
        
        if ($('#status').val() != 'REQUEST_RESTART_APPROVAL') { 
            $('#stateChangeForm').attr('action', originalPostUrl);
        }
        
    });
    
    $('input:radio[name=switch]').change(function()
    {
        if($(this).val() == 'yes'){
            $("#interviewDelegation").show();
        } else if ($(this).val() == 'no'){
            $("#interviewDelegation").hide();
        }
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
					$('#delegate').val('true');
					saveComment();
				}
			});
		}
	}
}
