$(document).ready(function(){
		getUpiForCurrentUser();
		bindSaveUpiAction();
		bindIrisProfileModalConfirmAction();
		bindUnlinkUpiAction();
		generalTabing();
});

function bindIrisProfileModalConfirmAction(){
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
					$("#iris-account-not-linked-message").hide();
					$("#save-upi-go").hide();

					$("#iris-account-linked-message").show().find("span").html($('#upi').val());
					$("#unlink-upi-go").show();
					
					$("#upi").attr("disabled", "disabled");
				}
			},
			complete: function() {
			}
		});
	});
}

function bindUnlinkUpiAction(){
	$("#unlink-upi-go").bind('click', function() {
		$.ajax({
			type: 'DELETE',
			dataType: "json",
			statusCode: {
				401: function() { window.location.reload(); },
				500: function() { window.location.href = "/pgadmissions/error"; },
				404: function() { window.location.href = "/pgadmissions/404"; },
				400: function() { window.location.href = "/pgadmissions/400"; },                  
				403: function() { window.location.href = "/pgadmissions/404"; }
			},
			url: "/pgadmissions/users/IRIS/",
			success: function(data) {
					$("#iris-account-linked-message").hide();
					$("#unlink-upi-go").hide();
					
					$("#upi").val('');
					$("#iris-account-not-linked-message").show();
					$("#save-upi-go").show();
					
					$("#upi").removeAttr("disabled");
			},
			complete: function() {
			}
		});
	});
}

function bindSaveUpiAction(){
	$("#save-upi-go").bind('click', function() {
		$("#irisSection").find("div.alert-error").remove();
		if ($("#upi").val() == "") {
			$('#upi').parent().append('<div class="alert alert-error"> <i class="icon-warning-sign"></i> You must make an entry.</div>');
			return;
		}
		$('#iris-profile-modal').modal();
		$("#iris-profile-modal-iframe").attr("src", "http://iris.ucl.ac.uk/iris/browse/profile?upi=" + $('#upi').val());
	});
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
				$("#unlink-upi-go").show();
				
				$("#upi").attr("disabled", "disabled");
			} else {
				$("#iris-account-not-linked-message").show();
				$("#save-upi-go").show();
			}
		},
		complete: function() {
		}
	});
}

function appendErrorToElementIfPresent(message, element){
	if(message && element){
		element.append(getErrorMessageHTML(message));
	}
}

function getErrorMessageHTML(message){
	return "<div class=\"row error\"><div class=\"field\"><div class=\"alert alert-error\"><i class=\"icon-warning-sign\"></i> "+message+"</div></div></div>";
}

