$(document).ready(function(){

	$('#uploadFields').on('change','#commentDocument', function(event)
	{	
		$('#uploadFields span.invalid').remove();
		
		if (this.files[0].size < 10485760)
		{
			$('#commentDocumentProgress').html("uploading file...");
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
	
});

function commentDocumentDelete()
{
	var id = $(this).attr("id");
	$.ajax({
		type: 'POST',
		 statusCode: {
			  401: function() {
				  window.location.reload();
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
	
	$("#commentDocumentProgress").ajaxStart(function()
	{
		$(this).show();
	})
	.ajaxComplete(function(){
		$(this).hide();
		$('#commentDocumentProgress').html("");
		$('#commentDocument').val("");
		
	});

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
				var $invalid = $(data).children('span.invalid');
				if ($invalid.length > 0)
				{
					// Display error message.
					$('#uploadFields').append($invalid);
				}
				else
				{
					// Display the uploaded file.
					$('#commentUploadedDocument').append(data);
					$('#uploadFields').addClass('uploaded');
					$('#commentUploadedDocument').show();
				}
			}
		}
	);
}