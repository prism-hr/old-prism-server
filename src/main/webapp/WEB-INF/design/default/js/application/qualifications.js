$(document).ready(function(){
	
	var qualImgCount = 0;
	
	$("#acceptTermsQDValue").val("NO");
	
	/*if($("#qualificationInstitution").val() == ""){
		$("#currentQualificationCB").attr('checked', false);
		$("#currentQualification").val("NO");
	}
	
	if($("#currentQualificationCB").is(":checked")){
		$("#currentQualification").val("YES");
	}
	else{	
		$("#currentQualification").val("NO");
		$("#qualificationAwardDate").val("");
		$("#qualificationAwardDate").attr("disabled", "disabled");
		$("#proofOfAward").val("");
		$("#proofOfAward").attr("disabled", "disabled");
	}*/
	
	$('#qualificationsCloseButton').click(function(){
		$('#qualifications-H2').trigger('click');
		return false;
	});
	
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
	
	$('a[name="deleteQualificationButton"]').click( function(){

			var id = $(this).attr("id").replace("qualification_", "");
			$.post("/pgadmissions/deleteentity/qualification",
					{
						id: id	
					}, 
					
					function(data) {
						$('#qualificationsSection').html(data);
					}	
					
				);
	});
	
	$("input[name*='acceptTermsQDCB']").click(function() {
		if ($("#acceptTermsQDValue").val() =='YES'){
			$("#acceptTermsQDValue").val("NO");
		} else {	
			$("#acceptTermsQDValue").val("YES");
			
			/*
			$(".terms-box").attr('style','');
			$("#qual-info-bar-div").switchClass("section-error-bar", "section-info-bar", 1);
			$("#qual-info-bar-span").switchClass("invalid-info-text", "info-text", 1);
			$("#qual-info-bar-div .row span.error-hint").remove();
			*/
			qualImgCount = 0;
			
			$.post("/pgadmissions/acceptTerms", {  
				applicationId: $("#applicationId").val(), 
				acceptedTerms: $("#acceptTermsQDValue").val()
			},
			function(data) {
			});
		}
		});
	
	$('#addQualificationButton').click(function(){
		
		if($('#acceptTermsQDValue').length != 0 &&  $("#acceptTermsQDValue").val() =='NO'){
			
			$(this).parent().parent().parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			$("#qual-info-bar-div").switchClass("section-info-bar", "section-error-bar", 1);
			$("#qual-info-bar-span").switchClass("info-text", "invalid-info-text", 1);
			if(qualImgCount == 0){
				$("#qual-info-bar-div .row").prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
				qualImgCount = qualImgCount + 1;
			}
			addToolTips();
			
		}
		else{
			$("span[name='nonAcceptedQD']").html('');
			postQualificationData('add');
		}
	});
	
	$('#qualificationsSaveButton').click(function()
	{
		if ($("#acceptTermsQDValue").val() =='NO')
		{ 
			// Highlight the information bar and terms box.
//			var $form = $('#qualificationsSection form');
//			$('.terms-box, .section-info-bar', $form).css({ borderColor: 'red', color: 'red' });
			
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			$("#qual-info-bar-div").switchClass("section-info-bar", "section-error-bar", 1);
			$("#qual-info-bar-span").switchClass("info-text", "invalid-info-text", 1);
			if(qualImgCount == 0){
				$("#qual-info-bar-div .row").prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
				qualImgCount = qualImgCount + 1;
			}
			addToolTips();
			
		}
		else{
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
	
	$('a[name="editQualificationLink"]').click(function(){
		var id = this.id;
		id = id.replace('qualification_', '');	
		$.get("/pgadmissions/update/getQualification",
				{
					applicationId:  $('#applicationId').val(),
					qualificationId: id,
					message: 'edit',					
					cacheBreaker: new Date().getTime()
				},
				function(data) {
					$('#qualificationsSection').html(data);
					
					if($("#currentQualificationCB").is(":checked")){
						
						$("#qualificationAwardDate").removeAttr("disabled", "disabled");
						$("#proofOfAward").removeAttr("disabled", "disabled");
						$("#quali-grad-id em").remove();
						$("#quali-grad-id").text("Grade / Result / GPA").append('<em>*</em>');
						$("#quali-award-date-lb em").remove();
						$("#quali-award-date-lb").append('<em>*</em>').removeClass("grey-label");
						$("#quali-proof-of-award-lb").removeClass("grey-label");
						
					}
					else{	
						
						$("#qualificationAwardDate").attr("disabled", "disabled");
						$("#proofOfAward").attr("disabled", "disabled");
						$("#quali-grad-id em").remove();
						$("#quali-grad-id").text("Expected Grade / Result / GPA").append('<em>*</em>');
						$("#quali-award-date-lb em").remove();
						$("#quali-award-date-lb").text("Award Date").addClass("grey-label");
						$("#quali-proof-of-award-lb").text("Proof of award (PDF)").addClass("grey-label");
						
					}
					
				}
		);
	});
	
	$('a[name="qualificationCancelButton"]').click(function(){
		$.get("/pgadmissions/update/getQualification",
				{
					applicationId:  $('#applicationId').val(),
					message: 'cancel',					
					cacheBreaker: new Date().getTime()
				},
				function(data) {
					$('#qualificationsSection').html(data);
				}
		);
	});
	
	bindDatePicker('#qualificationStartDate');
	bindDatePicker('#qualificationAwardDate');
	addToolTips();
	
  // Generic file upload solution...
	watchUpload($('#proofOfAward'));	
	
	/* Show the upload field if edit button is clicked. */
	$(document).on('click','a.button-edit', function()
  {
		$(this).closest('.uploaded').removeClass('uploaded');
		$(this).hide();
	});
	
	
});

function postQualificationData(message)
{
	$('#qualificationsSection > div').append('<div class="ajax" />');

	$.post("/pgadmissions/update/editQualification", {  
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
	function(data) {
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
	});
}
function ajaxProofOfAwardDelete(){
	
	if($('#profOfAwardId') && $('#profOfAwardId').val() && $('#profOfAwardId').val() != ''){
		$.post("/pgadmissions/delete/asyncdelete",
			{
				documentId: $('#profOfAwardId').val()
				
			}				
		);

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