function bindDatePickers(){
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
}
