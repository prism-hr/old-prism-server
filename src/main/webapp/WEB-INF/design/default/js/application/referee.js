$(document).ready(function(){
$('#refereeSaveButton').click(function(){
		$.post("/pgadmissions/update/addReferee", { 
			firstname: $("#position_title").val(),
			lastname: $("#position_startDate").val(), 
			relationship: $("#position_endDate").val(), 
			jobEmployer: $("#position_remit").val(), 
			jobTitle: $("#position_language").val(), 
			addressLocation: $("#position_employer").val(), 
			addressPostcode: $("#position_employer").val(), 
			addressCountry: $("#position_employer").val(), 
			email: $("#position_employer").val(), 
			telephones: $("#telephones").val(), 
			messengers: $("#messengers").val(), 
			appId: $("#appId").val(),
			id: $("#id").val(), 
			refereeId: $("#refereeId").val()
								},
				   function(data) {
				     $('#referencesSection').html(data);
				   });
	});

$('a[name="refereeEditButton"]').click(function(){
	var id = this.id;
	id = id.replace('referee_', '');
	$("#refereeId").val($('#'+id+"_refereeId").val());
	$("#ref_firstname").val($('#'+id+"_firstname").val());
	$("#ref_lastname").val($('#'+id+"_lastname").val());
	$("#ref_relationship").val($('#'+id+"_relationship").val());
	$("#ref_employer").val($('#'+id+"_jobEmployer").val());
	$("#ref_position").val($('#'+id+"_jobTitle").val());
	$("#ref_address_location").val($('#'+id+"_addressLocation").val());
	$("#ref_address_postcode").val($('#'+id+"_addressPostcode").val());
	$("#ref_address_country").val($('#'+id+"_addressCountry").val());
	$("#ref_email").val($('#'+id+"_email").val());
	$("#telephones").val($('#'+id+"_messengers").val());
	$("#messengers").val($('#'+id+"_telephones").val());
});

});