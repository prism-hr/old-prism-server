$(document).ready(function(){
	
	limitTextArea();
	
	$('#refereeCloseButton').click(function(){
		$('#referee-H2').trigger('click');
		return false;
	});
	
	$('a[name="deleteRefereeButton"]').click( function(){	
				var id = $(this).attr("id").replace("referee_", "");
				$.post("/pgadmissions/deleteentity/referee",
				{
					id: id	
				}, 
				
				function(data) {
					$('#referencesSection').html(data);
				}	
					
			);
	});
	

	

	
	$('#refereeSaveAndCloseButton').click(function(){	
		postRefereeData("close");
	});
	$('#addReferenceButton').click(function(){
		postRefereeData("add");
		
	});


	
	$('a[name="refereeCancelButton"]').click(function(){
		$.get("/pgadmissions/update/getReferee",
				{
					applicationId:  $('#applicationId').val(),
					message: 'cancel',					
					cacheBreaker: new Date().getTime()
				},
				function(data) {
					$('#referencesSection').html(data);
				}
		);
	});
	
	$('a[name="editRefereeLink"]').click(function(){
		var id = this.id;
		id = id.replace('referee_', '');	
		$.get("/pgadmissions/update/getReferee",
				{
					applicationId:  $('#applicationId').val(),
					refereeId: id,
					message: 'edit',					
					cacheBreaker: new Date().getTime()
				},
				function(data) {
					$('#referencesSection').html(data);
				}
		);
	});
	
	addToolTips();
	
	// To make uncompleted functionalities disable.
	$(".disabledEle").attr("disabled", "disabled");
	
	//open/close
	var $header  =$('#referee-H2');
	var $content = $header.next('div');
	$header.bind('click', function()
	{
	  $content.toggle();
	  $(this).toggleClass('open', $content.is(':visible'));
	  return false;
	});

});

function postRefereeData(messsage){
	
	var postData ={ 
			firstname: $("#ref_firstname").val(),
			lastname: $("#ref_lastname").val(), 
			jobEmployer: $("#ref_employer").val(), 
			jobTitle: $("#ref_position").val(), 
			addressLocation: $("#ref_address_location").val(), 		
			messenger: $("#ref_messenger").val(), 
			addressCountry: $("#ref_address_country").val(), 
			email: $("#ref_email").val(),			
			applicationId:  $('#applicationId').val(),
			application:  $('#applicationId').val(),	
			refereeId: $("#refereeId").val(),
			phoneNumber: $("#refPhoneNumber").val(),
			message:messsage
	}
	$.post( "/pgadmissions/update/editReferee" , $.param(postData),			
			function(data) {
				$('#referencesSection').html(data);
			}
	);
}