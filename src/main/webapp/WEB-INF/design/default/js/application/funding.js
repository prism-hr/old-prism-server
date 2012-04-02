$(document).ready(function(){
	
	$('#fundingCloseButton').click(function(){
		$('#funding-H2').trigger('click');
		return false;
	});
	
	$('a[name="deleteButton"]').click( function(){	
		$(this).parent("form").submit();
	});
	
	$('#fundingSaveCloseButton').click(function(){
		$.post("/pgadmissions/updateFunding", { 
			fundingType: $("#fundingType").val(),
			fundingDescription: $("#fundingDescription").val(), 
			fundingValue: $("#fundingValue").val(), 
			fundingAwardDate: $("#fundingAwardDate").val(), 
			id: $("#id").val(), 
			appId: $("#appId").val(),
			fundingId: $("#fundingId").val()
		},
		
		function(data) {
			$('#fundingSection').html(data);
		});
	});
	
	$('#fundingSaveAddButton').click(function(){
		$.post("/pgadmissions/updateFunding", { 
			fundingType: $("#fundingType").val(),
			fundingDescription: $("#fundingDescription").val(), 
			fundingValue: $("#fundingValue").val(), 
			fundingAwardDate: $("#fundingAwardDate").val(), 
			id: $("#id").val(), 
			appId: $("#appId").val(),
			fundingId: $("#fundingId").val(),
			add:"add"
		},
		
		function(data) {
			$('#fundingSection').html(data);
		});
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