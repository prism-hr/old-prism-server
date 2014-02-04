$(document).ready(function() {
   
    // -------------------------------------------------------------------------------
    // Declined supervision reason
    // -------------------------------------------------------------------------------
    $("input[name='confirmedSupervision']").bind('change', function() {
        confirmedSupervisionChange();
    });
    
    // -------------------------------------------------------------------------------
    // Recommended offer type
    // -------------------------------------------------------------------------------
    $("input[name='recommendedConditionsAvailable']").bind('change', function() {
        var selected_radio = $("input[name='recommendedConditionsAvailable']:checked").val();
        if (selected_radio == 'true')   {
            enableConditions();
        } else {
            disableConditions();
        }
    });
    
	bindDatePicker('#recommendedStartDate');
    confirmedSupervisionChange();
    recomendedConditionsChange();
    
    autosuggest($("#secondarySupervisorFirstName"), $("#secondarySupervisorLastName"), $("#secondarySupervisorEmail"));
    
});

function confirmedSupervisionChange() {
    var selected_radio = $("input[name='confirmedSupervision']:checked").val();
    if (selected_radio == 'false')   {
        enableDeclinedSupervisionReason();
        disableProjectDescription();
        disableRecommendedOffer();
        disableSecondarySupervisor();
    } else if (selected_radio == 'true')   {
        disableDeclinedSupervisionReason();
        enableProjectDescription();
        enableRecommendedOffer();
        endableSecondarySupervisor();
    } else {
        disableDeclinedSupervisionReason();
        disableProjectDescription();
        disableRecommendedOffer();
        disableSecondarySupervisor();
    }
}

function disableSecondarySupervisor() {
	$('.secondarySupervisor_group input').attr("disabled", "disabled");
	$('#lbl_secondarySupervisor').addClass("grey-label").parent().find('.hint').addClass("grey");
	
	$('#lbl_secondarySupervisorFirstName').addClass("grey-label").parent().find('.hint').addClass("grey");
	$('#lbl_secondarySupervisorLastName').addClass("grey-label").parent().find('.hint').addClass("grey");
	$('#lbl_secondarySupervisorEmail').addClass("grey-label").parent().find('.hint').addClass("grey");

	$('#lbl_secondarySupervisorFirstName').html('Supervisor First Name');
	$('#lbl_secondarySupervisorLastName').html('Supervisor Last Name');
	$('#lbl_secondarySupervisorEmail').html('Supervisor Email');
	
}

function endableSecondarySupervisor() {
	$('.secondarySupervisor_group input').removeAttr("disabled");
	$('#lbl_secondarySupervisor').removeClass("grey-label").parent().find('.hint').removeClass("grey");

	$('#lbl_secondarySupervisorFirstName').removeClass("grey-label").parent().find('.hint').removeClass("grey");
	$('#lbl_secondarySupervisorLastName').removeClass("grey-label").parent().find('.hint').removeClass("grey");
	$('#lbl_secondarySupervisorEmail').removeClass("grey-label").parent().find('.hint').removeClass("grey");

	$('#lbl_secondarySupervisorFirstName').html('Supervisor First Name<em>*</em>');
	$('#lbl_secondarySupervisorLastName').html('Supervisor Last Name<em>*</em>');
	$('#lbl_secondarySupervisorEmail').html('Supervisor Email<em>*</em>');
	

}

function disableProjectDescription() {
	$("#lbl_projectDescription").addClass("grey-label").parent().find('.hint').addClass("grey");
	
    $("#projectTitle").attr("disabled", "disabled");
    $("#lbl_projectTitle").addClass("grey-label").parent().find('.hint').addClass("grey");
    $("#lbl_projectTitle").html("Project Title"); 
    
    $("#projectAbstract").attr("disabled", "disabled");
    $("#lbl_projectAbstract").addClass("grey-label").parent().find('.hint').addClass("grey");
    $("#lbl_projectAbstract").html("Project Abstract");

    $("#projectAcceptingApplicationsRadioYes").attr("disabled", "disabled");
    $("#projectAcceptingApplicationsRadioNo").attr("disabled", "disabled");
    $("#lbl_projectAcceptingApplications").addClass("grey-label").parent().find('.hint').addClass("grey");
    $("#lbl_projectAcceptingApplications").html("Do you wish to continue accepting applications?");
    
}

function enableProjectDescription() {
	$("#lbl_projectDescription").removeClass("grey-label").parent().find('.hint').removeClass("grey");
	
    $("#projectTitle").removeAttr("disabled", "disabled");
    $("#lbl_projectTitle").removeClass("grey-label").parent().find('.hint').removeClass("grey");
    $("#lbl_projectTitle").html("Project Title<em>*</em>");
    
    $("#projectAbstract").removeAttr("disabled", "disabled");
    $("#lbl_projectAbstract").removeClass("grey-label").parent().find('.hint').removeClass("grey");
    $("#lbl_projectAbstract").html("Project Abstract<em>*</em>");
    
    $("#projectAcceptingApplicationsRadioYes").removeAttr("disabled", "disabled");
    $("#projectAcceptingApplicationsRadioNo").removeAttr("disabled", "disabled");
    $("#lbl_projectAcceptingApplications").removeClass("grey-label").parent().find('.hint').removeClass("grey");
    $("#lbl_projectAcceptingApplications").html("Do you wish to continue accepting applications?<em>*</em>");
}

function disableRecommendedOffer() {
	$("#lbl_recommendedOffer").addClass("grey-label").parent().find('.hint').addClass("grey");
	
    $("#recommendedStartDate").attr("disabled", "disabled");
    $("#lbl_recommendedStartDate").addClass("grey-label").parent().find('.hint').addClass("grey");
    $("#lbl_recommendedStartDate").html("Provisional Start Date");
    
    $("input[name='recommendedConditionsAvailable']").attr("disabled", "disabled");
    $("#lbl_recommendedConditionsAvailable").addClass("grey-label").parent().find('.hint').addClass("grey");
    $("#lbl_recommendedConditionsAvailable").html("Recommended Offer Type");
    
   	disableConditions();
}

function enableRecommendedOffer() {
	$("#lbl_recommendedOffer").removeClass("grey-label").parent().find('.hint').removeClass("grey");
	
    $("#recommendedStartDate").removeAttr("disabled", "disabled");
    $("#lbl_recommendedStartDate").removeClass("grey-label").parent().find('.hint').removeClass("grey");
    $("#lbl_recommendedStartDate").html("Provisional Start Date<em>*</em>");
    
    $("input[name='recommendedConditionsAvailable']").removeAttr("disabled", "disabled");
    $("#lbl_recommendedConditionsAvailable").removeClass("grey-label").parent().find('.hint').removeClass("grey");
    $("#lbl_recommendedConditionsAvailable").html("Recommended Offer Type<em>*</em>");
    
    recomendedConditionsChange();
}

function recomendedConditionsChange() {
	var conditionsAvailable = $("input[name='recommendedConditionsAvailable']:checked").val();
	if(conditionsAvailable == "true"){
	var selected_radio = $("input[name='confirmedSupervision']:checked").val();
		if (selected_radio == 'true') {
			enableConditions();
		} else {
			disableConditions();
		}
	} else {
		disableConditions();
	}
} 

function disableConditions() {
    $("#recommendedConditions").attr("disabled", "disabled");
    $("#lbl_recommendedConditions").addClass("grey-label").parent().find('.hint').addClass("grey");
    $("#lbl_recommendedConditions").html("Recommended Conditions");
}

function enableConditions() {
    $("#recommendedConditions").removeAttr("disabled", "disabled");
    $("#lbl_recommendedConditions").removeClass("grey-label").parent().find('.hint').removeClass("grey");
    $("#lbl_recommendedConditions").html("Recommended Conditions<em>*</em>");
}

function disableDeclinedSupervisionReason() {
    $("#declinedSupervisionReason").attr("disabled", "disabled");
	$("#declinedSupervisionReason").parent().find('.alert-error').remove();
	$("#declinedSupervisionReason").val('');
	$("#lbl_declinedSupervisionReason").addClass("grey-label").parent().find('.hint').addClass("grey");
	$("#lbl_declinedSupervisionReason").html("Reason"); 
}

function enableDeclinedSupervisionReason() {
    $("#declinedSupervisionReason").removeAttr("disabled", "disabled");
    $("#lbl_declinedSupervisionReason").removeClass("grey-label").parent().find('.hint').removeClass("grey");
    $("#lbl_declinedSupervisionReason").html("Reason<em>*</em>");
}