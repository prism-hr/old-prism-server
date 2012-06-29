$(document).ready(function(){	

	getAccountDetailsSection();
	
	// Submit button.
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
				$('#accountdetails').html(data);
				addToolTips();
			},
      complete: function()
      {
				$('#accountdetails div.ajax').remove();
      }
		});
	});
	
	// Cancel button.
	$('#accountdetails').on('click', "#cancelMyACnt", function()
	{
		var $form = $(this).closest('form');
		clearForm($form);
	});
	
});

function getAccountDetailsSection()
{
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
			$('#accountdetails').html(data);
			addToolTips();
		},
    complete: function()
    {
			$('#reviewsecion div.ajax').remove();
    }
	});
	
}
