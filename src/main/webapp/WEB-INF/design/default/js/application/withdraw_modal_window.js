$(document).ready(function()
{
	$('#dialog-overlay, #dialog-box').hide();

	// if user clicked on button, the overlay layer or the dialogbox, close the dialog	
	$('#withdrawButton').click(function(e)
	{
		e.preventDefault();
		setPopupText("Are you sure you want to withdraw the application? You will not be able to submit a withdrawn application.");
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
		//alert("you have pressed ok - enter");
		$.post("/pgadmissions/withdraw",
			{ applicationId:  $('#wapplicationFormId').val() }, 
			function(data) {}
		);
	
		$('#dialog-overlay, #dialog-box').hide();
		//alert("you have pressed ok - exit");
		return false;
	});
	
	$('a#popup-cancel-button').click(function()
	{
		//alert("you have pressed cancel - enter");
		$('#dialog-overlay, #dialog-box').hide();
		//alert("you have pressed cancel - exite");
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
	/*
	// get the screen height and width  
	var maskHeight = $(document).height();
	var maskWidth = $(document).width();
	
	// calculate the values for center alignment
	var dialogTop =  (maskHeight/2) - ($('#dialog-box').height()) + 100; 
	
	if(dialogTop <= 0)
	{
		dialogTop = (maskHeight - $('#dialog-box').height()) / 2;
		if(dialogTop < 0)
		{
			dialogTop = 0;
			maskHeight = $('#dialog-box').height();
		}
	}

	var dialogLeft = (maskWidth/2) - ($('#dialog-box').width()/2); 

	// assign values to the overlay and dialog box
	$('#dialog-overlay').css({height:maskHeight, width:maskWidth}).show();
	$('#dialog-box').css({top:dialogTop, left:dialogLeft}).show();
	*/
}
