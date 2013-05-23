$(document).ready(function(){
		
		$("select#programme").bind('change', function() {
			var programme_id= $("#programme").val();
			if(programme_id==""){
				clearAll();
			}
			else{
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
		        	programmeId: programme_id,
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
					programmeId: programme_id,
				}, 
				success: function(data) {
					$("#buttonToApply").val(data);
				},
				complete: function() {
				}
			});
			}
		});
		
		$("#save-go").bind('click', function(){
			$.ajax({
				type: 'POST',
				statusCode: {
					401: function() { window.location.reload(); },
					500: function() { window.location.href = "/pgadmissions/error"; },
					404: function() { window.location.href = "/pgadmissions/404"; },
					400: function() { window.location.href = "/pgadmissions/400"; },                  
					403: function() { window.location.href = "/pgadmissions/404"; }
				},
				url:"/pgadmissions/prospectus/saveProgramAdvert",
				data: {
					programId: $("#programme").val(),
					description:$("#programmeDescription").val(),
					durationOfStudy:$("#programmeDurationOfStudy").val(),
					durationOfStudyUnit:$("#timeUnit").val(),
					fundingInformation:$("#programmeFundingInformation").val(),
					isCurrentlyAcceptingApplications:$("#currentlyAcceptingApplication").val()
				}, 
				success: function(data) {
				},
				complete: function() {
				}
			});

		});
		
		$("clear-go").bind('click', function(){
			clearAll();
		});
});

function clearAll(){
	$("#linkToApply").val("");
	$("#buttonToApply").val("");
}

