$(document).ready(function(){
	
	$("#acceptTermsFDValue").val("NO");
	limitTextArea();
	
	$('#fundingCloseButton').click(function(){
		$('#funding-H2').trigger('click');
		return false;
	});
	
	
	$('a[name="deleteFundingButton"]').click( function(){	
			var id = $(this).attr("id").replace("funding_", "");
			$.post("/pgadmissions/deleteentity/funding",
					{
						id: id	
					}, 
					
					function(data) {
						$('#fundingSection').html(data);
					}	
					
				);
	});
	
	$("input[name*='acceptTermsFDCB']").click(function() {
		if ($("#acceptTermsFDValue").val() =='YES'){
			$("#acceptTermsFDValue").val("NO");
		} else {	
			$("#acceptTermsFDValue").val("YES");
			
			$(".terms-box").attr('style','');
			$("#fund-info-bar-div").switchClass("section-error-bar", "section-info-bar", 1);
			$("#fund-info-bar-span").switchClass("invalid-info-text", "info-text", 1);
			$("#fund-info-bar-div .row span.error-hint").remove();
			
			$.post("/pgadmissions/acceptTerms", {  
				applicationId: $("#applicationId").val(), 
				acceptedTerms: $("#acceptTermsFDValue").val()
			},
			function(data) {
			});
		}
		});
	
	$('#addFundingButton').click(function()
	{
		if ($('#acceptTermsFDValue').length != 0 && $("#acceptTermsFDValue").val() =='NO')
		{ 
			// Highlight the information bar and terms box.
//			var $form = $('#fundingSection form');
//			$('.terms-box, .section-info-bar', $form).css({ borderColor: 'red', color: 'red' });
			
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			$("#fund-info-bar-div").switchClass("section-info-bar", "section-error-bar", 1);
			$("#fund-info-bar-span").switchClass("info-text", "invalid-info-text", 1);
			$("#fund-info-bar-div .row").prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
			addToolTips();
			
		}
		else
		{
			$("span[name='nonAcceptedFD']").html('');
			postFundingData('add');
		}
	});
	
	$('#fundingSaveCloseButton').click(function(){
		if ($("#acceptTermsFDValue").val() =='NO')
		{ 
			// Highlight the information bar and terms box.
//			var $form = $('#personalDetailsSection form');
//			$('.terms-box, .section-info-bar', $form).css({ borderColor: 'red', color: 'red' });
			
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			$("#fund-info-bar-div").switchClass("section-info-bar", "section-error-bar", 1);
			$("#fund-info-bar-span").switchClass("info-text", "invalid-info-text", 1);
			$("#fund-info-bar-div .row").prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
			addToolTips();
			
		}
		else{
			$("span[name='nonAcceptedFD']").html('');
			postFundingData('close');
		}
	});
	
	$('a[name="editFundingLink"]').click(function(){
		var id = this.id;
		id = id.replace('funding_', '');	
		$.get("/pgadmissions/update/getFunding",
				{
					applicationId:  $('#applicationId').val(),
					fundingId: id,
					message: 'edit',					
					cacheBreaker: new Date().getTime()
				},
				function(data) {
					$('#fundingSection').html(data);
				}
		);
	});
	
	$('a[name="fundingCancelButton"]').click(function(){
		$.get("/pgadmissions/update/getFunding",
				{
					applicationId:  $('#applicationId').val(),
					message: 'cancel',					
					cacheBreaker: new Date().getTime()
				},
				function(data) {
					$('#fundingSection').html(data);
				}
		);
	});
	
	bindDatePicker('#fundingAwardDate');
	addToolTips();
		
	$('#fundingUploadFields').on('change','#fundingDocument', function(event){	
		if(this.files[0].size < 10485760){
			fundingDocumentDelete();
			$('#fundingDocumentProgress').html("uploading file...");
			$('#fundingDocument').attr("readonly", "readonly");
			fundingDocumentUpload();
			$('#fundingDocument').removeAttr("readonly");
		}else{
			 alert("Sorry, document must be at most 10MB.");
		 }
	});
	
		
});


function postFundingData(message){

	$.post("/pgadmissions/update/editFunding", {  
		description: $("#fundingDescription").val(),
		value: $("#fundingValue").val(),
		awardDate: $("#fundingAwardDate").val(),
		type: $("#fundingType").val(),
		fundingId: $("#fundingId").val(),
		applicationId:  $('#applicationId').val(),
		application:  $('#applicationId').val(),	
		document: $('#document_SUPPORTING_FUNDING').val(),
		message:message
	},
	function(data) {
		$('#fundingSection').html(data);
	});
}


function fundingDocumentDelete(){
	
	if($('#document_SUPPORTING_FUNDING') && $('#document_SUPPORTING_FUNDING').val() && $('#document_SUPPORTING_FUNDING').val() != ''){
		$.post("/pgadmissions/delete/asyncdelete",
			{
				documentId: $('#document_SUPPORTING_FUNDING').val()
				
			}				
		);

	}
}

function fundingDocumentUpload()
{	
	
	$("#fundingDocumentProgress").ajaxStart(function(){
		$(this).show();
	})
	.ajaxComplete(function(){
		$(this).hide();
		$('#fundingDocumentProgress').html("");
		
	});

	$.ajaxFileUpload
	(
		{
			url:'/pgadmissions/documents/async',
			secureuri:false,
			
			fileElementId:'fundingDocument',	
			dataType:'text',
			data:{type:'SUPPORTING_FUNDING'},
			success: function (data)
			{		
				$('#fundingUploadedDocument').html(data);
				$('#fundingUploadedDocument').show();
				
			}
		}
	)

}