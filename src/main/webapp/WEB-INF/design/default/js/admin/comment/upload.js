$(document).ready(function() {
	watchUpload($('#commentDocument'), null);
	
	$('#referenceSaveButton').click(function()
	{
		if (!validateReference())
		{
			return false;
		}
		
		var message = 'Please confirm that you are satisfied with your comments. <b>You will not be able to change them.</b>';
		var onOk    = function()
		{
			$('#documentUploadForm').submit();
		};
		var onCancel = function(){};
		
		modalPrompt(message, onOk, onCancel);
		return false;
	});
	

});

function validateReference()
{
	var errors = 0;
	$('#reviewForm span.invalid').remove();
	
	if ($('#comment').val() == '')
	{
		$('#comment').after('<span class="invalid">You must make an entry.</span>');
		errors++;
	}

	if ($('input[name="suitableForUCL"]:checked').length == 0)
	{
		$('#field-issuitableucl').append('<span class="invalid">You must make a selection.</span>');
		errors++;
	}

	if ($('input[name="suitableForProgramme"]:checked').length == 0)
	{
		$('#field-issuitableprog').append('<span class="invalid">You must make a selection.</span>');
		errors++;
	}
	
	return (errors == 0);
}