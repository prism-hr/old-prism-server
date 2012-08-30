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
				  },
				  500: function() {
					  window.location.href = "/pgadmissions/error";
				  },
				  404: function() {
					  window.location.href = "/pgadmissions/404";
				  },
				  400: function() {
					  window.location.href = "/pgadmissions/400";
				  },				  
				  403: function() {
					  window.location.href = "/pgadmissions/404";
				  }
			  },
			url:"/pgadmissions/myAccount/submit", 
			data:$.param(postData),
			success: function(data)
			{
				if(data == "OK"){	
					window.location.href = '/pgadmissions/myAccount?messageCode=account.updated';
				}else{				
					$('#accountdetails').html(data);
					addToolTips();
				}
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
	$('.content-box-inner').append('<div class="ajax" />');
	var url = "/pgadmissions/myAccount/section";	
	if($('#messageCode').val() != ''){
		url = url + "?messageCode=" + $('#messageCode').val();
	}
	$.ajax({
		type: 'GET',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  },
			  500: function() {
				  window.location.href = "/pgadmissions/error";
			  },
			  404: function() {
				  window.location.href = "/pgadmissions/404";
			  },
			  400: function() {
				  window.location.href = "/pgadmissions/400";
			  },				  
			  403: function() {
				  window.location.href = "/pgadmissions/404";
			  }
		  },
		url:url,
		
		success: function(data)
		{
			$('#accountdetails').html(data);
			addToolTips();
		},
    complete: function()
    {
			$('.content-box-inner div.ajax').remove();
    }
	});
	
}
