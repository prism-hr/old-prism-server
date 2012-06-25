$(document).ready(function(){
	
	$('#saveChanges').click(function(){
		var postData ={ 
				email : $('#email').val(),
				firstName : $('#firstName').val(),
				lastName : $('#lastName').val(),
				password : $('#currentPassword').val(),
				newPassword : $('#newPassword').val(),
				confirmPassword : $('#confirmNewPass').val()
		};
		$.ajax({
			type: 'POST',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
			url:"/pgadmissions/myAccount/submit", 
			data:$.param(postData),
			success:	function(data) {
					$('#my_account_section').html(data);
				}
		});
	});
	
});
