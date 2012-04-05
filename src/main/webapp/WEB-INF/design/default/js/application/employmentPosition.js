$(document).ready(function(){


	$('#current').click(function() {
		if ($('#current:checked').val() !== undefined) {
			$('#position_endDate').attr("disabled", "disabled");
		}else{
			$('#position_endDate').removeAttr("disabled");
		}
	});
	
	
	$('#positionCloseButton').click(function(){
		$('#position-H2').trigger('click');
		return false;
	});
	
	$('a[name="deleteEmploymentButton"]').click( function(){	
		var id = $(this).attr("id").replace("position_", "");
		$.post("/pgadmissions/deleteentity/employment",
				{
					id: id	
				}, 				
				function(data) {
					$('#positionSection').html(data);
				}	
				
			);
	});
	
	$('#positionSaveAndCloseButton').click(function(){
		postData('close');
	});

	$('#positionSaveAndAddButton').click(function(){
		postData('add');
	});

	$('a[name="positionEditButton"]').click(function(){
		var id = this.id;
		id = id.replace('position_', '');	
		$.get("/pgadmissions/update/getEmploymentPosition",
				{
					applicationId:  $('#applicationId').val(), 
					employmentId: id
				},
				function(data) {
					$('#positionSection').html(data);
				}
		);
	});

	$('a[name="positionCancelButton"]').click(function(){
		$("#positionId").val("");
		$("#position_employer").val("");
		$("#position_remit").val("");
		$("#position_language").val("");
		$("#position_title").val("");
		$("#position_startDate").val("");
		$("#position_endDate").val("");
		$("#completedPositionCB").attr('checked', false);
		$("span[class='invalid']").each(function(){
			$(this).hide();
		});
		
	});


	//bindDatePickers();
	
	//open/close
	var $header  =$('#position-H2');
	var $content = $header.next('div');
	$header.bind('click', function()
	{
	  $content.toggle();
	  $(this).toggleClass('open', $content.is(':visible'));
	  return false;
	});


});

function postData(message){
	var current = false;
	if ($('#current:checked').val() !== undefined) {
		 current = true;
	}
	$.post("/pgadmissions/update/editEmploymentPosition",
	{ 
		position: $("#position_title").val(),
		startDate: $("#position_startDate").val(), 
		endDate: $("#position_endDate").val(), 
		remit: $("#position_remit").val(), 
		language: $("#position_language").val(), 
		employerCountry: $("#position_country").val(),
		employerName: $("#position_employer_name").val(),
		employerAddress: $("#position_employer_address").val(),
		current: current,
		application: $("#appId").val(),
		applicationId: $("#appId").val(),		 
		employmentId: $("#positionId").val(), 
		message:message
	},
   function(data) {
     $('#positionSection').html(data);
   });
}