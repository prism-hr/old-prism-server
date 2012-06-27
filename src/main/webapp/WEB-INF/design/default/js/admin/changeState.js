$(document).ready(function()
{

	// Submit button.
	$('#changeStateButton').click(function()
	{
		if (!confirm('Confirm you want to move this application to the ' + $('#status').val() + ' stage.'))
		{
			return;
		}
		
		if ($('#status').val() == 'APPROVAL' ||  $('#status').val() == 'APPROVED' ||  $('#status').val() == 'REJECTED' || $('#status').val() == 'REVIEW')
		{
			saveComment();
			return;
		}
	
		if ($('#status').val() == 'INTERVIEW')
		{
			if ($('#appliationAdmin').length == 0	|| $('#appliationAdmin').val() == '')
			{
				saveComment();
				return;
			}
			else
			{
				$('#delegateForm').submit();
			}
		}
	});


	$('#status').change(function()
	{
		if ($('#status').val() == 'INTERVIEW')
		{
			$('#appliationAdmin').removeAttr('disabled');
		}
		else
		{
			$('#appliationAdmin')
		}
	});


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
			}
		});
		
		return false;
	});
	
});


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
