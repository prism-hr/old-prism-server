$(document).ready(function(){

	$('#referenceUploadFields').on('change','#referenceDocument', function(event){	
		referenceDelete();
		$('#referenceDocumentProgress').html("uploading file...");
		$('#referenceDocument').attr("readonly", "readonly");
		referenceUpload();
		$('#referenceDocument').removeAttr("readonly");
	});
	
	$('#declineReference').click(function(){
		if(confirm("Decline to at as a referee. Are you sure?")){
			$('#declineForm').submit();
		}
	});
	
});
function referenceDelete(){
	
	if($('#document_REFERENCE') && $('#document_REFERENCE').val() && $('#document_REFERENCE').val() != ''){
		$.post("/pgadmissions/delete/asyncdelete",
			{
				documentId: $('#document_REFERENCE').val()
				
			}				
		);

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
				
			}
		}
	);
}