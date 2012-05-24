$(document).ready(function(){
	$("#acceptTermsDDValue").val("NO");
	
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
	
	$("input[name*='acceptTermsDDCB']").click(function() {
		if ($("#acceptTermsDDValue").val() =='YES'){
			$("#acceptTermsDDValue").val("NO");
		} else {	
			$("#acceptTermsDDValue").val("YES");
			$(".terms-box").attr('style','');
			$.post("/pgadmissions/acceptTerms", {  
				applicationId: $("#applicationId").val(), 
				acceptedTerms: $("#acceptTermsDDValue").val()
			},
			function(data) {
			});
		}
		});
	
	$('#documentsSaveButton').click(function(){
		if( $("#acceptTermsDDValue").val() =='NO')
		{ 
			// Highlight the information bar and terms box.
			var $form = $('#documentSection form');
			$('.terms-box, .section-info-bar', $form).css({ borderColor: 'red', color: 'red' });
		}
		else
		{
			$("span[name='nonAcceptedDD']").html('');
			postDocumentData('close');
		}
	});
	addToolTips();

	$('#cvUploadFields').on('change','#cvDocument', function(event){
		if(this.files[0].size < 10485760){
			cvDelete();
			$('#cvDocumentProgress').html("uploading file...");
			$('#cvDocument').attr("readonly", "readonly");
			cvUpload();
			$('#cvDocument').removeAttr("readonly");
		 }else{
			 alert("Sorry, document must be at most 10MB.");
		 }
	});
	
	$('#psUploadFields').on('change','#psDocument', function(event){	
		if(this.files[0].size < 10485760){
			psDelete();
			$('#psDocumentProgress').html("uploading file...");
			$('#psDocument').attr("readonly", "readonly");
			psUpload();
			$('#psDocument').removeAttr("readonly");
		}else{
			 alert("Sorry, document must be at most 10MB.");
		 }
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
