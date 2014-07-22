$(document).ready(function()
{

	$('#referenceSaveButton').click(function()
	{
		/*if (!validateReference())
		{
			return false;
		}*/
		
		var message = 'Please confirm that you are satisfied with your comments. <b>You will not be able to change them.</b>';
		var onOk    = function()
		{
			$('#documentUploadForm').submit();
		};
		var onCancel = function(){};
		
		modalPrompt(message, onOk, onCancel);
		return false;
	});
	

	$('#referenceUploadFields').on('change','#referenceDocument', function(event)
	{	
		$('#referenceUploadFields div.alert-error').remove();
		
		if (this.files[0].size < 10485760)
		{
			$('#referenceDocumentProgress').html("uploading file...");
			$('#referenceDocument').attr("readonly", "readonly");
			referenceUpload();
			$('#referenceDocument').removeAttr("readonly");
		}
		else
		{
			$('#referenceUploadFields').append('<div class="alert alert-error"> <i class="icon-warning-sign"></i>Document must be at most 10MB.</div>');
		}
	});
	
	$('#declineReference').click(function()
	{
		var message = "Decline to act as a referee. Are you sure?";
		var onOk    = function() { $('#declineForm').submit(); };
		var onCancel = function() { };
		
		modalPrompt(message, onOk, onCancel);
		return false;
	});
	
});

function referenceUpload()
{	
	
	$("#referenceDocumentProgress").ajaxStart(function()
	{
		$(this).show();
	})
	.ajaxComplete(function()
	{
		$(this).hide();
		$('#referenceDocumentProgress').html("");
		
	});

	$.ajaxFileUpload
	(
		{
			url:'/pgadmissions/documents/async',
			secureuri:false,
			
			fileElementId:'referenceDocument',	
			dataType:'text',
			data:{type:'REFERENCE'},
			success: function (data)
			{		
				$('#referenceUploadedDocument').html(data);
				$('#referenceUploadedDocument').show();
				addToolTips();
			}
		}
	);
}

function validateReference()
{
	var errors = 0;
	$('#reviewForm div.alert-error').remove();
	
	if ($('#comment').val() == '')
	{
		$('#comment').after('<div class="alert alert-error"> <i class="icon-warning-sign"> You must make an entry.</div>');
		errors++;
	}

	if ($('input[name="suitableForUCL"]:checked').length == 0)
	{
		$('#field-issuitableucl').append('<div class="alert alert-error"> <i class="icon-warning-sign"> You must make a selection.</div>');
		errors++;
	}

	if ($('input[name="suitableForProgramme"]:checked').length == 0)
	{
		$('#field-issuitableprog').append('<div class="alert alert-error"> <i class="icon-warning-sign"> You must make a selection.</div>');
		errors++;
	}
	
	return (errors == 0);
}