$(document).ready(function(){
$('#addressSaveButton').click(function(){
		$.post("/pgadmissions/apply/editAddress", { address: $("#address").val(), 
								id: $("#id").val(), 
								appId: $("#appId").val()
								},
				   function(data) {
				     $('#addressSection').html(data);
				   });
	});
});