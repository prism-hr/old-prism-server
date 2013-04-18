$(document).ready(function()
{
    $('#firstName_tooltip_input').qtip({
        content: {
            text: function(api) {
                return $('#firstName_tooltip_input').attr('data-desc');
            } 
        },
        position: {
            my: 'bottom right', // Use the corner...
            at: 'top center', // ...and opposite corner
            viewport: $(window),
            adjust: {
                method: 'flip shift'
            },
            target: $("#firstName_tooltip_input")
        },
        show: {
            solo: true
        },
        style: 'tooltip-pgr ui-tooltip-shadow'
    });
    
    $('#lastName_tooltip_input').qtip({
        content: {
            text: function(api) {
                return $('#lastName_tooltip_input').attr('data-desc');
            } 
        },
        position: {
            my: 'bottom right', // Use the corner...
            at: 'top center', // ...and opposite corner
            viewport: $(window),
            adjust: {
                method: 'flip shift'
            },
            target: $("#lastName_tooltip_input")
        },
        show: {
            solo: true
        },
        style: 'tooltip-pgr ui-tooltip-shadow'
    });
    
    $('#email_tooltip_input').qtip({
        content: {
            text: function(api) {
                return $('#email_tooltip_input').attr('data-desc');
            } 
        },
        position: {
            my: 'bottom right', // Use the corner...
            at: 'top center', // ...and opposite corner
            viewport: $(window),
            adjust: {
                method: 'flip shift'
            },
            target: $("#email_tooltip_input")
        },
        show: {
            solo: true
        },
        style: 'tooltip-pgr ui-tooltip-shadow'
    });
    
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