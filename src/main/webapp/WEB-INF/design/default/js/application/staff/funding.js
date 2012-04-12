$(document).ready(function(){
	
	$('#fundingCloseButton').click(function(){
		$('#funding-H2').trigger('click');
		return false;
	});
	



	$('a[name="fundingEditButton"]').click(function(){
		var id = this.id;
		id = id.replace('funding_', '');
		$("#fundingId").val($('#'+id+"_fundingIdDP").val());
		$("#fundingType").html($('#'+id+"_fundingTypeDP").val());
		$("#fundingValue").html($('#'+id+"_fundingValueDP").val());
		$("#fundingDescription").html($('#'+id+"_fundingDescriptionDP").val());
		$("#fundingAwardDate").html($('#'+id+"_fundingAwardDateDP").val());
		$("#proofOfAward").html('<a href="' + $('#'+id+"_docurl").val() + '">' +  $('#'+id+"_docname").val() + '</a>');
	});
	


		//open/close
		var $header  =$('#funding-H2');
		var $content = $header.next('div');
		$header.bind('click', function()
		{
		  $content.toggle();
		  $(this).toggleClass('open', $content.is(':visible'));
		  return false;
		});
		
});