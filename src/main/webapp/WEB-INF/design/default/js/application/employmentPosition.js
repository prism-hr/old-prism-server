$(document).ready(function(){

	$("#acceptTermsEPValue").val("NO");
	limitTextArea();
	
	$('#current').click(function() {
		if ($('#current:checked').val() !== undefined) {
			$("#posi-end-date-lb").text("End Date").addClass("grey-label");
			$('#position_endDate').attr("disabled", "disabled");
		}else{
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
			$.post("/pgadmissions/deleteentity/employment",
					{
						id: id	
					}, 				
					function(data) {
						$('#positionSection').html(data);
					}	
					
				);
	});
	
	$("input[name*='acceptTermsEPCB']").click(function() {
		if ($("#acceptTermsEPValue").val() =='YES'){
			$("#acceptTermsEPValue").val("NO");
		} else {	
			$("#acceptTermsEPValue").val("YES");
			$(".terms-box").attr('style','');
			$.post("/pgadmissions/acceptTerms", {  
				applicationId: $("#applicationId").val(), 
				acceptedTerms: $("#acceptTermsEPValue").val()
			},
			function(data) {
			});
		}
		});
	
	$('#positionSaveAndCloseButton').click(function(){
		if( $("#acceptTermsEPValue").val() =='NO'){ 
			//$("span[name='nonAcceptedEP']").html('You must agree to the terms and conditions');
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
		}
		else{
			$("span[name='nonAcceptedEP']").html('');
			postEmploymentData('close');
		}
	});

	$('#addPosisionButton').click(function(){
		if( $("#acceptTermsEPValue").val() =='NO'){ 
			//$("span[name='nonAcceptedEP']").html('You must agree to the terms and conditions');
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
		}
		else{
			$("span[name='nonAcceptedEP']").html('');
			postEmploymentData('add');
		}
	});

	$('a[name="positionEditButton"]').click(function(){
		var id = this.id;
		id = id.replace('position_', '');	
		$.get("/pgadmissions/update/getEmploymentPosition",
			{
				applicationId:  $('#applicationId').val(), 
				employmentId: id,
				message: 'edit',					
				cacheBreaker: new Date().getTime()
			},
			function(data) {								
				$('#positionSection').html(data);
				var curruntPos = $('#current').is(':checked');
				if(curruntPos == true){
					$('#position_endDate').attr('disabled','disabled');
					$('#posi-end-date-lb').addClass('grey-label');
					$('#posi-end-date-lb em').hide();
				}
			}
		);
	});

	$('a[name="positionCancelButton"]').click(function(){
		$.get("/pgadmissions/update/getEmploymentPosition",
			{
				applicationId:  $('#applicationId').val(),
				message: 'cancel',					
				cacheBreaker: new Date().getTime()
			},
			function(data) {
				$('#positionSection').html(data);
			}
		);
	});


	bindDatePicker('#position_startDate');
	bindDatePicker('#position_endDate');
	addToolTips();
	//open/close
	var $header  =$('#position-H2');
	var $content = $header.next('div');
	$header.bind('click', function()
	{
	  $content.toggle();
	  $(this).toggleClass('open', $content.is(':visible'));
	  return false;
	});


});

function postEmploymentData(message){
	var current = false;
	if ($('#current:checked').val() !== undefined) {
		 current = true;
	}
	$.post("/pgadmissions/update/editEmploymentPosition",
	{ 
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
   function(data) {
     $('#positionSection').html(data);
   });
}