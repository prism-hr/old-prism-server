$(document).ready(function() {
    
    addToolTips();
    addCounter();
    showFirstQualificationEntryOrExplanationArea();
    
    // --------------------------------------------------------------------------------
    // Close button.
    // --------------------------------------------------------------------------------
	$('#qualificationCloseButton').click(function(){
		$('#qualifications-H2').trigger('click');
		return false;
	});
	
    // -------------------------------------------------------------------------------
    // Clear button.
    // -------------------------------------------------------------------------------
    $('#qualificationClearButton').click(function() {
    	$('input[name="qualificationSendToUcl"]').each(function() {
    		$(this).attr("checked", false);
        });
    	$("#explanationText").val("");
    });
    
    // --------------------------------------------------------------------------------
    // SHOW SELECTED QUALIFICATION
    // --------------------------------------------------------------------------------
    $('a[name="showQualificationLink"]').on("click", function() {
    	$("#explanationText").val("");
    	$('#explanationArea').hide();
        $('a[name="showQualificationLink"]').each(function() {
            $("#" + $(this).attr("toggles")).hide();
        });
        $("#" + $(this).attr("toggles")).show();
    });
    
    // --------------------------------------------------------------------------------
    // ONLY ALLOW A MAXIMUM OF 2 QUALIFICATIONS TO BE SELECTED AT THE SAME TIME
    // --------------------------------------------------------------------------------
    $('input[name="qualificationSendToUcl"]:checkbox').on("change", function() {
        var maxAllowed = 2;
        var checked = $('input[name="qualificationSendToUcl"]:checked').size();
        if (checked > maxAllowed) {
            $(this).attr("checked", false);
        }
    });

    // --------------------------------------------------------------------------------
    // POST QUALIFICATION DATA
    // --------------------------------------------------------------------------------
    $('#qualificationSaveButton').on("click", function() {
        postQualificationsData();
    });
    
});

function showFirstQualificationEntryOrExplanationArea() {
	
	if($('#showExplanationText').val() == 'yes' || $("#explanationText").val() != '') {
		$('#explanationArea').show();
		return false;
	}
	
	qualifications = $('input[name="qualificationSendToUcl"]:checkbox');
	for(var i = 0 ; i < qualifications.length ; i++){
		qualificationCheckbox = qualifications[i];
		
		if(!$(qualificationCheckbox).attr("disabled")){
			var qualificationId = $(qualificationCheckbox).attr("value");
			$('#qualification_' + qualificationId).show();
			return false;
		}
	}
    
	$('#explanationArea').show();
}

function postQualificationsData() {
    $('#ajaxloader').show();
    var qualificationsSendToPortico = collectQualificationsSendToPortico();
    data = {
        applicationId : $('#applicationId').val(),
        qualificationsSendToPortico: JSON.stringify(qualificationsSendToPortico),
        cacheBreaker: new Date().getTime()
    };
    
    if($('#explanationArea').is(':visible')){
    	data.emptyQualificationsExplanation = $("#explanationText").val();
    }
    $.ajax({
        type : 'POST',
        statusCode : {
            401 : function() { window.location.reload(); },
            500 : function() { window.location.href = "/pgadmissions/error"; },
            404 : function() { window.location.href = "/pgadmissions/404"; },
            400 : function() { window.location.href = "/pgadmissions/400"; },
            403 : function() { window.location.href = "/pgadmissions/404"; }
        },
        url : "/pgadmissions/approval/postQualificationsData",
        data :  data,
        success : function(data) {
        	$("#qualificationsSection").html(data);
        },
        complete : function() {
            $('#ajaxloader').fadeOut('fast');
        }
    });
}

function collectQualificationsSendToPortico(){
    qualifications = new Array();
    
    $('input[name="qualificationSendToUcl"]:checkbox').each(function() {
        var checked = $(this).attr("checked");
        if (checked) {
        	qualifications.push($(this).val());
        }
    });
    return qualifications;
}
