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
			        	var map = JSON.parse(data);
			        	$("#buttonToApply").val(map['buttonToApply']);
			        	$("#linkToApply").val(map['linkToApply']);
			        	var advert = map['advert'];
			        	if(advert){
			        		$("#programmeDescription").val(advert['description']);
			        		$("#programmeDurationOfStudy").val(advert['durationOfStudyInMonth'].toString());
			        		$("#timeUnit").val('Months');
			        	}
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
		
		$("#save-upi-go").bind('click', function() {
			$("#irisSection").find("div.alert").remove();
			$('#iris-profile-modal').modal();
			$("#iris-profile-modal-iframe").attr("src", "http://iris.ucl.ac.uk/iris/browse/profile?upi=" + $('#upi').val());
		});
		
		$("#iris-profile-modal-confirm-btn").bind('click', function() {
			$('#iris-profile-modal').modal('hide');
			$("#irisSection").find("div.alert").remove();
			$.ajax({
				type: 'POST',
				dataType: "json",
				statusCode: {
					401: function() { window.location.reload(); },
					500: function() { window.location.href = "/pgadmissions/error"; },
					404: function() { window.location.href = "/pgadmissions/404"; },
					400: function() { window.location.href = "/pgadmissions/400"; },                  
					403: function() { window.location.href = "/pgadmissions/404"; }
				},
				url: "/pgadmissions/users/IRIS/",
				data: {
					upi : $('#upi').val()
				}, 
				success: function(data) {
					if (data.success) {
						alert("success");
					} else {
						if (data.irisProfile != null) {
		                    $('#upi').parent().append('<div class="alert alert-error"> <i class="icon-warning-sign"></i> ' + data.irisProfile  + '</div>');
		                }
					}
				},
				complete: function() {
				}
			});
		});
});

function clearAll(){
	$("#linkToApply").val("");
	$("#buttonToApply").val("");
}

