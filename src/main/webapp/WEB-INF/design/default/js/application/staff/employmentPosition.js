$(document).ready(function(){

	$('#positionCloseButton').click(function(){
		$('#position-H2').trigger('click');
		return false;
	});
	

	$('a[name="positionEditButton"]').click(function(){
		var id = this.id;
		id = id.replace('position_', '');
	
		$("#positionId").val($('#'+id+"_positionId").val());
		$("#emp_country").html($('#'+id+"_employerCountry").val());
		$("#emp_name").html($('#'+id+"_employerName").val());
		$("#emp_address").html($('#'+id+"_employerAddress").val());
		$("#emp_position").html($('#'+id+"_positionTitle").val());
		$("#emp_description").html($('#'+id+"_remit").val());
		$("#empl_language").html($('#'+id+"_language").val());
		
		$("#emp_startDate").html($('#'+id+"_positionStartDate").val());
		$("#emp_current").html($('#'+id+"_positionCurrent").val());
		$("#emp_endDate").html($('#'+id+"_positionEndDate").val());
	});


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