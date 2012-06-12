$(document).ready(function(){
	
	$('#saveChanges').click(function(){
		var postData ={ 
				email : $('#email').val()
		};
		if ($('#currentPassword').val() !== '' && $('#newPassword').val().trim !== '' && $('#confirmNewPass').val() !== '') {
			postData.password = $('#currentPassword').val();
			postData.newPassword = $('#newPassword').val();
			postData.confirmPassword = $('#confirmNewPass').val();
		}
		$.post("/pgadmissions/myAccount/submit", 
				$.param(postData),
				function(data) {
					$('#my_account_section').html(data);
				}
			);
	});
	
});
