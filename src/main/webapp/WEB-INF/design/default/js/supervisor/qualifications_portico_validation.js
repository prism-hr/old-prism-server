$(document).ready(function() {
    
    addToolTips();
    
    var checked = $('input[name="qualificationSendToUcl"]:checked').size();
    if (checked > 0) {
    	disableExplanation();
    }
    else {
        addCounter();
    }
    
    // --------------------------------------------------------------------------------
    // Close button.
    // --------------------------------------------------------------------------------
	$('#qualificationCloseButton').click(function(){
		$('#qualificationsSection').find('*[id*=qualification_]:visible').hide();
		$('html,body').animate({ scrollTop: $('#qualifications-H2').offset().top }, 'fast');
		$('#explanationArea').show();
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
    	$('#explanationArea').hide();
        $('a[name="showQualificationLink"]').each(function() {
            $("#" + $(this).attr("toggles")).hide();
        });
        $("#" + $(this).attr("toggles")).show();
    });
    
    // --------------------------------------------------------------------------------
    // TOGGLE CONTROLS BASED UPON NUMBER OF QUALIFICATIONS SELECTED
    // --------------------------------------------------------------------------------
    $('input[name="qualificationSendToUcl"]:checkbox').on("change", function() {
        var maxAllowed = 2;
        var checked = $('input[name="qualificationSendToUcl"]:checked').size();
        addCounter();       
        if (checked == 0) {
            $('#explanationTextLabel').removeClass("grey-label").parent().find('.hint').removeClass("grey");
        	$('#explanationText').prop('disabled', false);
        }
        else {
        	disableExplanation();
            if (checked > maxAllowed) {
                $(this).attr("checked", false);
            }
        }
    });

    // --------------------------------------------------------------------------------
    // POST QUALIFICATION DATA
    // --------------------------------------------------------------------------------
    $('#qualificationSaveButton').on("click", function() {
        postQualificationsData();
    });
    
});

function postQualificationsData() {
    $('#ajaxloader').show();
    var qualificationsSendToPortico = collectQualificationsSendToPortico();
    data = {
        applicationId : $('#applicationId').val(),
        applicationNumber: $('#applicationId').val(),
        qualificationsSendToPortico: JSON.stringify(qualificationsSendToPortico),
        cacheBreaker: new Date().getTime()
    };
    
    if($.trim($("#explanationText").val()).length > 0){
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

function disableExplanation(){
	$('#explanationTextLabel').addClass("grey-label").parent().find('.hint').addClass("grey");
 	$('#explanationText').val("");
 	$('#explanationText').prop('disabled', 'disabled');
 	$('.counter badge').remove();
 	addCounter();
}