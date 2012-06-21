$(document).ready(function(){
	$("#acceptTermsDDValue").val("NO");
	
	var addImgCount = 0;
	
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
			
			/*
			$(".terms-box").attr('style','');
			$("#doc-info-bar-div").switchClass("section-error-bar", "section-info-bar", 1);
			$("#doc-info-bar-span").switchClass("invalid-info-text", "info-text", 1);
			$("#doc-info-bar-div .row span.error-hint").remove();
			*/
			addImgCount = 0;
			
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
//			var $form = $('#documentSection form');
//			$('.terms-box, .section-info-bar', $form).css({ borderColor: 'red', color: 'red' });
			
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			$("#doc-info-bar-div").switchClass("section-info-bar", "section-error-bar", 1);
			$("#doc-info-bar-span").switchClass("info-text", "invalid-info-text", 1);
			if(addImgCount == 0){
				$("#doc-info-bar-div .row").prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
				addImgCount = addImgCount + 1;
			}
			addToolTips();
			
		}
		else
		{
			$("span[name='nonAcceptedDD']").html('');
			postDocumentData('close');
		}
	});
	addToolTips();

  // Generic file upload solution...
	watchUpload($('#cvDocument'));
	watchUpload($('#psDocument'));

});


function postDocumentData(message)
{

	$('#documentSection > div').append('<div class="ajax" />');
	
	$.post("/pgadmissions/update/editDocuments",
		{ 	
			applicationId:  $('#applicationId').val(),	
			cv: $('#document_CV').val(),
			personalStatement: $('#document_PERSONAL_STATEMENT').val(),
			message:message
		},
		function(data)
		{
			$('#documentSection').html(data);
			$('#documentSection div.ajax').remove();
			markSectionError('#documentSection');

			if (message == 'close')
			{
				// Close the section only if there are no errors.
				var errorCount = $('#documentSection .invalid:visible').length;
				if (errorCount == 0)
				{
					$('#documents-H2').trigger('click');
				}
			}
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
