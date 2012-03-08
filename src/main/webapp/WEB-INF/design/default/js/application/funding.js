$(document).ready(function(){
	$('#fundingSaveCloseButton').click(function(){
		$.post("/pgadmissions/update/addFunding", { 
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
		$.post("/pgadmissions/update/addFunding", { 
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

	$('a[name="fundingEditButton"]').click(function(){
		var id = this.id;
		id = id.replace('funding_', '');
		$("#fundingId").val($('#'+id+"_fundingIdDP").val());
		$("#fundingType").val($('#'+id+"_fundingTypeDP").val());
		$("#fundingValue").val($('#'+id+"_fundingValueDP").val());
		$("#fundingDescription").val($('#'+id+"_fundingDescriptionDP").val());
		$("#fundingAwardDate").val($('#'+id+"_fundingAwardDateDP").val());
	});
});