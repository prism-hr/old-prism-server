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
    
});

function confirmedSupervisionChange() {
    var selected_radio = $("input[name='confirmedSupervision']:checked").val();
    if (selected_radio == 'false')   {
        enableDeclinedSupervisionReason();
        disableProjectDescription();
        disableRecommendedOffer();
    } else if (selected_radio == 'true')   {
        disableDeclinedSupervisionReason();
        enableProjectDescription();
        enableRecommendedOffer();
    } else {
        disableDeclinedSupervisionReason();
        disableProjectDescription();
        disableRecommendedOffer();
    }
}

function disableProjectDescription() {
	$("#lbl_projectDescription").addClass("grey-label").parent().find('.hint').addClass("grey");
	
    $("#projectTitle").attr("disabled", "disabled");
    $("#lbl_projectTitle").addClass("grey-label").parent().find('.hint').addClass("grey");
    $("#lbl_projectTitle").html("Project Title"); 
    
    $("#projectAbstract").attr("disabled", "disabled");
    $("#lbl_projectAbstract").addClass("grey-label").parent().find('.hint').addClass("grey");
    $("#lbl_projectAbstract").html("Project Abstract");
    
}

function enableProjectDescription() {
	$("#lbl_projectDescription").removeClass("grey-label").parent().find('.hint').removeClass("grey");
	
    $("#projectTitle").removeAttr("disabled", "disabled");
    $("#lbl_projectTitle").removeClass("grey-label").parent().find('.hint').removeClass("grey");
    $("#lbl_projectTitle").html("Project Title<em>*</em>");
    
    $("#projectAbstract").removeAttr("disabled", "disabled");
    $("#lbl_projectAbstract").removeClass("grey-label").parent().find('.hint').removeClass("grey");
    $("#lbl_projectAbstract").html("Project Abstract<em>*</em>");
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