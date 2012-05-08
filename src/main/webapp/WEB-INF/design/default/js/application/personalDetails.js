$(document).ready(function(){

	
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
			
			if ($('#candidateNationalitiesLabel').length == 0){
				var candidNatHtml = '	<label class="plain-label" id="candidateNationalitiesLabel">My Nationality</label>';
				$('#existingCandidateNationalities').prepend(candidNatHtml);
			}
			
			var html = 
	  	 	
			'	<div class="field">'+
			'		<label class="full">' + $('#candidateNationalityCountry option:selected').text() + '</label>'  +
	  		"		<input type='hidden' name='candidateNationalities' value='" +$('#candidateNationalityCountry option:selected').val()+ "'/>" +
	  		'		<a class="button-delete">Delete</a><br/>'+
	  		'	</div>';
				
			// To show the tool-tip with the label 
			
			var myHint = "<span id=\"my-hint\" class=\"hint individual-hint\" data-desc=\"<@spring.message \'personalDetails.nationality\'/>\"></span>";
			
			if($('#existingCandidateNationalities').children().length <= 1){
				$('#existingCandidateNationalities').append(myHint + html);
			}else{
				$('#existingCandidateNationalities').append(html);
			}
			$('#my-nationality-hint').hide();
			
			
			$('#candidateNationalityCountry').val("");
			
			$('#nationality-em').remove();
			$('#my-nationality').replaceWith('<label class="plain-label" id="my-nationality-lb"></label>');
			$('#my-nationality-lb').html("");
			
		}
		
	});
	
	//maternal guardian nationalities
	$('#addMaternalNationalityButton').on("click", function(){
		if( $('#maternalNationalityCountry option:selected').val()!= ''){
			
			if ($('#maternalNationalitiesLabel').length == 0){
				var motherNatHtml = '	<label class="plain-label" id="maternalNationalitiesLabel">Mother\'s Nationality</label>';
				$('#existingMaternalNationalities').prepend(motherNatHtml);
			}
			
			var html = 
			'	<div class="field">'+
			'		<label class="full">' + $('#maternalNationalityCountry option:selected').text() + '</label>'  +
	  		"		<input type='hidden' name='maternalGuardianNationalities' value='" +$('#maternalNationalityCountry option:selected').val() + "'/>" +
	  		'		<a class="button-delete">Delete</a><br/>'+
	  		'	</div>';
			
			// To show the tool-tip with the label 
			
			var maternalHint = "<span id=\"maternal-hint\" class=\"hint individual-hint\" data-desc=\"<@spring.message \'personalDetails.maternalGuardianNationality\'/>\"></span>";
			
			if($('#existingMaternalNationalities').children().length <= 1){
				$('#existingMaternalNationalities').append(maternalHint + html);
			}else{
				$('#existingMaternalNationalities').append(html);
			}
			$('#maternal-nationality-hint').hide();
			
			$('#maternalNationalityCountry').val("");
			$('#maternal-nationality').replaceWith('<label class="plain-label" id="maternal-nationality-lb"></label>');
			$('#maternal-nationality-lb').html("");

		}
		
	});
	
	//paternal guardian nationalities
	$('#addPaternalNationalityButton').on("click", function(){
		if( $('#paternalNationalityCountry option:selected').val()!= ''){
			
			if ($('#paternalNationalitiesLabel').length == 0){
				var fatherNatHtml = '	<label class="plain-label" id="paternalNationalitiesLabel">Father\'s Nationality</label>';
				$('#existingPaternalNationalities').prepend(fatherNatHtml);
			}
			
			var html = 
			'	<div class="field">'+
			'		<label class="full">' + $('#paternalNationalityCountry option:selected').text() + '</label>'  +
	  		"		<input type='hidden' name='paternalGuardianNationalities' value='" +$('#paternalNationalityCountry option:selected').val() + "'/>" +
	  		'		<a class="button-delete">Delete</a> <br/>'+
	  		'	</div>';
			
			// To show the tool-tip with the label 
			
			var paternalHint = "<span id=\"paternal-hint\" class=\"hint individual-hint\" data-desc=\"<@spring.message \'personalDetails.paternalGuardianNationality\'/>\"></span>";
			
			if($('#existingPaternalNationalities').children().length <= 1){
				$('#existingPaternalNationalities').append(paternalHint + html);
			}else{
				$('#existingPaternalNationalities').append(html);
			}
			$('#paternal-nationality-hint').hide();
			
			$('#paternalNationalityCountry').val("");
			
			$('#paternal-nationality').replaceWith('<label class="plain-label" id="paternal-nationality-lb"></label>');
			$('#paternal-nationality-lb').html("");

		}
	});
	
	
	$("input[name*='acceptTermsPEDCB']").click(function() {
		if ($("#acceptTermsPEDValue").val() =='YES'){
			$("#acceptTermsPEDValue").val("NO");
		} else {	
			$("#acceptTermsPEDValue").val("YES");
			$.post("/pgadmissions/acceptTerms", {  
				applicationId: $("#applicationId").val(), 
				acceptedTerms: $("#acceptTermsPEDValue").val()
			},
			function(data) {
			});
		}
		});
	
	$('#personalDetailsSaveButton').on("click", function(){	
		if( $("#acceptTermsPEDValue").val() =='NO'){ 
			$("span[name='nonAcceptedPED']").html('You must agree to the terms and conditions');
		}
		else{
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
		//open/close
	var $header  = $('#personalDetails-H2');
	var $content = $header.next('div');
	$header.bind('click', function()
	{	
	  $content.toggle();
	  $(this).toggleClass('open', $content.is(':visible'));
	  return false;
	});
		
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
	
	var englishFirstLanguage = false;
	if ($('#englishFirstLanguageCB:checked').val() !== undefined) {
		englishFirstLanguage = true;
	}
	
	var requiresVisa = false;
	if ($('#requiresVisaCB:checked').val() !== undefined) {
		requiresVisa = true;
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
			personalDetailsId: $("#personalDetailsId").val(), 
			application: $('#applicationId').val(),
			applicationId: $('#applicationId').val(),		
			englishFirstLanguage:englishFirstLanguage,		
			requiresVisa: requiresVisa,		
			candidateNationalities:"",
			maternalGuardianNationalities:"",
			paternalGuardianNationalities:"",
			ethnicity: $("#ethnicity").val(), 
			disability: $("#disability").val(), 
			message: message
			
		};
	
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
