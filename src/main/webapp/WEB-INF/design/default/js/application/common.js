
var tooltipSettings = {
	content: {
		text: function(api) {
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


// A simple function for replacing buttons with a loading graphic.
function replaceWithLoader($button)
{
	$button.replaceWith('<img class="loader" src="/pgadmissions/design/default/images/ajax-loader.gif" />');
}


function bindDatePicker(selector)
{
	// Date pickers.
	$(selector).each(function(){

		if(!$(this).hasClass('hasDatepicker'))
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

function limitTextArea(){
	$('textarea[maxlength]').keyup(function(){  
		//get the limit from maxlength attribute  
		var limit = parseInt($(this).attr('maxlength'));  
		//get the current text inside the textarea  
		var text = $(this).val();  
		//count the number of characters in the text  
		var chars = text.length;  

		//check if there are more characters than allowed  
		if(chars > limit){  
			//and if there are use substr to get the text before the limit  
			var new_text = text.substr(0, limit);  

			//and change the current text with the new text  
			$(this).val(new_text);
		} else if (chars == limit){
			alert('You have entered the maximum allowed characters for this field: '+ limit);
		}  
	});
}

// Form hint tooltips.
function addToolTips()
{
	$('*[data-desc]').qtip(tooltipSettings);
}

// Set up file uploading functionality.
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
			 alert("Sorry, document must be at most 10MB.");
		 }
  });
}

// Delete any associated file uploads.
function deleteUploadedFile($hidden_field)
{
	if ($hidden_field && $hidden_field.val() != '')
  {
		$.post("/pgadmissions/delete/asyncdelete", { documentId: $hidden_field.val() });
	}
}

// Process a file selected for uploading.
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


function isFormEmpty($container)
{
	var filled = 0;
	// DO NOT check hidden fields!
	$('input[type!="hidden"],select,textarea', $container).each(function()
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
