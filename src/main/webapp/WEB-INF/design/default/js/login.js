$(document).ready(function()
{
  
	// Clear form fields on focus
	var el = $('input[type=text], input[type=password]');
	
	el.each(function(){
		var cssObj = {'color': '#808080' }
		$(this).css(cssObj);
	});
	
	el.focus(function(e) {
		if (e.target.value == e.target.defaultValue)
			e.target.value = '';
	});
	el.blur(function(e) {
		if (e.target.value == '')
			e.target.value = e.target.defaultValue;
	}); 

  
});