$(document).ready(function(){
$('#fundingSaveButton').click(function(){
		$.post("/pgadmissions/update/addFunding", { funding: $("#funding").val(), 
								id: $("#id").val(), 
								appId: $("#appId").val()
								},
				   function(data) {
				     $('#fundingSection').html(data);
				   });
	});
});