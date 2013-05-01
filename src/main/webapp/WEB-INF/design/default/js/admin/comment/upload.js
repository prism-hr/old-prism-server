$(document).ready(function() {
	watchUploadComment($('#commentDocument'), $('#commentUploadedDocument'));
	
	$('#referenceSaveButton').click(function()
	{
		/*if (!validateReference())
		{
			return false;
		}*/
		
		var onOk    = function()
		{
			$('#documentUploadForm').submit();
		};
		
		var section = $(this).closest('section.form-rows');
		if (section.length == 1 && section.find('#confirmNextStage').length > 0) {
			onOk();
		}
		else {
			var message = 'Please confirm that you are satisfied with your comments. <b>You will not be able to change them.</b>';
			var onCancel = function(){};
			modalPrompt(message, onOk, onCancel);
		}
		
		return false;
	});
});

function validateReference()
{
	var errors = 0;
	$('#reviewForm .alert-error').remove();
	$('#documentUploadForm .alert-error').remove();
	$('#documentUploadForm .extrafield').remove();
	
	
	if ($('#comment').val() == '')
	{
		$('#comment').parent().after('<div class="field extrafield"><div class="alert alert-error"> <i class="icon-warning-sign"></i> You must make an entry.</div></div>');
		errors++;
	}

	if ($('input[name="suitableForUCL"]:checked').length == 0)
	{
		$('#field-issuitableucl').append('<div class="alert alert-error"> <i class="icon-warning-sign"></i> You must make a selection.</div>');
		errors++;
	}

	if ($('input[name="suitableForProgramme"]:checked').length == 0)
	{
		$('#field-issuitableprog').append('<div class="alert alert-error"> <i class="icon-warning-sign"></i> You must make a selection.</div>');
		errors++;
	}
	
	return (errors == 0);
}