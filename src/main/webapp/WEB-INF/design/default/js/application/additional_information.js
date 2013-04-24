$(document).ready(function(){
	
	 //Add Counter
    addCounter();
	
	var addImgCount = 0;
	debugger;
	$("#acceptTermsAIDValue").val("NO");
	
	//limitTextArea();
	if ($('#convictionRadio_true').attr("checked", "checked")) {
		
	} else {
		$("#convictionsText").attr("disabled", "disabled");
	}
	
	if ($("#convictionsText").val() != "") {
	    $("#convictionsText").removeAttr("disabled", "disabled");
	}
	
	$('#additionalCloseButton').click(function(){
		$('#additional-H2').trigger('click');
		return false;
	});
	
	$('#informationClearButton').click(function()
	{
		$('#additionalInformationSection > div').append('<div class="ajax" />');
		loadAdditionalInformationSection(true);
	});
	
	$("input[name$='convictionRadio']").click(function()
	{
		if ($(this).val() == 'TRUE')
		{
			if ($("#convictions-details-lbl em").length == 0)
			{
				$("#convictions-details-lbl").text($.trim($("#convictions-details-lbl").text())).append('<em>*</em>');
			}
			$("#convictions-details-lbl").removeClass("grey-label");
			$("#convictionsText").removeClass("grey-label");
			$("#convictionsText").removeAttr("disabled", "disabled");
		}
		else
		{
			$("#convictions-details-lbl em").remove();
			$("#convictions-details-lbl").addClass("grey-label");
			$("#convictionsText").val("");
			$("#convictionsText").addClass("grey-label");
			$("#convictionsText").attr("disabled", "disabled");
		}
	});
	
	$("input[name*='acceptTermsAIDCB']").click(function()
	{
		if ($("#acceptTermsAIDValue").val() =='YES')
		{
			$("#acceptTermsAIDValue").val("NO");
		}
		else
		{
			$("#acceptTermsAIDValue").val("YES");

			addImgCount = 0;
		}
	});
	
	$("input[name*='confirmNextStage']").click(function()
			{
				if ($("#confirmNextStageValue").val() =='YES')
				{
					$("#confirmNextStageValue").val("NO");
				}
				else
				{
					$("#confirmNextStageValue").val("YES");

					addImgCount = 0;
				}
			});
	
	
	$('#informationSaveButton').click(function()
	{
		var hasConvictions = null;
		if ($('#convictionRadio_true:checked').val() !== undefined)
		{
			hasConvictions = true;
		}
		if ($('#convictionRadio_false:checked').val() !== undefined)
		{
			hasConvictions = false;
		}

		var acceptedTheTerms;
		if ($("#acceptTermsAIDValue").val() == 'NO')
		{
			acceptedTheTerms = false;
		}
		else
		{
			acceptedTheTerms = true;
		}
			
		$("span[name='nonAcceptedAID']").html('');
		$('#additionalInformationSection > div').append('<div class="ajax" />');

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
			url:"/pgadmissions/update/editAdditionalInformation",
			data: { 
				informationText: $("#informationText").val(),
				convictions: hasConvictions,
				convictionsText: $("#convictionsText").val(),
				applicationId:  $('#applicationId').val(),
				application:  $('#applicationId').val(),
				message:'close',
				acceptedTerms: acceptedTheTerms
			},
		
			success:function(data)
			{
				$('#additionalInformationSection').html(data);
			
				// Close the section only if there are no errors.
				var errorCount = $('#additionalInformationSection .alert-error:visible').length;
				if (errorCount == 0)
				{
					$('#additional-H2').trigger('click');
				}
				else
				{
					markSectionError('#additionalInformationSection');
				}
			},
			complete: function()
			{
				$('#additionalInformationSection div.ajax').remove();
			}
		});
	});
	
	addToolTips();
	
});