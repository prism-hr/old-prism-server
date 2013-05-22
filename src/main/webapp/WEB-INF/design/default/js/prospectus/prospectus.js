$(document).ready(function(){
		
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
		        url:"/pgadmissions/prospectus/getLinkToApply",
		        data: {
		        	programmeCode: $("#programme").val(),
		        }, 
		        success: function(data) {
		        	$("#linkToApply").val(data);
		        },
		        complete: function() {
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
				url:"/pgadmissions/prospectus/getButtonToApply",
				data: {
					programmeCode: $("#programme").val(),
				}, 
				success: function(data) {
					$("#buttonToApply").val(data);
				},
				complete: function() {
				}
			});
		});
});

