$(document).ready(function()
{
	$('#dialog-overlay, #dialog-box').hide();

	// if user clicked on button, the overlay layer or the dialogbox, close the dialog	
	$('#withdrawButton').click(function(e)
	{
		e.preventDefault();
		setPopupText("Are you sure you want to withdraw the application? <b>You will not be able to submit a withdrawn application.</b>");
		positionPopup();
		$('#dialog-overlay, #dialog-box').show();
		return false;
	});

	// if user resize the window, call the same function again
	// to make sure the overlay fills the screen and dialogbox aligned to center	
	$(window).resize(function()
	{
		//only do it if the dialog box is not hidden
		if (!$('#dialog-box').is(':hidden'))
		{
			positionPopup();
		}
	});	

	$('a#popup-ok-button').click(function()
	{
		$('#withdrawApplicationForm').submit();
	
		$('#dialog-overlay, #dialog-box').hide();
		return false;
	});
	
	$('a#popup-cancel-button').click(function()
	{
		$('#dialog-overlay, #dialog-box').hide();
		return false;
	});

	$(document).keypress(function(e)
	{
		if ( e.keyCode == 27 )
		{
			$('#dialog-overlay, #dialog-box').hide();		
			return false;
		}
	});

});

//Popup dialog
function setPopupText(message)
{
	// display the message
	$('#dialog-message').html(message);
	$('#dialog-header').html("Please Confirm!");
	positionIcon($('#dialog-message').height());
}

function positionIcon(maxHeight)
{
	var iconMarginTop = 20 + (maxHeight / 2) - 22;
	$('#dialog-icon').css({marginTop:iconMarginTop});
}

function positionPopup()
{
	var offset_x = $('#dialog-box').width() / 2;
	var offset_y = $('#dialog-box').height() / 2;
	
	$('#dialog-box').css({ marginLeft: -offset_x, marginTop: -offset_y })
									.show();
}
