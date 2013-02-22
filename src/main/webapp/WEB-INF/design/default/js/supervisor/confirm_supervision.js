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
	$("#lbl_projectDescription").addClass("grey-label");
	
    $("#projectTitle").attr("disabled", "disabled");
    $("#lbl_projectTitle").addClass("grey-label");
    $("#lbl_projectTitle").html("Project Title");
    
    $("#projectAbstract").attr("disabled", "disabled");
    $("#lbl_projectAbstract").addClass("grey-label");
    $("#lbl_projectAbstract").html("Project Abstract");
    
}

function enableProjectDescription() {
	$("#lbl_projectDescription").removeClass("grey-label");
	
    $("#projectTitle").removeAttr("disabled", "disabled");
    $("#lbl_projectTitle").removeClass("grey-label");
    $("#lbl_projectTitle").html("Project Title<em>*</em>");
    
    $("#projectAbstract").removeAttr("disabled", "disabled");
    $("#lbl_projectAbstract").removeClass("grey-label");
    $("#lbl_projectAbstract").html("Project Abstract<em>*</em>");
}

function disableRecommendedOffer() {
	$("#lbl_recommendedOffer").addClass("grey-label");
	
    $("#recommendedStartDate").attr("disabled", "disabled");
    $("#lbl_recommendedStartDate").addClass("grey-label");
    $("#lbl_recommendedStartDate").html("Provisional Start Date");
    
    $("input[name='recommendedConditionsAvailable']").attr("disabled", "disabled");
    $("#lbl_recommendedConditionsAvailable").addClass("grey-label");
    $("#lbl_recommendedConditionsAvailable").html("Recommended Offer Type");
    
   	disableConditions();
}

function enableRecommendedOffer() {
	$("#lbl_recommendedOffer").removeClass("grey-label");
	
    $("#recommendedStartDate").removeAttr("disabled", "disabled");
    $("#lbl_recommendedStartDate").removeClass("grey-label");
    $("#lbl_recommendedStartDate").html("Provisional Start Date<em>*</em>");
    
    $("input[name='recommendedConditionsAvailable']").removeAttr("disabled", "disabled");
    $("#lbl_recommendedConditionsAvailable").removeClass("grey-label");
    $("#lbl_recommendedConditionsAvailable").html("Recommended Offer Type<em>*</em>");
    
    recomendedConditionsChange();
}

function recomendedConditionsChange() {
    var conditionsAvailable = $("input[name='recommendedConditionsAvailable']:checked").val();
    if(conditionsAvailable == "true"){
        enableConditions();
    } else {
        disableConditions();
    }
}

function disableConditions() {
    $("#recommendedConditions").attr("disabled", "disabled");
    $("#lbl_recommendedConditions").addClass("grey-label");
    $("#lbl_recommendedConditions").html("Recommended Conditions");
}

function enableConditions() {
    $("#recommendedConditions").removeAttr("disabled", "disabled");
    $("#lbl_recommendedConditions").removeClass("grey-label");
    $("#lbl_recommendedConditions").html("Recommended Conditions<em>*</em>");
}

function disableDeclinedSupervisionReason() {
    $("#declinedSupervisionReason").attr("disabled", "disabled");
    $("#lbl_declinedSupervisionReason").addClass("grey-label");
    $("#lbl_declinedSupervisionReason").html("Reason");
}

function enableDeclinedSupervisionReason() {
    $("#declinedSupervisionReason").removeAttr("disabled", "disabled");
    $("#lbl_declinedSupervisionReason").removeClass("grey-label");
    $("#lbl_declinedSupervisionReason").html("Reason<em>*</em>");
}