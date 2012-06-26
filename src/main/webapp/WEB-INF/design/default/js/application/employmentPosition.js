$(document).ready(function(){

	var empImgCount = 0;
	
	$("#acceptTermsEPValue").val("NO");
	limitTextArea();
	
	// Current employment checkbox
	$('#current').click(function()
	{
		if ($('#current:checked').val() !== undefined) 
		{
			// checked
			$("#posi-end-date-lb").text("End Date").addClass("grey-label");
			$('#position_endDate').attr("disabled", "disabled")
                                  .val(''); // empty date field.
		}
		else
		{
			// unchecked
			$('#position_endDate').removeAttr("disabled");
			$("#posi-end-date-lb").text("End Date").append('<em>*</em>').removeClass("grey-label");
		}
	});
	
	$('#positionCloseButton').click(function(){
		$('#position-H2').trigger('click');
		return false;
	});
	
	$('a[name="deleteEmploymentButton"]').click( function(){	
			var id = $(this).attr("id").replace("position_", "");
			$.ajax({
				type: 'POST',
				statusCode: {
				  401: function() {
					  window.location.reload();
				  }
				},
				url:"/pgadmissions/deleteentity/employment",
				data:{
					id: id	
				}, 				
				success:function(data) {
					$('#positionSection').html(data);
				}	
					
			});
	});
	
	$("input[name*='acceptTermsEPCB']").click(function() {
		if ($("#acceptTermsEPValue").val() =='YES'){
			$("#acceptTermsEPValue").val("NO");
		} else {	
			$("#acceptTermsEPValue").val("YES");
			
			/*
			$(".terms-box").attr('style','');
			$("#emp-info-bar-div").switchClass("section-error-bar", "section-info-bar", 1);
			$("#emp-info-bar-span").switchClass("invalid-info-text", "info-text", 1);
			$("#emp-info-bar-div .row span.error-hint").remove();
			*/
			empImgCount = 0;
			
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
					acceptedTerms: $("#acceptTermsEPValue").val()
				},
				success:function(data) {}
			});
		}
		});
	
	$('#positionSaveAndCloseButton').click(function(){
		if ($("#acceptTermsEPValue").val() =='NO')
		{ 
			// Highlight the information bar and terms box.
//			var $form = $('#positionSection form');
//			$('.terms-box, .section-info-bar', $form).css({ borderColor: 'red', color: 'red' });
			
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			var $infobar = $('#emp-info-bar-div.section-info-bar');
			$infobar.switchClass("section-info-bar", "section-error-bar", 1);
			if ($infobar)
			{
				$infobar.prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
				empImgCount = empImgCount + 1;
			}
			addToolTips();
			
		}
		else
		{
			$("span[name='nonAcceptedEP']").html('');

			// Check for a "dirty" employment position form. If there is data try to submit it.
			if (!isFormEmpty('#positionSection'))
			{
				postEmploymentData('close');
			}
			else
			{
				$('#positionCloseButton').trigger('click');
			}
		}
	});

	$('#addPosisionButton').click(function(){
		if($('#acceptTermsEPValue').length != 0  &&  $("#acceptTermsEPValue").val() =='NO'){ 
			//$("span[name='nonAcceptedEP']").html('You must agree to the terms and conditions');
			$(this).parent().parent().parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			var $infobar = $('#emp-info-bar-div.section-info-bar');
			$infobar.switchClass("section-info-bar", "section-error-bar", 1);
			if ($infobar)
			{
				$infobar.prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
				empImgCount = empImgCount + 1;
			}
			addToolTips();
			
		}
		else{
			$("span[name='nonAcceptedEP']").html('');
			postEmploymentData('add');
		}
	});

	$('a[name="positionEditButton"]').click(function(){
		var id = this.id;
		id = id.replace('position_', '');	

		$.ajax({
			 type: 'GET',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
			  url: "/pgadmissions/update/getEmploymentPosition",
			  data:	{
					applicationId:  $('#applicationId').val(), 
					employmentId: id,
					message: 'edit',					
					cacheBreaker: new Date().getTime()
				}, 
			  success:function(data) {								
					$('#positionSection').html(data);
					var curruntPos = $('#current').is(':checked');
					if(curruntPos == true){
						$('#position_endDate').attr('disabled','disabled');
						$('#posi-end-date-lb').addClass('grey-label');
						$('#posi-end-date-lb em').hide();
					}
				}
		});
	});

	$('a[name="positionCancelButton"]').click(function(){
		$.ajax({
			 type: 'GET',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
			  url: "/pgadmissions/update/getEmploymentPosition",
			  data:	{
					applicationId:  $('#applicationId').val(),
					message: 'cancel',					
					cacheBreaker: new Date().getTime()
				}, 
			  success:function(data) {
					$('#positionSection').html(data);
				}
		});		
		
	});


	bindDatePicker('#position_startDate');
	bindDatePicker('#position_endDate');
	addToolTips();

});

function postEmploymentData(message){
	var current = false;
	if ($('#current:checked').val() !== undefined) {
		 current = true;
	}

	$('#positionSection > div').append('<div class="ajax" />');

	$.ajax({
		type: 'POST',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  }
		  },
		url:"/pgadmissions/update/editEmploymentPosition",
		data:{ 
			position: $("#position_title").val(),
			startDate: $("#position_startDate").val(), 
			endDate: $("#position_endDate").val(), 
			remit: $("#position_remit").val(), 
			language: $("#position_language").val(), 
			employerCountry: $("#position_country").val(),
			employerName: $("#position_employer_name").val(),
			employerAddress: $("#position_employer_address").val(),
			current: current,
			application: $("#applicationId").val(),
			applicationId: $("#applicationId").val(),		 
			employmentId: $("#positionId").val(), 
			message:message
		},
	   success: function(data) {
				$('#positionSection').html(data);
				$('#positionSection div.ajax').remove();
				markSectionError('#positionSection');
	
				if (message == 'close')
				{
					// Close the section only if there are no errors.
					var errorCount = $('#positionSection .invalid:visible').length;
					if (errorCount == 0)
					{
						$('#position-H2').trigger('click');
					}
				}
	   		}
	});
}