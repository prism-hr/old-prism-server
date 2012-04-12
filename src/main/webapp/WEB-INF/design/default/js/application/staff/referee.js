$(document).ready(function(){
	
	$('#refereeCloseButton').click(function(){
		$('#referee-H2').trigger('click');
		return false;
	});


	
	
$('a[name="refereeEditButton"]').click(function(){
	alert('click');
	//var id = this.id;
	id = id.replace('referee_', '');
	//alert('id:' +  id);
	$("#refereeId").val($('#'+id+"_refereeId").val());
	$("#ref_firstname").html($('#'+id+"_firstname").val());
	$("#ref_lastname").val($('#'+id+"_lastname").val());
	$("#ref_employer").val($('#'+id+"_jobEmployer").val());
	$("#ref_position").val($('#'+id+"_jobTitle").val());
	$("#ref_messenger").val($('#'+id+"_messenger").val());
	$("#ref_phone").val($('#'+id+"_phone").val());
	$("#ref_address_location").val($('#'+id+"_addressLocation").val());
	
	$("#ref_address_country").val($('#'+id+"_addressCountry").val());
	$("#ref_email").val($('#'+id+"_email").val());
	
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

	var $multiPhone = $('#multi-phone').text();
	//alert("The value is "+ $multiPhone);
	
	if($multiPhone != ""){
		$('#telephone-em').remove();
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