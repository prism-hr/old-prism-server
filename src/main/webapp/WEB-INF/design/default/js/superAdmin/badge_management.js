$(document).ready(function() {
    
    $('#badgeSaveButton').click(function() {
        $.ajax({
             type: 'POST',
             statusCode: {
            	 	401: function() { window.location.reload(); },
	                500: function() { window.location.href = "/pgadmissions/error"; },
	                404: function() { window.location.href = "/pgadmissions/404"; },
	                400: function() { window.location.href = "/pgadmissions/400"; },                  
	                403: function() { window.location.href = "/pgadmissions/404"; }
              },
            url:"/pgadmissions/badge/saveBadge",
            data: {
                closingDate: $('#batchdeadline').val(),
                projectTitle: $('#project').val(),
                program: $('#programme').val(),
                programmeHomepage: $('#programhome').val(),
                cacheBreaker: new Date().getTime() 
            },
            success: function(data) {
            	if (/^<form/i.test(data)) {
            		$(".alert-error").remove();
            		$('#html').removeAttr('readonly').val(data).autosize();
            	} else {
            		$('#badgeSection').html(data);
            	}
            	addToolTips();
    			initialiseDatepicker();
    			initialiseAutocomplete();
            },
            completed: function() {
				
            }
        });
        
        $.ajax({
	        type: 'GET',
	        statusCode: {
	                401: function() { window.location.reload(); },
	                500: function() { window.location.href = "/pgadmissions/error"; },
	                404: function() { window.location.href = "/pgadmissions/404"; },
	                400: function() { window.location.href = "/pgadmissions/400"; },                  
	                403: function() { window.location.href = "/pgadmissions/404"; }
	        },
	        url:"/pgadmissions/badge/getClosingDates",
	        data: {
	        	program: $("#programme").val(),
	        	cacheBreaker: new Date().getTime()
	        }, 
	        success: function(data) {
	        	selectedDates = [];
	            selectedDates = jQuery.parseJSON(data);
	        },
	        completed: function() {
	        }
	    });
    });
});