$(document).ready(function(){
	
		getUpiForCurrentUser();
	
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
			        		
			        		var durationOfStudyInMonths=advert['durationOfStudyInMonth'];
			        		if(durationOfStudyInMonths%12==0){
			        			$("#programmeDurationOfStudy").val((durationOfStudyInMonths/12).toString());
			        			$("#timeUnit").val('Years');
			        		}else{
			        			$("#programmeDurationOfStudy").val(durationOfStudyInMonths.toString());
			        			$("#timeUnit").val('Months');
			        		}
			        		
			        		if(advert['isCurrentlyAcceptingApplications']){$("#currentlyAcceptingApplicationYes").prop("checked", true);}
			        		else{$("#currentlyAcceptingApplicationNo").prop("checked", true);}
			        	}else{
			        		clearAdvert();
			        	}
			        },
			        complete: function() {
			        }
			    });
			}
		});
		
		$("#save-go").bind('click', function(){
			clearPreviousErrors();
			var duration = {
				value : $("#programmeDurationOfStudy").val(),
				unit : $("#timeUnit").val()
			};
			var acceptApplications;
			if($("#currentlyAcceptingApplicationYes").prop("checked")){acceptApplications="true";}
			else if($("#currentlyAcceptingApplicationNo").prop("checked")){acceptApplications="false";}
			else {acceptApplications="";}
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
					isCurrentlyAcceptingApplications:acceptApplications
				}, 
				success: function(data) {
					var map = JSON.parse(data);
					if(!map['success']){
						if(map['program']){
							$("#program").append(getErrorMessageHTML(map['program']));
						}
						if(map['description']){
							$("#description").append(getErrorMessageHTML(map['description']));
						}
						if(map['durationOfStudyInMonth']){
							$("#durationOfStudyInMonth").append(getErrorMessageHTML(map['durationOfStudyInMonth']));
						}
						if(map['isCurrentlyAcceptingApplications']){
							$("#isCurrentlyAcceptingApplications").append(getErrorMessageHTML(map['isCurrentlyAcceptingApplications']));
						}
					}
				},
				complete: function() {
				}
			});

		});
		
		
		$("#save-upi-go").bind('click', function() {
			$("#irisSection").find("div.alert-error").remove();
			if ($("#upi").val() == "") {
				$('#upi').parent().append('<div class="alert alert-error"> <i class="icon-warning-sign"></i> You must make an entry.</div>');
				return;
			}
			$('#iris-profile-modal').modal();
			$("#iris-profile-modal-iframe").attr("src", "http://iris.ucl.ac.uk/iris/browse/profile?upi=" + $('#upi').val());
		});
		
		$("#iris-profile-modal-confirm-btn").bind('click', function() {
			$('#iris-profile-modal').modal('hide');
			$("#irisSection").find("div.alert-error").remove();
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
					if (!data.success) {
						if (data.irisProfile != null) {
		                    $('#upi').parent().append('<div class="alert alert-error"> <i class="icon-warning-sign"></i> ' + data.irisProfile  + '</div>');
		                }
					} else {
						$("#iris-account-linked-message").show().find("span").html($('#upi').val());
						$("#iris-account-not-linked-message").hide();
					}
				},
				complete: function() {
				}
			});
		});
});

function clearAdvert(){
	$("#programmeDescription").val("");
	$("#programmeDurationOfStudy").val("");
	$("#timeUnit").val("");
	$("#programmeFundingInformation").val("");
	$("#currentlyAcceptingApplicationYes").prop("checked", false);
	$("#currentlyAcceptingApplicationNo").prop("checked", false);
}

function clearAll(){
	clearAdvert();
	$("#buttonToApply").val("");
	$("#linkToApply").val("");
}

function getUpiForCurrentUser() {
	$.ajax({
		type: 'GET',
		statusCode: {
			401: function() { window.location.reload(); },
			500: function() { window.location.href = "/pgadmissions/error"; },
			404: function() { window.location.href = "/pgadmissions/404"; },
			400: function() { window.location.href = "/pgadmissions/400"; },                  
			403: function() { window.location.href = "/pgadmissions/404"; }
		},
		url: "/pgadmissions/users/IRIS/",
		success: function(data) {
			if (data.upi) {
				$('#upi').val(data.upi);
				$("#iris-account-linked-message").show().find("span").html($('#upi').val());
				$("#iris-account-not-linked-message").hide();
			} else {
				$("#iris-account-linked-message").hide();
				$("#iris-account-not-linked-message").show();
			}
		},
		complete: function() {
		}
	});
}

function getErrorMessageHTML(message){
	return "<div class=\"row error\"><div class=\"field\"><div class=\"alert alert-error\"><i class=\"icon-warning-sign\"></i> "+message+"</div></div></div>";
}

function clearPreviousErrors(){
	$(".error").remove();
}
