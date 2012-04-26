$(document).ready(function(){
	
	$('#additionalCloseButton').click(function(){
		$('#additional-H2').trigger('click');
		return false;
	});
	
	$('#informationSaveButton').click(function(){
		$.post("/pgadmissions/update/addAdditionalInformation", { 
			additionalInformation: $("#additionalInformation").val(),
			id: $("#id").val(), 
			appId: $("#appId").val()
		},
		
		function(data) {
			$('#additionalInformationSection').html(data);
		});
	});
	
	$('a[name="informationCancelButton"]').click(function(){
		$("#additionalInformation").val("");
	});

	//open/close
	var $header  =$('#additional-H2');
	var $content = $header.next('div');
	$header.bind('click', function()
	{
	  $content.toggle();
	  $(this).toggleClass('open', $content.is(':visible'));
	  return false;
	});
	
});