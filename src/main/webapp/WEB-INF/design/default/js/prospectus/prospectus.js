$(document).ready(function(){
		
		$("select#programme").bind('change', function() {
			var programme_code= $("#programme").val();
			if(programme_code==""){
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
			        url:"/pgadmissions/prospectus/getAdvertData",
			        data: {
			        	programCode: programme_code,
			        }, 
			        success: function(data) {
			        	alert(data);
			        },
			        complete: function() {
			        }
			    });
			}
		});
		
		$("#save-go").bind('click', function(){
			duration = {
				value : $("#programmeDurationOfStudy").val(),
				unit : $("#timeUnit").val()
			};
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
					programCode: $("#programme").val(),
					description:$("#programmeDescription").val(),
					durationOfStudyInMonth:JSON.stringify(duration),
					fundingInformation:$("#programmeFundingInformation").val(),
					isCurrentlyAcceptingApplications:$('input["#currentlyAcceptingApplication"]:checked').val()
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

