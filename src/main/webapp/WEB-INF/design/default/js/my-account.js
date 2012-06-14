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
		$.post("/pgadmissions/myAccount/submit", 
				$.param(postData),
				function(data) {
					$('#my_account_section').html(data);
				}
			);
	});
	
});
