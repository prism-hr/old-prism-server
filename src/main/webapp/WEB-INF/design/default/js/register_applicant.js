$(document).ready(function()
{
  
	// Clear form fields on focus
	var el = $('input[type=text], input[type=password]');
	var cssObjBefore = {'color': '#808080' }
	var cssObjAfter = {'color': '#000' }
	
	el.each(function(){
		$(this).css(cssObjBefore);
	});
	
	el.focus(function(e) {
		if (e.target.value == e.target.defaultValue)
			e.target.value = '';
		$(this).css(cssObjAfter);
	});
	el.blur(function(e) {
		if (e.target.value == '')
			e.target.value = e.target.defaultValue;
	});


	$('#registration-box button').click(function()
	{
		var field;
		field = $('#firstName')[0];
		var firstNameValue = field.value;
		var firstNameString = 'First Name';
		if (firstNameValue == firstNameString)
		{
			field.value = '';
		}
		field = $('#lastName')[0];
		var lastNameValue = field.value;
		var lastNameString = 'Last Name';
		if (lastNameValue == lastNameString)
		{
			field.value = '';
		}
		$('#registration-box').append('<div class="ajax" />');
		return true;
	});
  

	// Resend confirmation email button.
	$('#resend').click(function()
	{
		var url = $(this).attr('href');
		$('#site-message').append('<div class="ajax" />');
		window.location.href = url;
	});
  
});