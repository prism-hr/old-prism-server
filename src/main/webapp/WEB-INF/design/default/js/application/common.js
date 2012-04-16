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
	
	
}
