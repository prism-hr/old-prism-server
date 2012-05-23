$(document).ready(function(){
	$("#acceptTermsQDValue").val("NO");
	
	if($("#qualificationInstitution").val() == ""){
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
	}
	
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
			$("#quali-grad-id").text("Expected Grade / Result / GPA").append('<em>*</em>');
			$("#quali-award-date-lb").text("Award Date").addClass("grey-label");
			$("#quali-proof-of-award-lb").text("Proof of award (PDF)").addClass("grey-label");
			
			if ($('#uploadFields').hasClass('uploaded'))
			{
				$('#uploadFields').addClass('upload-delete');
				$('#uploadFields a.button-edit').hide();
				/*
				$('#uploadFields a.docName').removeAttr('href');
				*/
			}
		}
		else
		{		
			// Check the box
			$("#currentQualification").val("YES");
			$("#qualificationAwardDate").removeAttr("disabled", "disabled");	
			$("#proofOfAward").removeAttr("disabled");
			$("#quali-grad-id").text("Grade / Result / GPA").append('<em>*</em>');
			$("#quali-award-date-lb").append('<em>*</em>').removeClass("grey-label");
			$("#quali-proof-of-award-lb").append('<em>*</em>').removeClass("grey-label");

			if ($('#uploadFields').hasClass('uploaded'))
			{
				$('#uploadFields').removeClass('upload-delete');
				$('#uploadFields a.button-edit').show();
				/*
				$('#uploadFields a.docName').attr('href', 
				$('#uploadFields a.docName').attr('data-oldlink'));
				*/
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
			$(".terms-box").attr('style','');
			$.post("/pgadmissions/acceptTerms", {  
				applicationId: $("#applicationId").val(), 
				acceptedTerms: $("#acceptTermsQDValue").val()
			},
			function(data) {
			});
		}
		});
	
	$('#addQualificationButton').click(function(){
		if( $("#acceptTermsQDValue").val() =='NO'){ 
			//$("span[name='nonAcceptedQD']").html('You must agree to the terms and conditions');
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
		}
		else{
			$("span[name='nonAcceptedQD']").html('');
			postQualificationData('add');
		}
	});
	
	$('#qualificationsSaveButton').click(function(){
		if( $("#acceptTermsQDValue").val() =='NO'){ 
			//$("span[name='nonAcceptedQD']").html('You must agree to the terms and conditions');
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
		}
		else{
			$("span[name='nonAcceptedQD']").html('');
			postQualificationData('close');
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
						$("#quali-grad-id").text("Grade / Result / GPA").append('<em>*</em>');
						$("#quali-award-date-lb").append('<em>*</em>').removeClass("grey-label");
						$("#quali-proof-of-award-lb").append('<em>*</em>').removeClass("grey-label");
						
					}
					else{	
						
						$("#qualificationAwardDate").attr("disabled", "disabled");
						$("#proofOfAward").attr("disabled", "disabled");
						$("#quali-grad-id").text("Expected Grade / Result / GPA").append('<em>*</em>');
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
		
	$('#uploadFields').on('change','#proofOfAward', function(event){	
		ajaxProofOfAwardDelete();
		$('#progress').html("uploading file...");
		$('#proofOfAward').attr("readonly", "readonly");
		ajaxProofOfAwardUpload();
		$('#proofOfAward').removeAttr("readonly");
	});
	
	
	/* Show the upload field if edit button is clicked */
	$(document).on('click','a.button-edit', function(){
		
		$(this).closest('.uploaded').removeClass('uploaded');
		$(this).hide();
		
	});
	
	
});

function postQualificationData(message){
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
	
	$("#progress").ajaxStart(function(){
		$(this).show();
	})
	.ajaxComplete(function(){
		$(this).hide();
		$('#progress').html("");
		
	});

	$.ajaxFileUpload
	(
		{
			url:'/pgadmissions/documents/async',
			secureuri:false,
			
			fileElementId:'proofOfAward',	
			dataType:'text',
			data:{type:'PROOF_OF_AWARD'},
			success: function (data)
			{	
				//console.log(data);
				$('#qualUploadedDocument').html(data);
				$('#qualUploadedDocument').show();
				
				$('#uploadFields').addClass('uploaded');
				
				$('span[name="supportingDocumentSpan"] a.button-edit')
						.attr({'id':'editQualiPOA','data-desc':'Edit Proof Of Award'});
				
			}
		}
	)

}