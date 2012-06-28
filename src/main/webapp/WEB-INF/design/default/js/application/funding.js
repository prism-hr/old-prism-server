$(document).ready(function(){
	
	var fundImgCount = 0;

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
	limitTextArea();
	

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
			$.ajax({
				type: 'POST',
				 statusCode: {
					  401: function() {
						  window.location.reload();
					  }
				  },
				url:"/pgadmissions/deleteentity/funding",
				data:	{
						id: id	
					}, 
					
				success:	function(data) {
						$('#fundingSection').html(data);
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
			
			$.ajax({
				type: 'POST',
				 statusCode: {
					  401: function() {
						  window.location.reload();
					  }
				  },
				url:"/pgadmissions/acceptTerms",
				data:{  
					applicationId: $("#applicationId").val(), 
					acceptedTerms: $("#acceptTermsFDValue").val()
				},
				success:function(data) {
				}
			});
		}
		});
	

	// -------------------------------------------------------------------------------
	// Add funding button.
	// -------------------------------------------------------------------------------
	$('#addFundingButton').click(function()
	{
		if ($('#acceptTermsFDValue').length != 0 && $("#acceptTermsFDValue").val() =='NO')
		{ 
			// Highlight the information bar and terms box.
			markSectionError('#fundingSection');
		}
		else
		{
			$("span[name='nonAcceptedFD']").html('');
			postFundingData('add');
		}
	});
	
	
	// -------------------------------------------------------------------------------
	// Save button.
	// -------------------------------------------------------------------------------
	$('#fundingSaveCloseButton').click(function()
	{
		if ($("#acceptTermsFDValue").val() =='NO')
		{ 
			// Highlight the information bar and terms box.
			markSectionError('#fundingSection');
		}
		else
		{
			$("span[name='nonAcceptedFD']").html('');

			// Check for a "dirty" employment position form. If there is data try to submit it.
			if (!isFormEmpty('#fundingSection'))
			{
				postFundingData('close');
			}
			else
			{
				$('#fundingCloseButton').trigger('click');
			}
		}
	});
	
	
	// -------------------------------------------------------------------------------
	// Edit existing funding details.
	// -------------------------------------------------------------------------------
	$('a[name="editFundingLink"]').click(function()
	{
		var id = this.id;
		id = id.replace('funding_', '');	
		$.ajax({
			 	type: 'GET',
			 	statusCode: {
			 		401: function() {
			 			window.location.reload();
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
				}
		});
	});
	

	// -------------------------------------------------------------------------------
	// Clear button.
	// -------------------------------------------------------------------------------
	$('a[name="fundingCancelButton"]').click(function()
	{
		$.ajax({
				type: 'GET',
				statusCode: {
					401: function() {
						window.location.reload();
					}
				},
				url:"/pgadmissions/update/getFunding",
				data:{
					applicationId:  $('#applicationId').val(),
					message: 'cancel',					
					cacheBreaker: new Date().getTime()
				},
				success: function(data)
				{
					$('#fundingSection').html(data);
				}
		});
	});
	
	bindDatePicker('#fundingAwardDate');
	addToolTips();

  // Generic file upload solution...
	watchUpload($('#fundingDocument'));

});


function postFundingData(message)
{
	$('#fundingSection > div').append('<div class="ajax" />');

	$.ajax({
		type: 'POST',
		 statusCode: {
			  401: function() {
				  window.location.reload();
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
			message:message
		},
		success: function(data)
		{
			$('#fundingSection').html(data);
			$('#fundingSection div.ajax').remove();
			markSectionError('#fundingSection');
	
			if (message == 'close')
			{
				// Close the section only if there are no errors.
				var errorCount = $('#fundingSection .invalid:visible').length;
				if (errorCount == 0)
				{
					$('#funding-H2').trigger('click');
				}
			}
		}
	});
}


function fundingDocumentDelete(){
	
	if($('#document_SUPPORTING_FUNDING') && $('#document_SUPPORTING_FUNDING').val() && $('#document_SUPPORTING_FUNDING').val() != ''){
		$.ajax({
			type: 'POST',
			 statusCode: {
				  401: function() {
					  window.location.reload();
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
	)

}