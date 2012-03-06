$(document).ready(function(){
$('#fundingSaveButton').click(function(){
		$.post("/pgadmissions/update/addFunding", { fundingType: $("#fundingType").val(),
								fundingDescription: $("#fundingDescription").val(), 
								fundingValue: $("#fundingValue").val(), 
								fundingAwardDate: $("#fundingAwardDate").val(), 
								id: $("#id").val(), 
								appId: $("#appId").val()
								},
				   function(data) {
				     $('#fundingSection').html(data);
				   });
	});
});