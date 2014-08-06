$(document).ready(function() {
	
	bindDatePicker('#recommendedStartDate');
	
    // -------------------------------------------------------------------------------
    // Recommended offer type
    // -------------------------------------------------------------------------------
    $("input[name='recommendedConditionsAvailable']").bind('change', function() {
    	recommendedConditionsChanged();
    });
    
    $("#offerRecommendationForm").submit(function(e) {
        e.preventDefault();
        
        var primarySupervisor = $('input[name=primarySupervisor]:checked').val();
        $('#applicationSupervisorsList li').each(function() {
        	var supervisorId = $(this).data('supervisorid');
        	if(supervisorId == primarySupervisor){
        		supervisorId = supervisorId + "|primary";
        	}
            $('#offerRecommendationForm').append("<input name='supervisors' type='hidden' value='" + supervisorId + "'/>");
        });
        
        this.submit(); 
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