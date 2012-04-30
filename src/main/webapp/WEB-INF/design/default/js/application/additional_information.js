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
		if($(this).val() == 'TRUE') {
			$("#convictions-details-lbl").append('<em>*</em>').removeClass("grey-label");
			$("#convictionsText").removeClass("grey-label");
			$("#convictionsText").removeAttr("disabled", "disabled");
		} else {
			var newLblText = $("#convictions-details-lbl").text();
			var starIndex = newLblText.lastIndexOf("*");
			if( starIndex > 0) {
			  newLblText = newLblText.substring(0, starIndex);
			}
			$("#convictions-details-lbl").text(newLblText).addClass("grey-label");
			$("#convictionsText").val("");
			$("#convictionsText").addClass("grey-label");
			$("#convictionsText").attr("disabled", "disabled");
		}
	});
	
	
	$('#informationSaveButton').click(function(){
		var hasConvictions = null;
		if ($('#convictionRadio_true:checked').val() !== undefined) {
			hasConvictions = true;
		}
		if ($('#convictionRadio_false:checked').val() !== undefined) {
			hasConvictions = false;
		}

		$.post("/pgadmissions/update/editAdditionalInformation", { 
			informationText: $("#informationText").val(),
			convictions: hasConvictions,
			convictionsText: $("#convictionsText").val(),
			applicationId:  $('#applicationId').val(),
			application:  $('#applicationId').val(),
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