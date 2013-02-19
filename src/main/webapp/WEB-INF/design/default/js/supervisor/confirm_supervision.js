$(document).ready(function() {
   
    bindDatePicker('#recommendedStartDate');
   
    // -------------------------------------------------------------------------------
    // Declined supervision reason
    // -------------------------------------------------------------------------------
    $("input[name='confirmedSupervision']").bind('change', function() {
        var selected_radio = $("input[name='confirmedSupervision']:checked").val();
        if (selected_radio == 'false')   {
        	enableDeclinedSupervisionReason();
        } else {
        	disableDeclinedSupervisionReason();
        }
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
    
});

function disableConditions() {
    $("#recommendedConditions").attr("disabled", "disabled");
    $("#recommendedConditions").val("");
    $("#lbl_recommendedConditions").addClass("grey-label");
    $("#lbl_recommendedConditions").html("Recommended Conditions");
}

function enableConditions() {
    $("#recommendedConditions").removeAttr("disabled", "disabled");
    $("#recommendedConditions").removeAttr("readonly", "readonly");
    $("#lbl_recommendedConditions").removeClass("grey-label");
    $("#lbl_recommendedConditions").html("Recommended Conditions<em>*</em>");
}

function disableDeclinedSupervisionReason() {
    $("#declinedSupervisionReason").attr("disabled", "disabled");
    $("#declinedSupervisionReason").val("");
    $("#lbl_declinedSupervisionReason").addClass("grey-label");
    $("#lbl_declinedSupervisionReason").html("Reason");
}

function enableDeclinedSupervisionReason() {
    $("#declinedSupervisionReason").removeAttr("disabled", "disabled");
    $("#declinedSupervisionReason").removeAttr("readonly", "readonly");
    $("#lbl_declinedSupervisionReason").removeClass("grey-label");
    $("#lbl_declinedSupervisionReason").html("Reason<em>*</em>");
}