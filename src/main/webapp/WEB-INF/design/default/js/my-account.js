$(document).ready(function(){
	
	$('#saveChanges').click(function(){
		var postData ={ 
				id : $('#userId').val(),
				email : $('#email').val(),
				firstName : $('#firstname').val(),
				lastName : $('#lastname').val(),
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
