$(document).ready(function()
{
	
	// Clear form fields on focus
	var el = $('input[type=text], input[type=password]');
	var cssObjBefore = {'color': '#808080' };
	var cssObjAfter = {'color': '#000' };
	
	el.each(function(){
	    if (!$(this).is('[readonly]')) {
	        $(this).css(cssObjBefore);
	    }
	});
	
	el.focus(function(e) {
	    if (!$(this).is('[readonly]')) {
    		if (e.target.value == e.target.defaultValue)
    			e.target.value = '';
    		$(this).css(cssObjAfter);
	    }
	});
	el.blur(function(e) {
	    if (!$(this).is('[readonly]')) {
    		if (e.target.value == '')
    			e.target.value = e.target.defaultValue;
	    }
	}); 
	
	
	$('#login-box button').click(function()
	{
		$('#login-box').addClass('loading');
		return true;
	});
  
});