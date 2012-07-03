$(document).ready(function(){
	
	var addImgCount = 0;
	
	$("#acceptTermsAIDValue").val("NO");
	
	limitTextArea();
	
	$('#additionalCloseButton').click(function(){
		$('#additional-H2').trigger('click');
		return false;
	});
	
	$('a[name="informationCancelButton"]').click(function(){

		$.ajax({
			 type: 'GET',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
			  url: "/pgadmissions/update/getAdditionalInformation",
			  data:{
					applicationId:  $('#applicationId').val(),
					message: 'cancel',					
					cacheBreaker: new Date().getTime() 
				}, 
			  success: function(data) {
					$('#additionalInformationSection').html(data);
				}	
		});
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
	
	$("input[name*='acceptTermsAIDCB']").click(function() {
		if ($("#acceptTermsAIDValue").val() =='YES'){
			$("#acceptTermsAIDValue").val("NO");
		} else {
			$("#acceptTermsAIDValue").val("YES");

			/*
			$(".terms-box").attr('style','');
			$("#add-info-bar-div").switchClass("section-error-bar", "section-info-bar", 1);
			$("#add-info-bar-span").switchClass("invalid-info-text", "info-text", 1);
			$("#add-info-bar-div .row span.error-hint").remove();
			*/
			addImgCount = 0;
			
			
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
		/*if ($("#acceptTermsAIDValue").val() =='NO')
		{ 
			// Highlight the information bar and terms box.
//			var $form = $('#additionalInformationSection form');
//			$('.terms-box, .section-info-bar', $form).css({ borderColor: 'red', color: 'red' });
			
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			var $infobar = $('#add-info-bar-div.section-info-bar');
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

		var acceptedTheTerms;
		if ($("#acceptTermsAIDValue").val() == 'NO'){
			acceptedTheTerms = false;
		}
		else{
			acceptedTheTerms = true;
		}
			
		$("span[name='nonAcceptedAID']").html('');
			$('#additionalInformationSection > div').append('<div class="ajax" />');

			$.ajax({
				type: 'POST',
				 statusCode: {
					  401: function() {
						  window.location.reload();
					  }
				  },
				url:"/pgadmissions/update/editAdditionalInformation",
				data:{ 
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
					var errorCount = $('#additionalInformationSection .invalid:visible').length;
					if (errorCount == 0)
					{
						$('#additional-H2').trigger('click');
					}else{
						markSectionError('#additionalInformationSection');
						
					}
				},
		        complete: function()
		        {
							$('#additionalInformationSection div.ajax').remove();
		        }
			});
	//	}

		
	});
	
	addToolTips();
	
});