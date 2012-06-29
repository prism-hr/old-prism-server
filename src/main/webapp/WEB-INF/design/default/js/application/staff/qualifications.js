$(document).ready(function(){
	
	$('#qualificationsCloseButton').click(function(){
		$('#qualifications-H2').trigger('click');
		return false;
	});
	
	$('a[name="editQualificationLink"]').click(function(){
		var id = this.id;
		id = id.replace('qualification_', '');
		$('#qualificationId').val($('#'+id+'_qualificationIdDP').val());
		$('#qualificationInstitutionCountry').html($('#'+id+'_qualificationInstitutionCountryDP').val());
		$('#qualificationInstitution').html($('#'+id+'_qualificationInstitutionDP').val());
		$('#qualificationType').html($('#'+id+'_qualificationTypeDP').val());
		$('#qualificationSubject').html($('#'+id+'_qualificationSubjectDP').val());
		$('#qualificationLanguage').html($('#'+id+'_qualificationLanguageDP').val());
		$('#qualificationStartDate').html($('#'+id+'_qualificationStartDateDP').val());
		$('#qualificationCompleted').html($('#'+id+'_qualificationCompletedDP').val());
		$('#qualificationGrade').html($('#'+id+'_qualificationGradeDP').val());
		$('#qualificationAwardDate').html($('#'+id+'_qualificationAwardDateDP').val());
		$("#qualificationProofOfAward").html('<a href="' + $('#'+id+"_qualdocurl").val() + '">' +  $('#'+id+"_qualdocname").val() + '</a>');
	});
	

		//open/close
		var $header  =$('#qualifications-H2');
		var $content = $header.next('div');
		$header.bind('click', function()
		{
		  $content.toggle();
		  $(this).toggleClass('open', $content.is(':visible'));
		  return false;
		});
		
});