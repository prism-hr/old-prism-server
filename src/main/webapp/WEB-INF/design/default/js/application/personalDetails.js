$(document).ready(function(){

	
	var persImgCount = 0;
	
	//Adjust CSS to display multiple values in one line, delimited by comma ','
	// in admin view.
	
	$(".half,.multiples").each(function(){
		
		//alert($(this).text());
		$(this).css('display','inline');
		
	});
	
	$("#acceptTermsPEDValue").val("NO");
	$('#personalDetailsCloseButton').click(function(){		
		$('#personalDetails-H2').trigger('click');
		return false;
	});
	
	$('#personalDetailsCancelButton').click(function(){
		$.get("/pgadmissions/update/getPersonalDetails",
				{
					applicationId:  $('#applicationId').val(),					
					cacheBreaker: new Date().getTime()					
				},
				function(data) {
					$('#personalDetailsSection').html(data);
				}
		);
	});
	

	
	
	
	//candidate nationalities
	$('#addCandidateNationalityButton').on("click", function(){
		if( $('#candidateNationalityCountry option:selected').val()!= ''){
			
			var html = 
	  	 	
			'	<div class="nationality-item">'+
			'		<label class="full">' + $('#candidateNationalityCountry option:selected').text() + '</label>'  +
	  		"		<input type='hidden' name='candidateNationalities' value='" +$('#candidateNationalityCountry option:selected').val()+ "'/>" +
	  		'		<a class="button-delete" data-desc="Delete">Delete</a><br/>'+
	  		'	</div>';
			
			$('#my-nationality-div').append(html);
			
			
			$('#nationality-em').remove();
			
		}
		
	});
	
	//candidate nationalities - remove
	$('.nationality-item a.button-delete').live('click', function(){
		
		$(this).parent().remove();
		return false;
	});
	
	//maternal guardian nationalities
	$('#addMaternalNationalityButton').on("click", function(){
		if( $('#maternalNationalityCountry option:selected').val()!= ''){
			
			var html = 
			'	<div class="nationality-item">'+
			'		<label class="full">' + $('#maternalNationalityCountry option:selected').text() + '</label>'  +
	  		"		<input type='hidden' name='maternalGuardianNationalities' value='" +$('#maternalNationalityCountry option:selected').val() + "'/>" +
	  		'		<a class="button-delete" data-desc="Delete">Delete</a><br/>'+
	  		'	</div>';
			
			// To show the tool-tip with the label 
			
			$('#maternal-nationality-div').append(html);

		}
		
	});
	
	//paternal guardian nationalities
	$('#addPaternalNationalityButton').on("click", function(){
		if( $('#paternalNationalityCountry option:selected').val()!= ''){
			
			var html = 
			'	<div class="nationality-item">'+
			'		<label class="full">' + $('#paternalNationalityCountry option:selected').text() + '</label>'  +
	  		"		<input type='hidden' name='paternalGuardianNationalities' value='" +$('#paternalNationalityCountry option:selected').val() + "'/>" +
	  		'		<a class="button-delete" data-desc="Delete">Delete</a> <br/>'+
	  		'	</div>';
			
			// To show the tool-tip with the label 
			
			$('#paternal-nationality-div').append(html);

		}
	});
	
	
	$("input[name*='acceptTermsPEDCB']").click(function() {
		if ($("#acceptTermsPEDValue").val() =='YES'){
			$("#acceptTermsPEDValue").val("NO");
		} else {	
			$("#acceptTermsPEDValue").val("YES");
			
			$(".terms-box").attr('style','');
			$("#pres-info-bar-div").switchClass("section-error-bar", "section-info-bar", 1);
			$("#pres-info-bar-span").switchClass("invalid-info-text", "info-text", 1);
			$("#pres-info-bar-div .row span.error-hint").remove();
			persImgCount = 0;
			
			$.post("/pgadmissions/acceptTerms", {  
				applicationId: $("#applicationId").val(), 
				acceptedTerms: $("#acceptTermsPEDValue").val()
			},
			function(data) {
			});
		}
		});
	
	$('#personalDetailsSaveButton').on("click", function()
	{	
		if ($("#acceptTermsPEDValue").val() =='NO')
		{ 
			// Highlight the information bar and terms box.
//			var $form = $('#personalDetailsSection form');
//			$('.terms-box, .section-info-bar', $form).css({ borderColor: 'red', color: 'red' });
			
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			$("#pres-info-bar-div").switchClass("section-info-bar", "section-error-bar", 1);
			$("#pres-info-bar-span").switchClass("info-text", "invalid-info-text", 1);
			if(persImgCount == 0){
				$("#pres-info-bar-div .row").prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
				persImgCount = persImgCount + 1;
			}
			addToolTips();
			
		}
		else
		{
			$("span[name='nonAcceptedPED']").html('');
			postPersonalDetailsData('close');
		}

	});
	

	
	// To make uncompleted functionalities disabled
	$(".disabledEle").attr("disabled", "disabled");	
	
	/// delete collection items		
	$("#existingCandidateNationalities").on("click", "a", function(){	
		$(this).parent("div").remove();
		
		if ( $('#existingCandidateNationalities').children().length <= 1 ) {
			$('#candidateNationalitiesLabel').remove();
			$('#my-nationality-lb').html("My Nationality");
		}
		
		if($('#existingCandidateNationalities').children().length == 1 ) {
			
			$('#my-hint').remove();
			$('#my-nationality-hint').show();
		}
					
	});
	
	$("#existingMaternalNationalities").on("click", "a", function(){	
		$(this).parent("div").remove();
		
		if ( $('#existingMaternalNationalities').children().length <= 1 ) {
			$('#maternalNationalitiesLabel').remove();
			$('#maternal-nationality-lb').html("Mother's Nationality");
		}
	});
	
	$("#existingPaternalNationalities").on("click", "a", function(){	
		$(this).parent("div").remove();
		
		if ( $('#existingPaternalNationalities').children().length <= 1 ) {
			$('#paternalNationalitiesLabel').remove();
			$('#paternal-nationality-lb').html("Father's Nationality");
		}
	});
		
	
	
	bindDatePicker('#dateOfBirth');
	addToolTips();
		
});


function postPersonalDetailsData(message){

	//candidate nationalities
	if( $('#candidateNationalityCountry option:selected').val()!= ''){
		var html = 	"<span><input type='hidden' name='candidateNationalities' value='" +$('#candidateNationalityCountry option:selected').val() + "'/>" + '</span>';
			
		
		$('#existingCandidateNationalities').append(html);
	}
	

	//maternal nationalities
	if( $('#maternalNationalityCountry option:selected').val()!= ''){
		var html = 	"<span><input type='hidden' name='maternalGuardianNationalities' value='"  +$('#maternalNationalityCountry option:selected').val() + "'/></span>";
		
		$('#existingMaternalNationalities').append(html);
	}
	
	//paternal nationalities
	if( $('#paternalNationalityCountry option:selected').val()!= ''){
		
		var html = 	"<span><input type='hidden' name='paternalGuardianNationalities' value='{" +$('#paternalNationalityCountry option:selected').val() + "'/></span>";
		
		$('#existingPaternalNationalities').append(html);
	}
	

	//general post data
	var postData ={ 
			firstName: $("#firstName").val(), 
			lastName: $("#lastName").val(), 
			email: $("#email").val(),
			country: $("#country").val(), 
			dateOfBirth: $("#dateOfBirth").val(),
			residenceCountry: $("#residenceCountry").val(),
			messenger: $("#pd_messenger").val(),
			phoneNumber: $('#pd_telephone').val(),			
			application: $('#applicationId').val(),
			applicationId: $('#applicationId').val(),			
			candidateNationalities:"",
			maternalGuardianNationalities:"",
			paternalGuardianNationalities:"",
			ethnicity: $("#ethnicity").val(), 
			disability: $("#disability").val(), 
			message: message
			
		};
	

	if ($('input:radio[name=englishFirstLanguage]:checked').length > 0) {
		postData.englishFirstLanguage = $('input:radio[name=englishFirstLanguage]:checked').val();
	}
	

	if ($('input:radio[name=requiresVisa]:checked').length > 0 ) {
		postData.requiresVisa = $('input:radio[name=requiresVisa]:checked').val();
	}
	
	var gender =  $("input[name='genderRadio']:checked").val();
	if(gender){
		postData.gender = gender;
	}
	
	//do the post!
	$.post( "/pgadmissions/update/editPersonalDetails" ,
			$.param(postData) + 
			"&" + $('input[name="candidateNationalities"]').serialize()+
			"&" + $('input[name="maternalGuardianNationalities"]').serialize()+
			"&" + $('input[name="paternalGuardianNationalities"]').serialize(),
			
			 function(data) {
			    $('#personalDetailsSection').html(data);
			  }
	);
	
}
