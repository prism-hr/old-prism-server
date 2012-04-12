$(document).ready(function(){
	
	$('#additionalCloseButton').click(function(){
		$('#additional-H2').trigger('click');
		return false;
	});
	
	$('#informationSaveButton').click(function(){
		$.post("/pgadmissions/update/editAdditionalInformation", { 
			additionalInformation: $("#additionalInformation").val(),
			applicationId:  $('#applicationId').val()
		},
		
		function(data) {
			$('#additionalInformationSection').html(data);
		});
	});
	
	$('a[name="informationCancelButton"]').click(function(){
		$.get("/pgadmissions/update/getAdditionalInformatio",
				{
					applicationId:  $('#applicationId').val(),
					message: 'cancel'
				},
				function(data) {
					$('#additionalInformationSection').html(data);
				}
		);
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