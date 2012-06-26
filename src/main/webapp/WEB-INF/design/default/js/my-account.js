$(document).ready(function(){
	
	$('#saveChanges').click(function()
	{
		var postData ={ 
				email : $('#email').val(),
				firstName : $('#firstName').val(),
				lastName : $('#lastName').val(),
				password : $('#currentPassword').val(),
				newPassword : $('#newPassword').val(),
				confirmPassword : $('#confirmNewPass').val()
		};
		
		$('#configuration > div').append('<div class="ajax" />');
		
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
				$('#configuration div.ajax').remove();
				$('#my_account_section').html(data);
			}
		});
	});
	
});
