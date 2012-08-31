$(document).ready(function()
{

	// Tooltip settings used across the board.
	var tooltipSettings = {
		content: {
			text: function(api)
			{
				// Retrieve content from custom attribute of the $('.selector') elements.
				return $(this).attr('data-desc');
			} 
		},
		position: {
			my: 'bottom right', // Use the corner...
			at: 'top center', // ...and opposite corner
			viewport: $(window),
			adjust: {
				method: 'flip shift'
			}
		},
		style: 'tooltip-pgr ui-tooltip-shadow'
	};


	// ------------------------------------------------------------------------------
	// Apply a class to the BODY tag if we're in "beta".
	// ------------------------------------------------------------------------------
	var now = new Date();
	var currentYear = now.getFullYear();
	if (currentYear == '2012')
	{
		$('body').addClass('beta');	
	}

	// ------------------------------------------------------------------------------
	// On the application form, show "Not Provided" text in grey.
	// ------------------------------------------------------------------------------
	$('.field').each(function()
	{
		var strValue = $(this).text();
		if (strValue.match("Not Provided"))
		{
			$(this).toggleClass('grey-label');
		}
	});
  
	// ------------------------------------------------------------------------------
	// Add tooltips!
	// ------------------------------------------------------------------------------
	fn = window['addToolTips'];
	if(typeof fn == 'function')
	{
		addToolTips();
	}
  
	// ------------------------------------------------------------------------------
	// Initialise date picker controls.
	// ------------------------------------------------------------------------------
	fn = window['bindDatePickers'];
	if(typeof fn == 'function')
	{
		bindDatePickers();
	}
  
  // ------------------------------------------------------------------------------
  // Toolbar button action for jumping to a specific part of the application form.
  // ------------------------------------------------------------------------------
  $('.tool-button a').click(function()
  {
    var buttonId  = $(this).parent().attr("id");
    var sectionId = "";
    
    switch(buttonId)
    {
      case "tool-programme":
        sectionId = "programme-H2";
        break;
      case "tool-personal":
        sectionId = "personalDetails-H2";
        break;
      case "tool-funding":
        sectionId = "funding-H2";
        break;
      case "tool-employment":
        sectionId = "position-H2";
        break;
      case "tool-address":
        sectionId = "address-H2";
        break;
      case "tool-information":
        sectionId = "additional-H2";
        break;
      case "tool-qualification":
        sectionId = "qualifications-H2";
        break;
      case "tool-documents":
        sectionId = "documents-H2";
        break;
      case "tool-references":
        sectionId = "referee-H2";
        break;
      default:
        return false;
    }
    
		// Close the other sections and jump to the selected section.
    $('section.folding h2').each(function()
    {
      if (sectionId != $(this).attr('id'))
      {
        $(this).removeClass('open');
        $(this).next('div').hide();
      }
      else
      {
        $(this).addClass('open');
        $(this).next('div').show();
      }
    });
    
    $.scrollTo('#'+sectionId, 1000);
    return false;
  });


  // ------------------------------------------------------------------------------
  // Opening and closing each section with a "sliding" animation.
  // ------------------------------------------------------------------------------
  $(document).on('click', 'section.folding h2', function()
  {
    var $header = $(this);
    var $content = $header.next('div');
    
    if ($content.not(':animated'))
    {
      var state = $content.is(':visible');
      if (state)
      {
        $header.removeClass('open');
        $content.slideUp(800);
      }
      else
      {
        $header.addClass('open');
        $content.slideDown(800);
      }
    }
  });
  

  // ------------------------------------------------------------------------------
  // Generic clear button functionality for forms, which empties all form fields.
  // ------------------------------------------------------------------------------
	/*
	$(document).on('click', 'button.clear', function()
	{
		var $form = $(this).closest('form');
		if ($form.length > 0)
		{
			clearForm($form)
		}
		else
		{
			alert('clear button is not inside a FORM!');
		}
	});
	*/
	
});


// ------------------------------------------------------------------------------
// Back to top functionality, project manager style.
// ------------------------------------------------------------------------------
function backToTop()
{
  $.scrollTo('#wrapper', 900);
}


// ------------------------------------------------------------------------------
// Generating a URL for feedback buttons.
// ------------------------------------------------------------------------------
function makeFeedbackButton()
{
	var pathname = window.location.pathname;
	var linkToFeedback = "https://docs.google.com/spreadsheet/viewform?formkey=dDNPWWt4MTJ2TzBTTzQzdUx6MlpvWVE6MQ"
		+"&entry_2="+pathname+"&entry_3="+$("#userRolesDP").val()+"&entry_4="+$("#userFirstNameDP").val()+"&entry_5="+$("#userLastNameDP").val()
		+"&entry_6="+$("#userEmailDP").val();
		
	$("#feedbackButton").attr('href', linkToFeedback);
}


// ------------------------------------------------------------------------------
// Initialise jQuery's date picker on specified fields.
// ------------------------------------------------------------------------------
function bindDatePicker(selector)
{
	$(selector).each(function()
	{
		if (!$(this).hasClass('hasDatepicker'))
		{
			$(this).attr("readonly", "readonly");	
			$(this).datepicker({
				dateFormat: 'dd M yy',
				changeMonth: true,
				changeYear: true,
				yearRange: '1900:c+20' });
		}
	});
}


// ------------------------------------------------------------------------------
// Restrict TEXTAREA fields to a certain number of characters.
// ------------------------------------------------------------------------------
function limitTextArea()
{
	$('textarea[maxlength]').keyup(function()
	{  
		//get the limit from maxlength attribute  
		var limit = parseInt($(this).attr('maxlength'));  
		//get the current text inside the textarea  
		var text = $(this).val();  
		//count the number of characters in the text  
		var chars = text.length;  

		//check if there are more characters than allowed  
		if (chars > limit)
		{  
			//and if there are use substr to get the text before the limit  
			var new_text = text.substr(0, limit);  

			//and change the current text with the new text  
			$(this).val(new_text);
		}
		else if (chars == limit)
		{
			//alert('You have entered the maximum allowed characters for this field: '+ limit);
			return false;
		}  
	});
}


// ------------------------------------------------------------------------------
// Adding tooltips to items with a data-desc attribute.
// ------------------------------------------------------------------------------
function addToolTips()
{
	$('*[data-desc]').qtip(tooltipSettings);
}


// ------------------------------------------------------------------------------
// Immediately display a tooltip.
// ------------------------------------------------------------------------------
function fixedTip($object, text)
{
	// remove any existing tooltips.
	var existing = $object.attr('aria-describedby');
	if (existing)
	{
		$('#' + existing).remove();
	}
	$object.removeAttr('aria-describedby');
	
	var tooltipSettings = {
		content: {
			text: function(api)
			{
				return text;
			} 
		},
		position: {
			my: 'bottom right', // Use the corner...
			at: 'top center', // ...and opposite corner
			viewport: $(window),
			adjust: {
				method: 'flip shift'
			}
		},
		style: 'tooltip-pgr ui-tooltip-shadow',
		show: {
			ready: true
		}
	};

	$object.removeData('qtip')
	       .qtip(tooltipSettings);
}


// ------------------------------------------------------------------------------
// Set up a div.field container with a file INPUT field for AJAX uploading.
// ------------------------------------------------------------------------------
function watchUpload($field, $deleteFunction)
{
	var $container  = $field.parent('div.field');
	
	/* Delete button functionality. */
	$container.on('click', '.button-delete', function()
	{
		var $hidden  = $container.find('input.file');
		if (!$deleteFunction)
		{
			deleteUploadedFile($hidden);
		}
		else
		{
			$deleteFunction();
		}
	
		$container.find('span a').each(function()
		{
			$(this).remove();
		});
	
	
		$hidden.val(''); // clear field value.
		$container.removeClass('uploaded');
	
		// Replace the file field with a fresh copy (that's the only way we can set its value to empty).
		var id		= $field.attr('id');
		var ref		= $field.attr('data-reference');
		var type	= $field.attr('data-type');
		$field.replaceWith('<input class="full" type="file" name="file" value="" id="' + id +'" data-reference="' + ref + '" data-type="' + type + '" />');
		watchUpload($('#'+id));
	});

	$container.on('change', $field, function()
	{
		var input    = this.children[0];
		var $hidden  = $container.find('input.file');
		var isOkSize = true;
		try
		{
			isOkSize = input.files[0].size < 10485760;
		}
		catch(error)
		{
			alert(error);
		}
		if (isOkSize) // 10MB in bytes
		{
			if (!$deleteFunction)
			{
				deleteUploadedFile($hidden);
			}
			else
			{
				$deleteFunction();
			}
			$field.attr("readonly", "readonly");
			$container.addClass('posting');
			doUpload($(input));
			$field.removeAttr("readonly");
		}
		else
		{
			$container.append('<span class="invalid">Document must be at most 10MB.</span>');
		}
	});
}


// ------------------------------------------------------------------------------
// Delete an uploaded file referenced by a hidden field.
// ------------------------------------------------------------------------------
function deleteUploadedFile($hidden_field)
{
	if ($hidden_field && $hidden_field.val() != '')
	{
		$.ajax({
			type: 'POST',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
			url:"/pgadmissions/delete/asyncdelete",
			data: { documentId: $hidden_field.val() }
			
			
		});
	}
}


// ------------------------------------------------------------------------------
// Upload a file from an INPUT field using AJAX.
// ------------------------------------------------------------------------------
function doUpload($upload_field)
{	
  var $container  = $upload_field.parent('div.field');
  var $hidden     = $container.find('span input');
  var $hfParent   = $hidden.parent();
  var $progress   = $container.find('span.progress');

	// Remove any previous error messages.
	$container.find('span.invalid').remove();

	$.ajaxFileUpload({
    url:            '/pgadmissions/documents/async',
    secureuri:      false,
    fileElementId:  $upload_field.attr('id'),	
    dataType:       'text',
    data:           { type: $upload_field.attr('data-type') },
    success: function (data)
    {		
			$container.removeClass('posting');
			if ($(data).find('span.invalid').length > 0)
			{
        // There was an uploading error.
				$container.append(data);
      }
      else if ($(data).find('input').length == 0)
      {
        // There was (probably) a server error.
        $container.append('<span class="invalid">Could not upload.</span>');
      }
			else
			{
				// i.e. if there are no uploading errors, which would be indicated by the presence of a SPAN.invalid tag.
				$hfParent.html(data);
				$container.addClass('uploaded');
				var doc_type = $upload_field.attr('data-reference');
				$('a.button-delete', $hfParent).attr({ 'data-desc': 'Delete ' + doc_type })
		                                       .qtip(tooltipSettings);
			}
    },
		error: function()
		{
			$container.append('<span class="invalid">Upload failed; please retry.</span>');
		}
  });
}


// ------------------------------------------------------------------------------
// Check whether a form is empty (no user input).
// ------------------------------------------------------------------------------
function isFormEmpty($container)
{
	var filled = 0;
	// DO NOT check hidden fields!
	$('input[type!="hidden"], select, textarea, input.file', $container).each(function()
	{
		var $field = $(this);
		// Don't check terms checkboxes.
		if (!$field.parent('div').hasClass('terms-field'))
		{
			// Checkboxes require checking for a checked state (as val() returns the checkbox's checked value).
			if ($field.is(':checked') ||
				 (($field.attr('type') != 'checkbox' && $field.attr('type') != 'radio') && $field.val()))
			{
				filled++;
			}
		}
	});
	return (filled == 0);
}


// ------------------------------------------------------------------------------
// Mark a section's info bar if the section contains validation errors.
// ------------------------------------------------------------------------------
function markSectionError(section_id)
{
	var $section = $(section_id);
	
	// If the terms checkbox isn't ticked, highlight the terms box.
	var $terms = $('.terms-box', $section);
	if ($('input:checkbox', $terms).not(':checked'))
	{
		$terms.css({borderColor: 'red', color: 'red'});
	}
	
	// Check for form validation errors.
	var errors   = $('.invalid:visible', $section).length;
	if (errors == 0) { return; }
	
	// Change the info bar.
	var $infobar = $('.section-info-bar', $section);
	if ($infobar)
	{
		$infobar.removeClass('section-info-bar').addClass('section-error-bar');
		$infobar.prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
		addToolTips();
	}
}


// ------------------------------------------------------------------------------
// "Unmark" a section.
// ------------------------------------------------------------------------------
function unmarkSection(section_id)
{
	var $section = $(section_id);
	
	// Unmark the terms box.
	var $terms = $('.terms-box', $section);
	$terms.removeAttr('style');
	
	// Remove validation errors.
	$('span.invalid', $section).remove();
	
	// Revert the info bar.
	var $infobar = $('.section-error-bar', $section);
	$infobar.removeClass('section-error-bar').addClass('section-info-bar');
	$('span.error-hint', $infobar).remove();
}


function clearForm($form)
{
	var $fields = $form.find('input[type!="hidden"], select, textarea');
	$fields.each(function()
	{
		var $field = $(this);
		if ($field.not('.list-select-to, .list-select-from')) // exclude lists of reviewers/supervisors/interviewers
		{
			if ($field.is(':checkbox, :radio'))
			{
				$field.attr('checked', false);
			}
			else
			{
				$(this).val('');
			}
		}
	});
		
	// Remove any uploaded files in field rows.
	$('div.field', $form).removeClass('uploaded');
	$('div.field .uploaded-files', $form).html('');
	
	// Remove any validation errors.
	var id = $form.closest('section').attr('id');
	if (id != '')
	{
		unmarkSection(id);
	}
	//$('span.invalid', $form).remove();
}


function setupModalBox()
{
	// Hide the modal window and overlay.
	$('#dialog-overlay, #dialog-box').hide();
	
	// Reposition the dialog box on window resize.
	$(window).resize(function()
	{
		//only do it if the dialog box is not hidden
		if (!$('#dialog-box').is(':hidden'))
		{
			modalPosition();
		}
	});	

	// Hitting the escape key closes the modal box.
	$(document).keypress(function(e)
	{
		if ( e.keyCode == 27 )
		{
			$('#dialog-overlay, #dialog-box').hide();		
			return false;
		}
	});
}


function modalPosition()
{
	var offset_x = $('#dialog-box').width() / 2;
	var offset_y = $('#dialog-box').height() / 2;
	
	$('#dialog-box').css({ marginLeft: -offset_x, marginTop: -offset_y })
									.show();
}


function modalPrompt(message, okay, cancel)
{
	if (okay == 'undefined')
	{
		okay = function() {};
	}
	if (cancel == 'undefined')
	{
		cancel = function() {};
	}
	
	$('#dialog-header').html("Please Confirm!");
	$('#dialog-message').html(message);

	// Set function to execute on "Ok".
	$('#dialog-box').off('click', '#popup-ok-button')
                  .on('click', '#popup-ok-button', function()
									{
										$('#dialog-overlay, #dialog-box').hide();
										okay();
										return false;
									});

	// Set function to execute on "Cancel".
	$('#dialog-box').off('click', '#popup-cancel-button')
                  .on('click', '#popup-cancel-button', function()
									{
										$('#dialog-overlay, #dialog-box').hide();
										cancel();
										return false;
									});

	// Show the box.
	$('#dialog-overlay, #dialog-box').show();
	modalPosition();
}


function numbersOnly(event)
{
	// http://stackoverflow.com/questions/995183/how-to-allow-only-numeric-0-9-in-html-inputbox-using-jquery
	if (event.keyCode == 46 ||	// backspace
			event.keyCode == 8 ||		// delete
			event.keyCode == 9 ||		// tab
			event.keyCode == 27 ||	// escape
			event.keyCode == 13 || 	// enter
			(event.keyCode == 65 && event.ctrlKey === true) || // Ctrl+A
			(event.keyCode >= 35 && event.keyCode <= 39)) // home, end, left, right
	{
		// let it happen, don't do anything
		return;
	}
	else
	{
		// Ensure that it is a number and stop the keypress.
		if (event.shiftKey || (event.keyCode < 48 || event.keyCode > 57) && (event.keyCode < 96 || event.keyCode > 105 ))
		{
			event.preventDefault(); 
		}   
	}
}