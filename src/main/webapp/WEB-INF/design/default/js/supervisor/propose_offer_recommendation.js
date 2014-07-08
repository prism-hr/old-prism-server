$(document).ready(function() {

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
        $('#ajaxloader').show();
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
        
        if($('input:radio[name=projectAcceptingApplications]').val()!==undefined){
        	postData.projectAcceptingApplications =  $('input:radio[name=projectAcceptingApplications]:checked').val();
        }
        
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
                $('#ajaxloader').fadeOut('fast');
				addCounter();
				
            }
        });
    });
    
    bindDatePicker('#offerStartDate');
});

function getSupervisorsSection() {
    $('#ajaxloader').show();

    var url = "/pgadmissions/approval/propose_offer_recommendation";

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
            $('#ajaxloader').fadeOut('fast');
        }
    });
}

function disableConditions() {
    $("#offerConditions").attr("disabled", "disabled");
    $("#offerConditions").val("");
    $("#lbl_offerConditions").addClass("grey-label").parent().find('.hint').addClass("grey");
    $("#lbl_offerConditions").html("Recommended Conditions");
}

function enableConditions() {
    $("#offerConditions").removeAttr("disabled", "disabled");
    $("#offerConditions").removeAttr("readonly", "readonly");
    $("#lbl_offerConditions").removeClass("grey-label").parent().find('.hint').removeClass("grey");
    $("#lbl_offerConditions").html("Recommended Conditions<em>*</em>");
}

function disableProjectDescription() {
    $("#projectTitle").attr("disabled", "disabled");
    $("#projectTitle").val("");
    $("#lbl_projectTitle").addClass("grey-label").parent().find('.hint').addClass("grey");
    $("#lbl_projectTitle").html("Project Title");

    $("#projectAbstract").attr("disabled", "disabled");
    $("#projectAbstract").val("");
    $("#lbl_projectAbstract").addClass("grey-label").parent().find('.hint').addClass("grey");
    $("#lbl_projectAbstract").html("Project Abstract");
}

function enableProjectDescription() {
    $("#projectTitle").removeAttr("disabled", "disabled");
    $("#lbl_projectTitle").removeClass("grey-label").parent().find('.hint').removeClass("grey");
    $("#lbl_projectTitle").html("Project Title<em>*</em>");
    
    $("#projectAbstract").removeAttr("disabled", "disabled");
    $("#lbl_projectAbstract").removeClass("grey-label").parent().find('.hint').removeClass("grey");
    $("#lbl_projectAbstract").html("Project Abstract<em>*</em>");
}
