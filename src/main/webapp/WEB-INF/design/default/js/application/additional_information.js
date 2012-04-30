$(document).ready(function(){
	
	limitTextArea();
	
	$('#additionalCloseButton').click(function(){
		$('#additional-H2').trigger('click');
		return false;
	});
	
	$('a[name="informationCancelButton"]').click(function(){
		$.get("/pgadmissions/update/getAdditionalInformation",
				{
					applicationId:  $('#applicationId').val(),
					message: 'cancel',					
					cacheBreaker: new Date().getTime() 
				},
				function(data) {
					$('#additionalInformationSection').html(data);
				}
		);
	});
	
	$("input[name$='convictionRadio']").click(function() {
		if($(this).val() == 'YES') {
			$("#convictions-details-lbl").append('<em>*</em>').removeClass("grey-label");
			$("#convictionsText").removeClass("grey-label");
			$("#convictionsText").removeAttr("disabled", "disabled");
		} else {
			var newLblText = $("#convictions-details-lbl").text();
			newLblText = newLblText.substring(0, newLblText.length - 1);
			$("#convictions-details-lbl").text(newLblText).addClass("grey-label");
			$("#convictionsText").addClass("grey-label");
			$("#convictionsText").attr("disabled", "disabled");
		}
	});
		/*
		if ($("#currentQualification").val() =='YES'){
			$("#qualificationAwardDate").val("");
			$("#qualificationAwardDate").attr("disabled", "disabled");
			$("#proofOfAward").val("");
			$("#proofOfAward").attr("disabled", "disabled");
			$("#quali-grad-id").text("Expected Grade / Result / GPA").append('<em>*</em>');
			$("#quali-award-date-lb").text("Award Date").addClass("grey-label");
			$("#quali-proof-of-award-lb").text("Proof of award (PDF)").addClass("grey-label");

			
		} else {		
		
			$("#currentQualification").val("YES");
			$("#qualificationAwardDate").removeAttr("disabled", "disabled");	
			$("#proofOfAward").removeAttr("disabled", "disabled");
			$("#quali-grad-id").text("Grade / Result / GPA").append('<em>*</em>');
			$("#quali-award-date-lb").append('<em>*</em>').removeClass("grey-label");
			$("#quali-proof-of-award-lb").append('<em>*</em>').removeClass("grey-label");
			
			bindDatePickers();
		}
	*/
	/*
	if($("#convictionRadio").val(":checked")){
		$("#currentQualification").val("YES");
	}
	else{	
		$("#currentQualification").val("NO");
		$("#qualificationAwardDate").val("");
		$("#qualificationAwardDate").attr("disabled", "disabled");
		$("#proofOfAward").val("");
		$("#proofOfAward").attr("disabled", "disabled");
	}
	*/
	
	var hasConvictions = false;
	if ($('#convictionRadio:checked').val() !== undefined) {
		hasConvictions = $('#convictionRadio:checked').val();
	}
	
	$('#informationSaveButton').click(function(){
		$.post("/pgadmissions/update/editAdditionalInformation", { 
			informationText: $("#informationText").val(),
			convictions: hasConvictions,
			convictionsText: $("#convictionsText").val(),
			applicationId:  $('#applicationId').val(),
			message:'close'
		},
		
		function(data) {
			$('#additionalInformationSection').html(data);
		});
	});
	
	addToolTips();
	//open/close
	var $header  =$('#additional-H2');
	var $content = $header.next('div');
	$header.bind('click', function()
	{
	  $content.toggle();
	  $(this).toggleClass('open', $content.is(':visible'));
	  return false;
	});
	
});