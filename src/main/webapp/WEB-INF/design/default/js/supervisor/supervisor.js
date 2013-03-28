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
        $('#createsupervisorsection').append('<div class="ajax" />');
        
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
                $('#createsupervisorsection div.ajax').remove();
            }
        });

    });
    
    // -------------------------------------------------------------------------------
    // Provide project description
    // -------------------------------------------------------------------------------
    $("input[name='provideProjectDescription']").bind('change', function() {
        var selected_radio = $("input[name='provideProjectDescription']:checked").val();
        if (selected_radio == 'yes')   {
            enableProjectDescription();
        } else {
            disableProjectDescription();
        }
    });   
    
    // -------------------------------------------------------------------------------
    // Recommended offer type
    // -------------------------------------------------------------------------------
    $("input[name='offerType']").bind('change', function() {
        var selected_radio = $("input[name='offerType']:checked").val();
        if (selected_radio == 'conditional')   {
            enableConditions();
        } else {
            disableConditions();
        }
    });     

    // -----------------------------------------------------------------------------------------
    // Submit selected Supervisors
    // -----------------------------------------------------------------------------------------
    $('#assignSupervisorsBtn').click(function() {
        $('#approvalsection').append('<div class="ajax" />');
        var url = "/pgadmissions/approval/assignSupervisors";

        $('#applicationSupervisorsList li').each(function() {
            $('#postApprovalData').append("<input name='supervisors' type='hidden' value='" + $(this).data('supervisorid') + "'/>");
        });

        var postData = {
            applicationId : $('#applicationId').val(),
            supervisors : '',
            primarySupervisor : $('input[name=primarySupervisor]').val(),
            projectTitle : $('#projectTitle').val(),
            projectAbstract : $('#projectAbstract').val(),
            recommendedStartDate : $('#offerStartDate').val(),
            recommendedConditions : $('#offerConditions').val()
        };
        
        if ($('input:radio[name=provideProjectDescription]:checked').length > 0) {
        	var provide = $('input:radio[name=provideProjectDescription]:checked').val();
            postData.projectDescriptionAvailable = (provide == "yes" ? true : false);
        }
        
        if ($('input:radio[name=offerType]:checked').length > 0) {
        	var provide = $('input:radio[name=offerType]:checked').val();
            postData.recommendedConditionsAvailable = (provide == "conditional" ? true : false);
        }
        
        var serializedPostData = $.param(postData);
        var primarySupervisor = $('input[name=primarySupervisor]:checked').val();
        $('input[name="supervisors"]').each(function() {
        	var supervisorId = $(this).val();
        	if(supervisorId == primarySupervisor){
        		serializedPostData = serializedPostData + "&supervisors=" + supervisorId + "|primary";
        	} else {
        		serializedPostData = serializedPostData + "&supervisors=" + supervisorId;
        	}
        });
        
        $.ajax({
            type : 'POST',
            statusCode : {
                401 : function() { window.location.reload(); },
                500 : function() { window.location.href = "/pgadmissions/error"; },
                404 : function() { window.location.href = "/pgadmissions/404"; },
                400 : function() { window.location.href = "/pgadmissions/400"; },
                403 : function() { window.location.href = "/pgadmissions/404"; }
            },
            url : url,
            data : serializedPostData,
            success : function(data) {
                if (data == "OK") {
                    window.location.href = '/pgadmissions/applications?messageCode=move.approval&application=' + $('#applicationId').val();
                } else {
                    $('#approve-content').html(data);
                    $('#postApprovalData').html('');
                }
                addToolTips();
            },
            complete : function() {
                $('#approvalsection div.ajax').remove();
            }
        });
    });
    
    if($('#applicationSupervisorsList li').length >= 2){
    	disableAddingSupervisorsToList();
    }
    
    bindDatePicker('#offerStartDate');
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

function getSupervisorsSection() {
    $('#approvalsection').append('<div class="ajax" />');

    var url = "/pgadmissions/approval/supervisors_section";

    $.ajax({
        type : 'GET',
        statusCode : {
            401 : function() { window.location.reload(); },
            500 : function() { window.location.href = "/pgadmissions/error"; },
            404 : function() { window.location.href = "/pgadmissions/404"; },
            400 : function() { window.location.href = "/pgadmissions/400"; },
            403 : function() { window.location.href = "/pgadmissions/404"; }
        },
        url : url + "?applicationId=" + $('#applicationId').val(),
        success : function(data) {
            $('#approve-content').html(data);
            addToolTips();
        },
        complete : function() {
            $('#approvalsection div.ajax').remove();
        }
    });
}

function getCreateSupervisorsSection() {
    $('#createsupervisorsection').append('<div class="ajax" />');

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
            $('#createsupervisorsection div.ajax').remove();
            $('#createsupervisorsection').html(data);
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

function resetSupervisorsErrors() {
    if ($('#applicationSupervisors option').size() > 0) {
        $('#supervisorsErrorSpan').remove();
    }
}

function disableConditions() {
    $("#offerConditions").attr("disabled", "disabled");
    $("#offerConditions").val("");
    $("#lbl_offerConditions").addClass("grey-label");
    $("#lbl_offerConditions").html("Recommended Conditions");
}

function enableConditions() {
    $("#offerConditions").removeAttr("disabled", "disabled");
    $("#offerConditions").removeAttr("readonly", "readonly");
    $("#lbl_offerConditions").removeClass("grey-label");
    $("#lbl_offerConditions").html("Recommended Conditions<em>*</em>");
}

function disableProjectDescription() {
    $("#projectTitle").attr("disabled", "disabled");
    $("#projectTitle").val("");
    $("#lbl_projectTitle").addClass("grey-label");
    $("#lbl_projectTitle").html("Project Title");

    $("#projectAbstract").attr("disabled", "disabled");
    $("#projectAbstract").val("");
    $("#lbl_projectAbstract").addClass("grey-label");
    $("#lbl_projectAbstract").html("Project Abstract");
}

function enableProjectDescription() {
    $("#projectTitle").removeAttr("disabled", "disabled");
    $("#lbl_projectTitle").removeClass("grey-label");
    $("#lbl_projectTitle").html("Project Title<em>*</em>");
    
    $("#projectAbstract").removeAttr("disabled", "disabled");
    $("#lbl_projectAbstract").removeClass("grey-label");
    $("#lbl_projectAbstract").html("Project Abstract<em>*</em>");
}

function disableAddingSupervisorsToList() {
	$('#programSupervisors').attr("disabled", "disabled");
	$("#addSupervisorBtn").addClass("disabled");
	
	$("#p_newSupervisor").addClass("grey-label");
	
	$("#newSupervisorFirstName").attr("disabled", "disabled");
	$("#newSupervisorFirstName").val("");
    $("#lbl_newSupervisorFirstName").addClass("grey-label");
    $("#lbl_newSupervisorFirstName").html("Supervisor First Name");
	
	$("#newSupervisorLastName").attr("disabled", "disabled");
	$("#newSupervisorLastName").val("");
    $("#lbl_newSupervisorLastName").addClass("grey-label");
    $("#lbl_newSupervisorLastName").html("Supervisor Last Name");
	
	$("#newSupervisorEmail").attr("disabled", "disabled");
	$("#newSupervisorEmail").val("");
    $("#lbl_newSupervisorEmail").addClass("grey-label");
    $("#lbl_newSupervisorEmail").html("Email");
    
    $("#createsupervisorsection").find('div.alert-error').remove(); // remove all previous form errors
    
    $("#createSupervisor").hide();
}

function enableAddingSupervisorsToList() {
	$('#programSupervisors').removeAttr("disabled", "disabled");
	$("#addSupervisorBtn").removeClass("disabled");
	
	$("#p_newSupervisor").removeClass("grey-label");
	
	$("#newSupervisorFirstName").removeAttr("disabled", "disabled");
    $("#lbl_newSupervisorFirstName").removeClass("grey-label");
    $("#lbl_newSupervisorFirstName").html("Supervisor First Name<em>*</em>");
    
	$("#newSupervisorLastName").removeAttr("disabled", "disabled");
    $("#lbl_newSupervisorLastName").removeClass("grey-label");
    $("#lbl_newSupervisorLastName").html("Supervisor Last Name<em>*</em>");
    
	$("#newSupervisorEmail").removeAttr("disabled", "disabled");
    $("#lbl_newSupervisorEmail").removeClass("grey-label");
    $("#lbl_newSupervisorEmail").html("Email<em>*</em>");
    
    $("#createSupervisor").show();
}
