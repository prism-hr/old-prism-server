$(document).ready(function()
{
	// ------------------------------------------------------------------------------
	// Create the feedback button.
	// ------------------------------------------------------------------------------
	makeFeedbackButton();

	// ------------------------------------------------------------------------------
	// "Select all" checkbox functionality.
	// ------------------------------------------------------------------------------
  $('#select-all').click(function(event)
	{
    var selectAllValue = this.checked;
    
    //Iterate each checkbox
      $(':checkbox').each(function() {
        this.checked = selectAllValue;                        
      });
  });
  
	// ------------------------------------------------------------------------------
	// On the application form, show "Not Provided" text in grey.
	// ------------------------------------------------------------------------------
  $('.field').each(function()
	{
     var strValue = $(this).text();
    
     if (strValue.match("Not Provided"))
		 {
       $(this).toggleClass('grey-label');
       
       var labelValue = $(this).prev().text();
       if (labelValue.match("Additional Information"))
			 {
         $(this).prev().css("font-weight","bold");
       }
     }
  });
  
	// ------------------------------------------------------------------------------
	// Add tooltips!
	// ------------------------------------------------------------------------------
  fn = window['addToolTips'];
  if(typeof  fn  == 'function'){
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
	// Adding style to delete button to make it free from inherited style
	// ------------------------------------------------------------------------------
  var styleMap = {
    'padding':'0',
      'background' : 'none'
  } 
  $('.button-delete').parent().css(styleMap);
  
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
				dateFormat: 'dd-M-yy',
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

	$('*[data-desc]').qtip(tooltipSettings);
}


// ------------------------------------------------------------------------------
// Set up a div.field container with a file INPUT field for AJAX uploading.
// ------------------------------------------------------------------------------
function watchUpload($field)
{
  var $container  = $field.parent('div.field');
  
  $container.on('click', '.button-delete', function()
  {
	  $container.removeClass('uploaded');
		$field.val('');
  });

  $container.on('change', $field, function()
  {
    var input    = this.children[0];
    var $hidden  = $container.find('span input');
		if (input.files[0].size < 10485760) // 10MB in bytes
    {
			deleteUploadedFile($hidden);
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
		$.post("/pgadmissions/delete/asyncdelete", { documentId: $hidden_field.val() });
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
				$hfParent.html(data).show();
				$container.addClass('uploaded');
				var doc_type = $upload_field.attr('data-reference');
				$('a.button-delete', $hfParent).attr({ 'data-desc': 'Delete ' + doc_type })
                                     .qtip(tooltipSettings);
			}
			$container.removeClass('posting');
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
	$('input[type!="hidden"], select, textarea', $container).each(function()
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
	var errors   = $('.invalid:visible', $section).length;
	if (errors == 0) { return; }
	
	// highlight terms box.
	$('.terms-box', $section).css({borderColor: 'red', color: 'red'});

	// Change the info bar.
	var $infobar = $('.section-info-bar', $section);
	$infobar.removeClass('section-info-bar').addClass('section-error-bar');
	$(".info-text", $infobar).removeClass('info-text').addClass('invalid-info-text');
	if ($('.error-hint', $infobar).length == 0)
	{
		$('.row', $infobar).prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
		addToolTips();
	}
}
