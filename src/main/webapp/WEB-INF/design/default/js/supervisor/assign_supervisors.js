$(document).ready(function() {

    $("#applicationSupervisorsList").selectable();

    // -----------------------------------------------------------------------------------------
    // Remove supervisor
    // -----------------------------------------------------------------------------------------
    $('#assignSupervisorsToAppSection').on('click', '#removeSupervisorBtn', function() {
        $('#applicationSupervisorsList li').each(function () {
            var id = $(this).data("supervisorid");
            if ($(this).hasClass("ui-selected")) {
                $("#programSupervisors option[value='" + id + "']").removeClass('selected').removeAttr('disabled');
                $(this).remove();
                if($('#applicationSupervisorsList li').length < 2){ 
                	enableAddingSupervisorsToList();
                }
            }
        });
        resetSupervisorsErrors();
    });
    
    // -----------------------------------------------------------------------------------------
    // Add supervisor
    // -----------------------------------------------------------------------------------------
    $('#assignSupervisorsToAppSection').on('click', '#addSupervisorBtn', function() {
        var selectedSupervisors = $('#programSupervisors').val();
        if (selectedSupervisors) {
            for (var i in selectedSupervisors) {
                var id = selectedSupervisors[i];
                var $option = $("#programSupervisors option[value='" + id + "']");
                var selText = $option.text();
                var category = $option.attr("category");
                if(appendNewSupervisorToList(id, selText, category)){
                	$("#programSupervisors option[value='" + id + "']").addClass('selected').removeAttr('selected').attr('disabled', 'disabled');
                }
            }
        }
        resetSupervisorsErrors();
    });

    // -----------------------------------------------------------------------------------------
    // Create a new supervisor
    // -----------------------------------------------------------------------------------------
    $('#createsupervisorsection').on('click', '#createSupervisor', function() {
        $('#ajaxloader').show();
        
        var postData = {
            applicationId : $('#applicationId').val(),
            firstName : $('#newSupervisorFirstName').val(),
            lastName : $('#newSupervisorLastName').val(),
            email : $('#newSupervisorEmail').val()
        };
        
        $.ajax({
            type : 'POST',
            statusCode : {
                401 : function() { window.location.reload(); },
                500 : function() { window.location.href = "/pgadmissions/error"; },
                404 : function() { window.location.href = "/pgadmissions/404"; },
                400 : function() { window.location.href = "/pgadmissions/400"; },
                403 : function() { window.location.href = "/pgadmissions/404"; }
            },
            url : "/pgadmissions/approval/createSupervisor",
            data : $.param(postData),
            success : function(data) {
                var newSuperviosr;
                try {
                    newSuperviosr = jQuery.parseJSON(data);
                } catch (err) {
                    $('#createsupervisorsection').html(data);
                    addToolTips();
                    return;
                }
                if (newSuperviosr.isNew) {
                    $('#previous').append('<option value="' + $('#applicationId').val() + '|' + newSuperviosr.id + '" category="previous" disabled="disabled">' + newSuperviosr.firstname + ' ' + newSuperviosr.lastname + '</option>');
                    appendNewSupervisorToList($('#applicationId').val() + '|' + newSuperviosr.id, newSuperviosr.firstname + ' ' + newSuperviosr.lastname , "");
                } else {
                    addExistingUserToSupervisorsLists(newSuperviosr);
                }
                resetSupervisorsErrors();
                getCreateSupervisorsSection();
				
				
				
            },
            complete : function() {
               $('#ajaxloader').fadeOut('fast');
	
            }
        });

    });
    
    if($('#applicationSupervisorsList li').length >= 2){
    	disableAddingSupervisorsToList();
    } 
    
});

function appendNewSupervisorToList(id, text, category) {
	if($('#applicationSupervisorsList li').length >= 2){
		return false;
	}
    var newSupervisorRow = ''
        + '<li data-supervisorid="' + id + '" class="ui-widget-content">' + text + '<span style="float:right; padding-right:20px;"><input type="radio" value="' + id + '" name="primarySupervisor"> Primary Supervisor</span></li>';
    $("#applicationSupervisorsList").append(newSupervisorRow);
    if($('#applicationSupervisorsList li').length >= 2){
    	disableAddingSupervisorsToList();
    }
    return true;
}

function getCreateSupervisorsSection() {
    $('#ajaxloader').show();

    $.ajax({
        type : 'GET',
        statusCode : {
            401 : function() { window.location.reload(); },
            500 : function() { window.location.href = "/pgadmissions/error"; },
            404 : function() { window.location.href = "/pgadmissions/404"; },
            400 : function() { window.location.href = "/pgadmissions/400"; },
            403 : function() { window.location.href = "/pgadmissions/404"; }
        },
        url : "/pgadmissions/approval/create_supervisor_section?applicationId=" + $('#applicationId').val(),
        success : function(data) {
            $('#ajaxloader').fadeOut('fast');
            $('#createsupervisorsection').html(data);
			if($('#applicationSupervisorsList li').length >= 2){
				disableAddingSupervisorsToList();
			}
        }
    });
}

function addExistingUserToSupervisorsLists(newSupervisor) {

    if ($('#applicationSupervisors option[value="' + $('#applicationId').val() + '|' + newSupervisor.id + '"]').length > 0) {
        return;
    }

    if ($('#default option[value="' + $('#applicationId').val() + '|' + newSupervisor.id + '"]').length > 0) {
        $('#default option[value="' + $('#applicationId').val() + '|' + newSupervisor.id + '"]').attr("selected", 'selected');
        $('#addSupervisorBtn').trigger('click');
        return;
    }

    if ($('#previous option[value="' + $('#applicationId').val() + '|' + newSupervisor.id + '"]').length > 0) {
        $('#previous option[value="' + $('#applicationId').val() + '|' + newSupervisor.id + '"]').attr("selected", 'selected');
        $('#addSupervisorBtn').trigger('click');
        return;
    }

    $('#previous').append('<option value="' + $('#applicationId').val() + '|' + newSupervisor.id + '" category="previous" disabled="disabled">' + newSupervisor.firstname + ' ' + newSupervisor.lastname + '</option>');
    $('#applicationSupervisors').append('<option value="' + $('#applicationId').val() + '|' + newSupervisor.id + '">' + newSupervisor.firstname + ' ' + newSupervisor.lastname + '</option>');
    
    appendNewSupervisorToList($('#applicationId').val() + '|' + newSupervisor.id, newSupervisor.firstname + ' ' + newSupervisor.lastname, "");

}

function disableAddingSupervisorsToList() {

	$('#programSupervisors').attr("disabled", "disabled");
	$("#addSupervisorBtn").addClass("disabled");
	
	$("#p_newSupervisor").addClass("grey-label").parent().find('.hint').addClass("grey");
	
	$("#newSupervisorFirstName").attr("disabled", "disabled");
	$("#newSupervisorFirstName").val("");
    $("#lbl_newSupervisorFirstName").addClass("grey-label").parent().find('.hint').addClass("grey");
    $("#lbl_newSupervisorFirstName").html("Supervisor First Name");
	
	$("#newSupervisorLastName").attr("disabled", "disabled");
	$("#newSupervisorLastName").val("");
    $("#lbl_newSupervisorLastName").addClass("grey-label").parent().find('.hint').addClass("grey");
    $("#lbl_newSupervisorLastName").html("Supervisor Last Name");
	
	$("#newSupervisorEmail").attr("disabled", "disabled");
	$("#newSupervisorEmail").val("");
    $("#lbl_newSupervisorEmail").addClass("grey-label").parent().find('.hint').addClass("grey");
    $("#lbl_newSupervisorEmail").html("Email");
    
    $("#createsupervisorsection").find('div.alert-error').remove(); // remove all previous form errors
    
    $("#createSupervisor").attr('disabled', 'disabled');
}

function enableAddingSupervisorsToList() {
	$('#programSupervisors').removeAttr("disabled", "disabled");
	$("#addSupervisorBtn").removeClass("disabled");
	
	$("#p_newSupervisor").removeClass("grey-label").parent().find('.hint').removeClass("grey");
	
	$("#newSupervisorFirstName").removeAttr("disabled", "disabled");
    $("#lbl_newSupervisorFirstName").removeClass("grey-label").parent().find('.hint').removeClass("grey");
    $("#lbl_newSupervisorFirstName").html("Supervisor First Name<em>*</em>");
    
	$("#newSupervisorLastName").removeAttr("disabled", "disabled");
    $("#lbl_newSupervisorLastName").removeClass("grey-label").parent().find('.hint').removeClass("grey");
    $("#lbl_newSupervisorLastName").html("Supervisor Last Name<em>*</em>");
    
	$("#newSupervisorEmail").removeAttr("disabled", "disabled");
    $("#lbl_newSupervisorEmail").removeClass("grey-label").parent().find('.hint').removeClass("grey");
    $("#lbl_newSupervisorEmail").html("Email<em>*</em>");
    
    $("#createSupervisor").removeAttr('disabled');
}

function resetSupervisorsErrors() {
    if ($('#applicationSupervisors option').size() > 0) {
        $('#supervisorsErrorSpan').remove();
    }
}
