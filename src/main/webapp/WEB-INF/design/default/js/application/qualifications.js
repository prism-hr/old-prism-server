$(document).ready(function(){
	
	var qualImgCount = 0;
	
	$("#acceptTermsQDValue").val("NO");
	
	// -------------------------------------------------------------------------------
	// Close button.
	// -------------------------------------------------------------------------------
	$('#qualificationsCloseButton').click(function(){
		$('#qualifications-H2').trigger('click');
		return false;
	});
	

	// -------------------------------------------------------------------------------
	// Checkbox to mark the qualification as current.
	// -------------------------------------------------------------------------------
	$("input[name*='currentQualificationCB']").click(function()
	{
		if ($("#currentQualification").val() =='YES')
		{
			// Uncheck the box
			$("#currentQualification").val("NO");
			$("#qualificationAwardDate").val("");
			$("#qualificationAwardDate").attr("disabled", "disabled");
			$("#proofOfAward").val("");
			$("#proofOfAward").attr("disabled", "disabled");
			$("#quali-grad-id em").remove();
			$("#quali-grad-id").text("Expected Grade / Result / GPA").append('<em>*</em>');
			$("#quali-award-date-lb").text("Award Date").addClass("grey-label");
			$("#quali-proof-of-award-lb").text("Proof of award (PDF)").addClass("grey-label");
			
			if ($('#uploadFields').hasClass('uploaded'))
			{
				$('#uploadFields').addClass('upload-delete');
				$('#uploadFields a.button-edit').hide();
			}
		}
		else
		{		
			// Check the box
			$("#currentQualification").val("YES");
			$("#qualificationAwardDate").removeAttr("disabled", "disabled");	
			$("#proofOfAward").removeAttr("disabled");
			$("#quali-grad-id em").remove();
			$("#quali-grad-id").text("Grade / Result / GPA").append('<em>*</em>');
			$("#quali-award-date-lb em").remove();
			$("#quali-award-date-lb").append('<em>*</em>').removeClass("grey-label");
			$("#quali-proof-of-award-lb").removeClass("grey-label");

			if ($('#uploadFields').hasClass('uploaded'))
			{
				$('#uploadFields').removeClass('upload-delete');
				$('#uploadFields a.button-edit').show();
			}
		}
		
	
	});

	
	// -------------------------------------------------------------------------------
	// Delete a qualification.
	// -------------------------------------------------------------------------------
	$('a[name="deleteQualificationButton"]').click(function()
	{

			var id = $(this).attr("id").replace("qualification_", "");
			$.ajax({
				type: 'POST',
				 statusCode: {
					  401: function() {
						  window.location.reload();
					  }
				  },
				url:"/pgadmissions/deleteentity/qualification",
				data:	{
						id: id	
					}, 
				success:	function(data) {
						$('#qualificationsSection').html(data);
					}	
					
			});
	});
	

	// -------------------------------------------------------------------------------
	// "Accept terms" checkbox.
	// -------------------------------------------------------------------------------
	$("input[name*='acceptTermsQDCB']").click(function()
	{
		if ($("#acceptTermsQDValue").val() =='YES')
		{
			$("#acceptTermsQDValue").val("NO");
		}
		else
		{	
			$("#acceptTermsQDValue").val("YES");

			qualImgCount = 0;
			
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
					acceptedTerms: $("#acceptTermsQDValue").val()
				},
				success: function(data) {}
			});
		}
	});
	

	// -------------------------------------------------------------------------------
	// Add a qualification.
	// -------------------------------------------------------------------------------
	$('#addQualificationButton').click(function()
	{
		if ($('#acceptTermsQDValue').length != 0 &&  $("#acceptTermsQDValue").val() =='NO')
		{
			$(this).parent().parent().parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			var $infobar = $('#qual-info-bar-div.section-info-bar');
			$infobar.switchClass("section-info-bar", "section-error-bar", 1);
			if ($infobar)
			{
				$infobar.prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
				qualImgCount = qualImgCount + 1;
			}
			addToolTips();
		}
		else
		{
			$("span[name='nonAcceptedQD']").html('');
			postQualificationData('add');
		}
	});
	

	// -------------------------------------------------------------------------------
	// Save qualification.
	// -------------------------------------------------------------------------------
	$('#qualificationsSaveButton').click(function()
	{
		if ($("#acceptTermsQDValue").val() =='NO')
		{ 
			// Highlight the information bar and terms box.
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			var $infobar = $('#qual-info-bar-div.section-info-bar');
			$infobar.switchClass("section-info-bar", "section-error-bar", 1);
			if ($infobar)
			{
				$infobar.prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
				qualImgCount = qualImgCount + 1;
			}
			addToolTips();
		}
		else
		{
			$("span[name='nonAcceptedQD']").html('');
			
			// Check for a "dirty" qualification form. If there is data try to submit it.
			if (!isFormEmpty('#qualificationsSection form'))
			{
				postQualificationData('close');
			}
			else
			{
				$('#qualificationsCloseButton').trigger('click');
			}
		}
	});
	

	// -------------------------------------------------------------------------------
	// Edit a qualification.
	// -------------------------------------------------------------------------------
	$('a[name="editQualificationLink"]').click(function()
	{
		var id = this.id;
		id = id.replace('qualification_', '');	
		$.ajax({
			 type: 'GET',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
				url:"/pgadmissions/update/getQualification",
				data:{
					applicationId:  $('#applicationId').val(),
					qualificationId: id,
					message: 'edit',					
					cacheBreaker: new Date().getTime()
				},
				success: function(data)
				{
					$('#qualificationsSection').html(data);
					
					if ($("#currentQualificationCB").is(":checked"))
					{
						$("#qualificationAwardDate").removeAttr("disabled", "disabled");
						$("#proofOfAward").removeAttr("disabled", "disabled");
						$("#quali-grad-id em").remove();
						$("#quali-grad-id").text("Grade / Result / GPA").append('<em>*</em>');
						$("#quali-award-date-lb em").remove();
						$("#quali-award-date-lb").append('<em>*</em>').removeClass("grey-label");
						$("#quali-proof-of-award-lb").removeClass("grey-label");
					}
					else
					{	
						$("#qualificationAwardDate").attr("disabled", "disabled");
						$("#proofOfAward").attr("disabled", "disabled");
						$("#quali-grad-id em").remove();
						$("#quali-grad-id").text("Expected Grade / Result / GPA").append('<em>*</em>');
						$("#quali-award-date-lb em").remove();
						$("#quali-award-date-lb").text("Award Date").addClass("grey-label");
						$("#quali-proof-of-award-lb").text("Proof of award (PDF)").addClass("grey-label");
					}
					
					// Cheap way of changing the button text.
					$('#addQualificationButton').html('Update');
				}
		});
	});
	

	// -------------------------------------------------------------------------------
	// Clear button.
	// -------------------------------------------------------------------------------
	$('a[name="qualificationCancelButton"]').click(function()
	{
		$.ajax({
			 type: 'GET',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
				url:"/pgadmissions/update/getQualification",
				data:{
					applicationId:  $('#applicationId').val(),
					message: 'cancel',					
					cacheBreaker: new Date().getTime()
				},
				success:function(data) {
					$('#qualificationsSection').html(data);
				}
		});
	});

	
	bindDatePicker('#qualificationStartDate');
	bindDatePicker('#qualificationAwardDate');
	addToolTips();
	
  // Generic file upload solution...
	watchUpload($('#proofOfAward'));	
});

function postQualificationData(message)
{
	$('#qualificationsSection > div').append('<div class="ajax" />');

	$.ajax({
		type: 'POST',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  }
		  },
		url:"/pgadmissions/update/editQualification",
		data:{  
			qualificationSubject: $("#qualificationSubject").val(), 
			qualificationInstitution: $("#qualificationInstitution").val(), 
			qualificationType: $("#qualificationType").val(),
			qualificationGrade: $("#qualificationGrade").val(),
			qualificationScore: $("#qualificationScore").val(),
			qualificationStartDate: $("#qualificationStartDate").val(),
			qualificationLanguage: $("#qualificationLanguage").val(),
			qualificationAwardDate: $("#qualificationAwardDate").val(),
			completed: $("#currentQualification").val(),			
			qualificationId: $("#qualificationId").val(),
			applicationId:  $('#applicationId').val(),
			application:  $('#applicationId').val(),
			institutionCountry: $('#institutionCountry').val(),
			proofOfAward: $('#document_PROOF_OF_AWARD').val(),
			message:message
		},
		success:function(data) {
			$('#qualificationsSection').html(data);
			$('#qualificationsSection div.ajax').remove();
			markSectionError('#qualificationsSection');
	
			if (message == 'close')
			{
				// Close the section only if there are no errors.
				var errorCount = $('#qualificationsSection .invalid:visible').length;
				if (errorCount == 0)
				{
					$('#qualifications-H2').trigger('click');
				}
			}
		}
	});
}
function ajaxProofOfAwardDelete(){
	
	if($('#profOfAwardId') && $('#profOfAwardId').val() && $('#profOfAwardId').val() != ''){
		$.ajax({
			type: 'POST',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
			url:"/pgadmissions/delete/asyncdelete",
			data:{
				documentId: $('#profOfAwardId').val()				
			}				
		});

	}
}
function ajaxProofOfAwardUpload()
{	
	// Showing/hiding progress bar when we're uploading the file via AJAX.	
	$("#progress").ajaxStart(function()
		{
			$(this).show();
		})
		.ajaxComplete(function()
		{
			$(this).hide();
			$('#progress').html("");
		}
	);

	// Remove any previous error messages.
	$('#qualUploadedDocument').find('span.invalid').remove();

	// Functionality for uploading files via AJAX (immediately on selection).
	$.ajaxFileUpload(
	{
		url:            '/pgadmissions/documents/async',
		secureuri:      false,
		fileElementId:  'proofOfAward',	
		dataType:       'text',
		data:           { type: 'PROOF_OF_AWARD' },
		success: function(data)
		{	
			if ($(data).find('span.invalid').length == 0)
			{
				// i.e. if there are no uploading errors, which would be indicated by the presence of a SPAN.invalid tag.
				$('#qualUploadedDocument').html(data);
				$('#qualUploadedDocument').show();
				$('#uploadFields').addClass('uploaded');
				$('span[name="supportingDocumentSpan"] a.button-edit').attr({'id':'editQualiPOA','data-desc':'Edit Proof Of Award'});
			}
			else
			{
				$('#qualUploadedDocument').append(data);
			}
		}
	});

}