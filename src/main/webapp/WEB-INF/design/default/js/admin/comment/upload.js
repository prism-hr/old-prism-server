$(document).ready(function(){

	$('#uploadFields').on('change','#commentDocument', function(event)
	{	
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

function commentDocumentDelete(){
	 var id = $(this).attr("id");
	$.post("/pgadmissions/delete/asyncdelete",
		{
			documentId: id
		},
		function(data) {			
			$('#' +id).parent().remove()
		}
	);

	
}

function commentDocumentUpload()
{	
	
	$("#commentDocumentProgress").ajaxStart(function(){
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
			success: function (data)
			{		
				$('#commentUploadedDocument').append(data);
				$('#uploadFields').addClass('uploaded');
				$('#commentUploadedDocument').show();
				
			}
		}
	);
}