$(document).ready(function(){

	$('#referenceUploadFields').on('change','#referenceDocument', function(event)
	{	
		$('#referenceUploadFields span.invalid').remove();
		
		if (this.files[0].size < 10485760)
		{
			referenceDelete();
			$('#referenceDocumentProgress').html("uploading file...");
			$('#referenceDocument').attr("readonly", "readonly");
			referenceUpload();
			$('#referenceDocument').removeAttr("readonly");
		}
		else
		{
			$('#referenceUploadFields').append('<span class="invalid">Document must be at most 10MB.</span>');
		}
	});
	
	$('#declineReference').click(function(){
		if(confirm("Decline to at as a referee. Are you sure?")){
			$('#declineForm').submit();
		}
	});
	
});
function referenceDelete(){
	
	if($('#document_REFERENCE') && $('#document_REFERENCE').val() && $('#document_REFERENCE').val() != ''){
		$.ajax({
			type: 'POST',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
			  url:"/pgadmissions/delete/asyncdelete",
			data:{
				documentId: $('#document_REFERENCE').val()				
			}				
		});

	}
}

function referenceUpload()
{	
	
	$("#referenceDocumentProgress").ajaxStart(function(){
		$(this).show();
	})
	.ajaxComplete(function(){
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