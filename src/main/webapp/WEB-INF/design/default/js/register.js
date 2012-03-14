$(document).ready(function(){
	$('#registerSubmit').click(function(){
		$.post("/pgadmissions/register/submit", { 
			firstname: $("#rec_firstname").val(),
			lastname: $("#rec_lastname").val(), 
			email: $("#rec_email").val(), 
			password: $("#rec_password").val(),
			confirmPassword: $("#rec_password_confirm").val()
		});
	});
});
	
