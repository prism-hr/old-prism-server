$(document).ready(function()
{
	// ------------------------------------------------------------------------------
	// Submit button.
	// ------------------------------------------------------------------------------
	$('#changeStateButton').click(function()
	{
		if ($('#status').val() != '')
		{
			var message = 'Confirm you want to move this application to the ' + $('#status').val() + ' stage.';
			modalPrompt(message, changeState);
			return;
		}
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
		else
		{
			// disable the delegation dropdown box.
			$('#applicationAdministrator').attr('disabled', 'disabled');
			$('#delegateLabel').addClass('grey-label');
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
	$('input[name="documents"]').each(function()
	{
		$('#stateChangeForm').append(
				'<input type="hidden" name="documents" value="' + $(this).val() + '"/>');
	});
	 $('#stateChangeForm').submit();
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
		if ($('#applicationAdministrator').length == 0 || $('#applicationAdministrator').val() == '')
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
					}
				},
				url:"/pgadmissions/delegate",
				data:{
					applicationId : $('#applicationId').val(),
					applicationAdministrator : $('#applicationAdministrator').val()
				},
				success:function(data)
				{
					saveComment();
				}
			});
		}
	}
}
