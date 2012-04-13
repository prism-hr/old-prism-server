$(document).ready(function(){

	$('#documentsCloseButton').click(function(){
		$('#documents-H2').trigger('click');
		return false;
	});
	
	$('#documentsCancelButton').click(function(){
		$.get("/pgadmissions/update/getDocuments",
				{
					applicationId:  $('#applicationId').val(),
					message: 'cancel'
				},
				function(data) {
					$('#documentSection').html(data);
				}
		);
	});
	
	$('#documentsSaveButton').click(function(){
		postDocumentData('close');
	});
	addToolTips();

	//open/close
	var $header  =$('#documents-H2');
	var $content = $header.next('div');
	$header.bind('click', function()
	{
	  $content.toggle();
	  $(this).toggleClass('open', $content.is(':visible'));
	  return false;
	});
	
	$('#cvUploadFields').on('change','#cvDocument', function(event){	
		cvDelete();
		$('#cvDocumentProgress').html("uploading file...");
		$('#cvDocument').attr("readonly", "readonly");
		cvUpload();
		$('#cvDocument').removeAttr("readonly");
	});
	
	$('#psUploadFields').on('change','#psDocument', function(event){	
		psDelete();
		$('#psDocumentProgress').html("uploading file...");
		$('#psDocument').attr("readonly", "readonly");
		psUpload();
		$('#psDocument').removeAttr("readonly");
	});
	
	
});


function postDocumentData(message){
	
	$.post("/pgadmissions/update/editDocuments",
		{ 	
			applicationId:  $('#applicationId').val(),	
			cv: $('#document_CV').val(),
			personalStatement: $('#document_PERSONAL_STATEMENT').val(),
			message:message
		},
		function(data) {
			$('#documentSection').html(data);
		}
	);
}



function cvDelete(){
	
	if($('#document_CV') && $('#document_CV').val() && $('#document_CV').val() != ''){
		$.post("/pgadmissions/delete/asyncdelete",
			{
				documentId: $('#document_CV').val()
				
			}				
		);

	}
}

function cvUpload()
{	
	
	$("#cvDocumentProgress").ajaxStart(function(){
		$(this).show();
	})
	.ajaxComplete(function(){
		$(this).hide();
		$('#cvDocumentProgress').html("");
		
	});

	$.ajaxFileUpload
	(
		{
			url:'/pgadmissions/documents/async',
			secureuri:false,
			
			fileElementId:'cvDocument',	
			dataType:'text',
			data:{type:'CV'},
			success: function (data)
			{		
				$('#cvUploadedDocument').html(data);
				$('#cvUploadedDocument').show();
				
			}
		}
	)
}


function psDelete(){
	
	if($('#document_PERSONAL_STATEMENT') && $('#document_PERSONAL_STATEMENT').val() && $('#document_PERSONAL_STATEMENT').val() != ''){
		$.post("/pgadmissions/delete/asyncdelete",
			{
				documentId: $('#document_PERSONAL_STATEMENT').val()
				
			}				
		);

	}
}

function psUpload()
{	
	
	$("#psDocumentProgress").ajaxStart(function(){
		$(this).show();
	})
	.ajaxComplete(function(){
		$(this).hide();
		$('#psDocumentProgress').html("");
		
	});

	$.ajaxFileUpload
	(
		{
			url:'/pgadmissions/documents/async',
			secureuri:false,
			
			fileElementId:'psDocument',	
			dataType:'text',
			data:{type:'PERSONAL_STATEMENT'},
			success: function (data)
			{		
				$('#psUploadedDocument').html(data);
				$('#psUploadedDocument').show();
				
			}
		}
	)
}
