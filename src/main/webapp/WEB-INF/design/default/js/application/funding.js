$(document).ready(function(){
	
	$('#fundingCloseButton').click(function(){
		$('#funding-H2').trigger('click');
		return false;
	});
	
	$('a[name="deleteButton"]').click( function(){	
		$(this).parent("form").submit();
	});
	
	$('#fundingSaveCloseButton').click(function(){
		$("#shouldAdd").val("");
	});
	
	$('#fundingSaveAddButton').click(function(){
		$("#isFundingAdd").val("add");
	});

	$('a[name="fundingEditButton"]').click(function(){
		var id = this.id;
		id = id.replace('funding_', '');
		$("#fundingId").val($('#'+id+"_fundingIdDP").val());
		$("#fundingType").val($('#'+id+"_fundingTypeDP").val());
		$("#fundingValue").val($('#'+id+"_fundingValueDP").val());
		$("#fundingDescription").val($('#'+id+"_fundingDescriptionDP").val());
		$("#fundingAwardDate").val($('#'+id+"_fundingAwardDateDP").val());
	});
	
	$('a[name="fundingCancelButton"]').click(function(){
		$("#fundingId").val("");
		$("#fundingType").val("");
		$("#fundingValue").val("");
		$("#fundingDescription").val("");
		$("#fundingAwardDate").val("");
		$("span[class='invalid']").each(function(){
			$(this).html("");
		});
		
	});
	
	  bindDatePickers();

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