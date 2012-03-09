$(document).ready(function(){
$('#positionSaveButton').click(function(){
		$.post("/pgadmissions/update/addEmploymentPosition", { 
			position_title: $("#position_title").val(),
			position_startDate: $("#position_startDate").val(), 
			position_endDate: $("#position_endDate").val(), 
			position_remit: $("#position_remit").val(), 
			position_language: $("#position_language").val(), 
			position_employer: $("#position_employer").val(), 
			appId: $("#appId").val(),
			id: $("#id").val(), 
			positionId: $("#positionId").val()
								},
				   function(data) {
				     $('#positionSection').html(data);
				   });
	});

$('a[name="positionEditButton"]').click(function(){
	var id = this.id;
	id = id.replace('position_', '');
	$("#positionId").val($('#'+id+"_positionId").val());
	$("#position_employer").val($('#'+id+"_employer").val());
	$("#position_remit").val($('#'+id+"_remit").val());
	$("#position_language").val($('#'+id+"_language").val());
	$("#position_title").val($('#'+id+"_positionTitle").val());
	$("#position_startDate").val($('#'+id+"_positionStartDate").val());
	$("#position_endDate").val($('#'+id+"_positionEndDate").val());
});

});