var selectedDates = {};
$(document).ready(function()
{
	$.ajax({
		 type: 'GET',
		 statusCode: {
			 401: function() { window.location.reload(); },
             500: function() { window.location.href = "/pgadmissions/error"; },
             404: function() { window.location.href = "/pgadmissions/404"; },
             400: function() { window.location.href = "/pgadmissions/400"; },                  
             403: function() { window.location.href = "/pgadmissions/404"; }
		  },
		url:"/pgadmissions/badge",
		data:{
		},
		success:function(data)
		{
			$('#badgeSection').html(data);
			addToolTips();
			initialiseDatepicker();
			getClosingDates();
		}
	});
});

function initialiseDatepicker() {
	$('#batchdeadline').datepicker({
		dateFormat: 'dd M yy',
		changeMonth: true,
		changeYear: true,
		yearRange: '1900:c+20',
		beforeShowDay: function(date) {
			for (var i = 0; i < selectedDates.length; i++) {
	            if (new Date(selectedDates[i]).toString() == date.toString()) {
	                return [true, 'highlighted', ''];
	            }
	        }
			return [true, '', ''];
		}
	});
}

function getClosingDates() {
	$("select#programme").bind('change', function() {
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

}

function updateBadge()
{
	$.ajax({
		 type: 'GET',
		 statusCode: {
			 401: function() { window.location.reload(); },
             500: function() { window.location.href = "/pgadmissions/error"; },
             404: function() { window.location.href = "/pgadmissions/404"; },
             400: function() { window.location.href = "/pgadmissions/400"; },                  
             403: function() { window.location.href = "/pgadmissions/404"; }
		  },
 		url:"/pgadmissions/badge/html",
		data:{
			program : $('#programme').val(),	
			project: $('#project').val(),
			programhome: $('#programhome').val(),
			batchdeadline: $('#batchdeadline').val(),			
			cacheBreaker: new Date().getTime() 
		},
		success:function(data)
		{
			$('#html').val(data).autosize();
		}
	});	
};