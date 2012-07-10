$(document).ready(function(){

	$('#uploadFields').on('change','#commentDocument', function(event)
	{	
		$('#uploadFields span.invalid').remove();
		
		if (this.files[0].size < 10485760)
		{
			//$('#commentDocumentProgress').html("uploading file...");
			$('#commentDocument').attr("readonly", "readonly");
			commentDocumentUpload();
			$('#commentDocument').removeAttr("readonly");
		}
		else
		{
			$('#uploadFields').append('<span class="invalid">Document must be at most 10MB.</span>');
		}
	});

	$('#commentUploadedDocument').on('click', 'span[name="supportingDocumentSpan"] a[name="delete"]', commentDocumentDelete);
	
	
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

function commentDocumentDelete()
{
	var id = $(this).attr("id");
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
			  }
		  },
		 url:"/pgadmissions/delete/asyncdelete",
		data:{
			documentId: id
		},
		success: function(data) {			
			$('#' +id).parent().remove()
		}
	});

	
}

function commentDocumentUpload()
{	
	
	/*$("#commentDocumentProgress").ajaxStart(function()
	{
		$(this).show();
	})
	.ajaxComplete(function(){
		$(this).hide();
		$('#commentDocumentProgress').html("");
		$('#commentDocument').val("");
		
	});*/

	$('#uploadFields').removeClass('uploaded').addClass('posting');
	$.ajaxFileUpload
	(
		{
			url:'/pgadmissions/documents/async',
			secureuri:false,
			
			fileElementId:'commentDocument',	
			dataType:'text',
			data:{type:'COMMENT'},
			success: function(data)
			{
				var invalid = data.indexOf('<span class="invalid');
				if (invalid >= 0)
				{
					// Display error message.
					var msg = data.substr(invalid);
					$('#uploadFields').append(msg);
				}
				else
				{
					// Display the uploaded file.
					$('#commentUploadedDocument').append(data);
					$('#uploadFields').addClass('uploaded');
					$('#commentUploadedDocument').show();
				}
			},
			complete: function()
			{
				$('#commentDocument').val("");
				$('#uploadFields').removeClass('posting');
			}
		}
	);
}

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