$(document).ready(function()
{
	$("#acceptTermsDDValue").val("NO");
	
	var addImgCount = 0;
	
	// --------------------------------------------------------------------------------
	// Close button
	// --------------------------------------------------------------------------------
	$('#documentsCloseButton').click(function()
	{
		$('#documents-H2').trigger('click');
		return false;
	});
	
	// --------------------------------------------------------------------------------
	// Clear button
	// --------------------------------------------------------------------------------

	$('#documentsClearButton').click(function()
	{
		if ($('#psUploadedDocument').children().length > 0) {
			$('#psUploadedDocument').find('.delete').trigger('click');
		}
		if ($('#cvUploadedDocument').children().length > 0) {
			$('#cvUploadedDocument').find('.delete').trigger('click');
		}
		
	});
	

	// --------------------------------------------------------------------------------
	// 
	// --------------------------------------------------------------------------------
	$("input[name*='acceptTermsDDCB']").click(function()
	{
		if ($("#acceptTermsDDValue").val() == 'YES')
		{
			$("#acceptTermsDDValue").val("NO");
		}
		else
		{	
			$("#acceptTermsDDValue").val("YES");
			
			addImgCount = 0;
			
			
		}
	});
	
	// --------------------------------------------------------------------------------
	// Save button
	// --------------------------------------------------------------------------------
	$('#documentsSaveButton').click(function()
	{
		/*if ($("#acceptTermsDDValue").val() == 'NO')
		{ 
			// Highlight the information bar and terms box.
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			var $infobar = $('#doc-info-bar-div.section-info-bar');
			$infobar.switchClass("section-info-bar", "section-error-bar", 1);
			if ($infobar)
			{
				$infobar.prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
				addImgCount = addImgCount + 1;
			}
			addToolTips();
		}
		else
		{*/
			$("span[name='nonAcceptedDD']").html('');
			postDocumentData('close');
		//}
	});
	addToolTips();

  // Generic file upload solution...
	watchUpload($('#cvDocument'), cvDelete);
	watchUpload($('#psDocument'), psDelete);

});


// --------------------------------------------------------------------------------
// Posting document data.
// --------------------------------------------------------------------------------
function postDocumentData(message)
{
	$('#ajaxloader').show();
	var acceptedTheTerms;
	if ($("#acceptTermsDDValue").val() == 'NO'){
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
		url:"/pgadmissions/update/editDocuments",
		data:{ 	
			applicationId:  $('#applicationId').val(),	
			cv: $('#document_CV').val(),
			personalStatement: $('#document_PERSONAL_STATEMENT').val(),
			message: message,
			acceptedTerms: acceptedTheTerms
		},
		success: function(data)
		{
			$('#documentSection').html(data);
		

			if (message == 'close')
			{
				// Close the section only if there are no errors.
				var errorCount = $('#documentSection .alert-error').length;
				if (errorCount > 0)
				{
					$('#documents-H2').trigger('click');
					markSectionError('#documentSection');
				}
			}
		},
		complete: function(){
			$('#ajaxloader').fadeOut('fast');
		}
	});
}

// --------------------------------------------------------------------------------
// 
// --------------------------------------------------------------------------------
function cvDelete()
{
	if ($('#document_CV') && $('#document_CV').val() && $('#document_CV').val() != '')
	{
		$.ajax({
			type: 'POST',
			 statusCode: {
				  401: function()
					{
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
			url:"/pgadmissions/delete/deleteCV",
			data: {
				application: $('#applicationId').val()
			},
			success: function(data){
				$('#cvLink').remove();
				$('#deleteCv').remove();
			}
		});
	}
}

// --------------------------------------------------------------------------------
// 
// --------------------------------------------------------------------------------
function cvUpload()
{	
	$("#cvDocumentProgress").ajaxStart(function(){
		$(this).show();
	})
	.ajaxComplete(function()
	{
		$(this).hide();
		$('#cvDocumentProgress').html("");
	});

	$.ajaxFileUpload({
		url: '/pgadmissions/documents/async',
		secureuri: false,
		
		fileElementId: 'cvDocument',	
		dataType: 'text',
		data: { type: 'CV' },
		success: function (data)
		{		
			$('#cvUploadedDocument').html(data);
			$('#cvUploadedDocument').show();
		}
	});
}

// --------------------------------------------------------------------------------
// 
// --------------------------------------------------------------------------------
function psDelete()
{
	if ($('#document_PERSONAL_STATEMENT') && $('#document_PERSONAL_STATEMENT').val() && $('#document_PERSONAL_STATEMENT').val() != '')
	{
		$.ajax({
			type: 'POST',
			 statusCode: {
				  401: function()
					{
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
			url: "/pgadmissions/delete/deletePersonalStatement",
			data: {
				application: $('#applicationId').val()
			},
			success: function(data){
				$('#psLink').remove();
				$('#deletePs').remove();
			}
		});
	}
}

// --------------------------------------------------------------------------------
// 
// --------------------------------------------------------------------------------
function psUpload()
{	
	$("#psDocumentProgress").ajaxStart(function()
	{
		$(this).show();
	})
	.ajaxComplete(function()
	{
		$(this).hide();
		$('#psDocumentProgress').html("");
	});

	$.ajaxFileUpload({
		url: '/pgadmissions/documents/async',
		secureuri: false,
		
		fileElementId: 'psDocument',	
		dataType: 'text',
		data: { type:'PERSONAL_STATEMENT' },
		success: function(data)
		{		
			$('#psUploadedDocument').html(data);
			$('#psUploadedDocument').show();
		}
	});
}
