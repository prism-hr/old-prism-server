$(document).ready(function(){
	
    addCounter();

	// -------------------------------------------------------------------------------
	// Prevent non-numerical input for funding value field.
	// -------------------------------------------------------------------------------	
	$('#fundingValue').keydown(function(event)
	{
		// http://stackoverflow.com/questions/995183/how-to-allow-only-numeric-0-9-in-html-inputbox-using-jquery
		// Allow: backspace, delete, tab, escape, and enter
		if ( event.keyCode == 46 || event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 27 || event.keyCode == 13 || 
		// Allow: Ctrl+A
		(event.keyCode == 65 && event.ctrlKey === true) || 
		// Allow: home, end, left, right
		(event.keyCode >= 35 && event.keyCode <= 39))
		{
			// let it happen, don't do anything
			return;
		}
		else
		{
			// Ensure that it is a number and stop the keypress
			if (event.shiftKey || (event.keyCode < 48 || event.keyCode > 57) && (event.keyCode < 96 || event.keyCode > 105 ))
			{
				event.preventDefault(); 
			}   
		}
	});
	
	$("#acceptTermsFDValue").val("NO");

	// -------------------------------------------------------------------------------
	// Close button.
	// -------------------------------------------------------------------------------
	$('#fundingCloseButton').click(function()
	{
		$('#funding-H2').trigger('click');
		return false;
	});
	
	
	// -------------------------------------------------------------------------------
	// Delete existing funding.
	// -------------------------------------------------------------------------------
	$('a[name="deleteFundingButton"]').click( function(){	
		var id = $(this).attr("id").replace("funding_", "");
		$('#ajaxloader').show();
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
					  },				  
					  403: function() {
						  window.location.href = "/pgadmissions/404";
					  }
				},
			url:"/pgadmissions/deleteentity/funding",
			data:	{
					id: id	
				}, 
				
			success:	function(data) {
					$('#fundingSection').html(data);
					$('#funding-H2').trigger('click');
				},
			complete: function()
			{
				$('#ajaxloader').fadeOut('fast');
			}
		});
	});
	

	// -------------------------------------------------------------------------------
	// "Accept terms" checkbox.
	// -------------------------------------------------------------------------------
	$("input[name*='acceptTermsFDCB']").click(function()
	{
		if ($("#acceptTermsFDValue").val() =='YES')
		{
			$("#acceptTermsFDValue").val("NO");
		}
		else
		{	
			$("#acceptTermsFDValue").val("YES");
			fundImgCount = 0;
		}
		});
	
	// -------------------------------------------------------------------------------
	// Add funding button.
	// -------------------------------------------------------------------------------
	$('#addFundingButton').click(function()
	{
		$("span[name='nonAcceptedFD']").html('');
		postFundingData('add');
	});
	
	
	// -------------------------------------------------------------------------------
	// Save button.
	// -------------------------------------------------------------------------------
	$('#fundingSaveCloseButton').click(function()
	{
		
			$("span[name='nonAcceptedFD']").html('');

			// Check for a "dirty" employment position form. If there is data try to submit it.
			if (!isFormEmpty('#fundingSection'))
			{
				postFundingData('close');
			}
			else
			{
				unmarkSection('#fundingSection');
				$('#fundingCloseButton').trigger('click');
			}
	});
	
	
	// -------------------------------------------------------------------------------
	// Edit existing funding details.
	// -------------------------------------------------------------------------------
	$('a[name="editFundingLink"]').click(function()
	{
		var id = this.id;
		id = id.replace('funding_', '');	
		$('#ajaxloader').show();
		$.ajax({
			type: 'GET',
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
				  },				  
				  403: function() {
					  window.location.href = "/pgadmissions/404";
				  }
			},
			url:"/pgadmissions/update/getFunding",
			data:{
				applicationId:  $('#applicationId').val(),
				fundingId: id,
				message: 'edit',					
				cacheBreaker: new Date().getTime()
			},
			success:function(data)
			{
				$('#fundingSection').html(data);
				$('#addFundingButton').html('Update');
			},
			complete: function()
			{
				$('#funding-H2').trigger('click');
				$('#ajaxloader').fadeOut('fast');
			}
		});
	});
	

	// -------------------------------------------------------------------------------
	// Clear button.
	// -------------------------------------------------------------------------------
	$('#fundingClearButton').click(function()
	{
		$('#ajaxloader').show();
		loadFundingSection(true);
	});
	
	bindDatePicker('#fundingAwardDate');
	addToolTips();

  // Generic file upload solution...
	watchUpload($('#fundingDocument'));

});


function postFundingData(message)
{
	$('#ajaxloader').show();
	var acceptedTheTerms;
	if ($("#acceptTermsFDValue").val() == 'NO'){
		acceptedTheTerms = false;
	}
	else{
		acceptedTheTerms = true;
	}
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
			  },				  
			  403: function() {
				  window.location.href = "/pgadmissions/404";
			  }
		  },
		url:"/pgadmissions/update/editFunding",
		data:{  
			description: $("#fundingDescription").val(),
			value: $("#fundingValue").val(),
			awardDate: $("#fundingAwardDate").val(),
			type: $("#fundingType").val(),
			fundingId: $("#fundingId").val(),
			applicationId:  $('#applicationId').val(),
			application:  $('#applicationId').val(),	
			document: $('#document_SUPPORTING_FUNDING').val(),
			message:message,
			acceptedTerms: acceptedTheTerms
		},
		success: function(data)
		{
			$('#fundingSection').html(data);
			var errorCount = $('#fundingSection .alert-error').length;
			if(errorCount > 0){
				$('#funding-H2').trigger('click');
				markSectionError('#fundingSection');
			} else {
				if (message == 'add') {
					$('#funding-H2').trigger('click');
				}
			}
		},
    complete: function()
    {
			$('#ajaxloader').fadeOut('fast');
    }
	});
}


function fundingDocumentDelete(){
	
	if ($('#document_SUPPORTING_FUNDING') && $('#document_SUPPORTING_FUNDING').val() && $('#document_SUPPORTING_FUNDING').val() != '') {
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
				  },				  
				  403: function() {
					  window.location.href = "/pgadmissions/404";
				  }
			  },
			url:"/pgadmissions/delete/asyncdelete",
			data:{
				documentId: $('#document_SUPPORTING_FUNDING').val()
			}				
		});
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
	);

}