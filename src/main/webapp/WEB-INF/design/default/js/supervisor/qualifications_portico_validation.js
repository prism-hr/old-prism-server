$(document).ready(function() {
    
    addToolTips();
    
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
    });
    
    // --------------------------------------------------------------------------------
    // SHOW SELECTED QUALIFICATION
    // --------------------------------------------------------------------------------
    $('a[name="showQualificationLink"]').on("click", function() {
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
	var anyQualificationChecked = false;
	$('input[name="qualificationSendToUcl"]:checkbox').each(function() {
        var checked = $(this).attr("checked");
        if (checked) {
        	anyQualificationChecked = true;
        }
    });
	
	if(!anyQualificationChecked) {
		$('#explanationArea').show();
		return false;
	}
	
    $('a[name="showQualificationLink"]').each(function() {
        $("#" + $(this).attr("toggles")).show();
        return false;
    });
}

function postQualificationsData() {
    $('#qualificationsSection > div').append('<div class="ajax" />');
    var sendToPorticoData = collectQualificationsSendToPortico();
    var explanation = $("#explanationText").val();
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
        data :  {
            applicationId : $('#applicationId').val(),
            sendToPorticoData: JSON.stringify(sendToPorticoData),
            explanation: explanation,
            cacheBreaker: new Date().getTime()
        },
        success : function(data) {
        	$("#qualificationsSection").html(data);
        },
        complete : function() {
            $('#qualificationsSection div.ajax').remove();
        }
    });
}

function collectQualificationsSendToPortico(){
    var sendToPorticoData = {
            qualifications : new Array(),
    };
    
    $('input[name="qualificationSendToUcl"]:checkbox').each(function() {
        var checked = $(this).attr("checked");
        if (checked) {
        	sendToPorticoData.qualifications.push($(this).val());
        }
    });
    return sendToPorticoData;
}