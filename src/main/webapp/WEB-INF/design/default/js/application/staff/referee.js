$(document).ready(function(){
	
	$('#refereeCloseButton').click(function(){
		$('#referee-H2').trigger('click');
		return false;
	});


	
	
$('a[name="refereeEditButton"]').click(function(){

	var id = this.id;
	id = id.replace('referee_', '');
	$('#ajaxloader').show();

	$("#refereeId").val($('#'+id+"_refereeId").val());
	$("#ref_firstname").html($('#'+id+"_firstname").val());
	$("#ref_lastname").html($('#'+id+"_lastname").val());
	$("#ref_employer").html($('#'+id+"_jobEmployer").val());
	$("#ref_position").html($('#'+id+"_jobTitle").val());
	$("#ref_messenger").html($('#'+id+"_messenger").val() + "&nbsp;");
	$("#ref_phone").html($('#'+id+"_phone").val());
	$("#ref_address_location").html($('#'+id+"_addressLocation").val());
	
	$("#ref_address_country").html($('#'+id+"_addressCountry").val());
	$("#ref_email").html($('#'+id+"_email").val());
	
	if($("#referenceUpdated")){
		$("#referenceUpdated").html($('#'+id+"_lastUpdated").val());
	}

	if($("#referenceDocument")){
		if($('#'+id+"_reference_document_url").val()){
			$("#referenceDocument").html("<a href='" + $('#'+id+"_reference_document_url").val() + "'>" + $('#'+id+"_reference_document_name").val() +  "</a>");
		}else{
			$("#referenceDocument").html( $('#'+id+"_reference_document_name").val());
		}
	}
	
});


//open/close
var $header  =$('#referee-H2');
var $content = $header.next('div');
$header.bind('click', function()
{
  $content.toggle();
  $(this).toggleClass('open', $content.is(':visible'));
  return false;
});

});