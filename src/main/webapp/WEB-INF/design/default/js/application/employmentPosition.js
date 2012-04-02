$(document).ready(function(){

	$("#completedPositionCB").attr('checked', false);
	$("#completedPosition").val("NO");
	
	$("input[name*='completedPositionCB']").click(function() {
		if ($("#completedPosition").val() =='YES'){
			$("#completedPosition").val("NO");
			$("#endDateField").html("<input class=\"half date\" type=\"text\" id=\"position_endDate\" name=\"position_endDate\" value=\"\" disabled=\"disabled\"</input>");
		} else {		
			$("#completedPosition").val("YES");
			$("#endDateField").html("<input class=\"half date\" type=\"text\" id=\"position_endDate\" name=\"position_endDate\" value=\"\"</input>	");
			bindDatePickers();
		}
	});
	
	
	$('#positionCloseButton').click(function(){
		$('#position-H2').trigger('click');
		return false;
	});
	
	$('a[name="deleteButton"]').click( function(){	
		$(this).parent("form").submit();
	});
	
	$('#positionSaveAndCloseButton').click(function(){
		$.post("/pgadmissions/update/addEmploymentPosition", { 
			position_title: $("#position_title").val(),
			position_startDate: $("#position_startDate").val(), 
			position_endDate: $("#position_endDate").val(), 
			position_remit: $("#position_remit").val(), 
			position_language: $("#position_language").val(), 
			position_employer: $("#position_employer").val(), 
			completed: $("#completedPosition").val(),
			appId: $("#appId").val(),
			id: $("#id").val(), 
			positionId: $("#positionId").val()
								},
				   function(data) {
				     $('#positionSection').html(data);
				   });
	});

$('#positionSaveAndAddButton').click(function(){
	$.post("/pgadmissions/update/addEmploymentPosition", { 
		position_title: $("#position_title").val(),
		position_startDate: $("#position_startDate").val(), 
		position_endDate: $("#position_endDate").val(), 
		position_remit: $("#position_remit").val(), 
		position_language: $("#position_language").val(), 
		position_employer: $("#position_employer").val(),
		completed: $("#completedPosition").val(),
		appId: $("#appId").val(),
		id: $("#id").val(), 
		positionId: $("#positionId").val(),
		add: "add"
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
	if ($('#'+id+'_positionCompleted').val() =='YES'){
		$("#completedPositionCB").attr('checked', true);
	} else {
		$("#completedPositionCB").attr('checked', false);
	}
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
		$(this).html("");
	});
	
});


bindDatePickers();

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