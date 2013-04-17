$(document).ready(function()
{
	// Clear form fields on focus
	var el = $('input[type=text], input[type=password]');
	var cssObjBefore = {'color': '#808080' };
	var cssObjAfter = {'color': '#000' };
	
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
		$('#registration-box').append('<div class="ajax" />');
		return true;
	});

});