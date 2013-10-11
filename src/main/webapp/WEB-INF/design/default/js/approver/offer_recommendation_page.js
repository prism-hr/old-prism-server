$(document).ready(function() {
	
	bindDatePicker('#recommendedStartDate');
	
    // -------------------------------------------------------------------------------
    // Recommended offer type
    // -------------------------------------------------------------------------------
    $("input[name='recommendedConditionsAvailable']").bind('change', function() {
    	recommendedConditionsChanged();
    });
    
    recommendedConditionsChanged();
 });

function recommendedConditionsChanged(){
    var selected_radio = $("input[name='recommendedConditionsAvailable']:checked").val();
    if (selected_radio == 'true')   {
        enableConditions();
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