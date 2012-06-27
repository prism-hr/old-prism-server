$(document).ready(function(){	

	getAccountDetailsSection();
	
	$('#accountdetails').on('click', "#saveChanges", function()
	{
		var postData ={ 
				email : $('#email').val(),
				firstName : $('#firstName').val(),
				lastName : $('#lastName').val(),
				password : $('#currentPassword').val(),
				newPassword : $('#newPassword').val(),
				confirmPassword : $('#confirmNewPass').val()
		};
		
		$('#accountdetails > div').append('<div class="ajax" />');
		
		
		$.ajax({
			type: 'POST',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
			url:"/pgadmissions/myAccount/submit", 
			data:$.param(postData),
			success: function(data)
			{
				$('#accountdetails div.ajax').remove();
				$('#accountdetails').html(data);
			}
		});
	});
	
	$('#accountdetails').on('click', "#cancelMyACnt", getAccountDetailsSection);
	
});

function getAccountDetailsSection(){
	$('#reviewsecion').append('<div class="ajax" />');
	
	$.ajax({
		type: 'GET',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  }
		  },
		url:"/pgadmissions/myAccount/section", 
		success: function(data)
		{
			$('#reviewsecion div.ajax').remove();
			$('#accountdetails').html(data);
		}
	});
	
}
