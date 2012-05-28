/*function bindDatePickers(){
 // Date pickers.

	$('input.date').each(function(){
		if(!$(this).hasClass()){
			$(this).attr("readonly", "readonly");	
			$(this).datepicker({
	  				dateFormat: 'dd-M-yy',
	 				changeMonth: true,
	  				changeYear: true,
	  				yearRange: '1900:c+20' });
		}
	});

}*/

function bindDatePicker(selector){
	// Date pickers.

	$(selector).each(function(){

		if(!$(this).hasClass('hasDatepicker')){

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

function addToolTips(){
	// Form hint tooltips.
	$('body span.hint').qtip({
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
	});
	
	$('.button-hint').qtip({
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
	});
		

	$('.error-hint').qtip({
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
	});
	
}


function watchUpload($field)
{
  var $container  = $field.parent('div.field');
  var $progress   = $container.find('span.progress');

  $container.on('change', $field, function()
  {
    var $this    = $(this);
    var $hidden  = $container.find('input[type="hidden"]');
		if ($this[0].files[0].size < 10485760) // 10MB in bytes
    {
			deleteUploadedFile($hidden);
			$progress.html('uploading file...');
			$field.attr("readonly", "readonly");
			doUpload($this);
			$field.removeAttr("readonly");
		 }
     else
     {
			 alert("Sorry, document must be at most 10MB.");
		 }
  });
}

function deleteUploadedFile($hidden_field)
{
	if ($hidden_field && $hidden_field.val() != '')
  {
		$.post("/pgadmissions/delete/asyncdelete", { documentId: $hidden_field.val() });
	}
}

function doUpload($upload_field)
{	
  var $container  = $upload_field.parent('div.field');
  var $hidden     = $container.find('input[type="hidden"]');
  var $hfParent   = $hidden.parent();
  var $progress   = $container.find('span.progress');

	// Showing/hiding progress bar (message) when we're uploading the file via AJAX.	
	$progress.ajaxStart(function()
  {
		$(this).show();
	})
	.ajaxComplete(function()
  {
		$(this).hide();
		$progress.html("");
	});
  
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
			if ($(data).find('span.invalid').length == 0)
			{
				// i.e. if there are no uploading errors, which would be indicated by the presence of a SPAN.invalid tag.
				$hfParent.html(data).show();
				$container.addClass('uploaded');
				//$('a.button-edit', $hfParent).attr({'id':'editQualiPOA','data-desc':'Edit Proof Of Award'});
			}
			else
			{
        // There was an uploading error.
				$container.append(data);
			}
    }
  });
}

