$(document).ready(function() {
   
    var checked = $('input[name="qualificationSendToUcl"]:checked').length;
    if (checked > 0) {
    	disableExplanation();
    }
    addCounter();
    addToolTips();
});
// --------------------------------------------------------------------------------
// Close button.
// --------------------------------------------------------------------------------
$(document).on('click', '#qualificationCloseButton', function(){
    $('#qualificationsSection').find('*[id*=qualification_]:visible').hide();
    $('html,body').animate({ scrollTop: $('#qualifications-H2').offset().top }, 'fast');
    $('#explanationArea').show();
});
// -------------------------------------------------------------------------------
// Clear button.
// -------------------------------------------------------------------------------
$(document).on('click', '#qualificationClearButton', function() {
    $('input[name="qualificationSendToUcl"]').each(function() {
        $(this).attr("checked", false);
        $('html,body').animate({ scrollTop: $('#qualifications-H2').offset().top }, 'fast');
    });
    $("#explanationText").val("");
});
// --------------------------------------------------------------------------------
// SHOW SELECTED QUALIFICATION
// --------------------------------------------------------------------------------
$(document).on('click', 'a[name="showQualificationLink"]', function() {
    $('#explanationArea').hide();
    $('a[name="showQualificationLink"]').each(function() {
        $("#" + $(this).attr("toggles")).hide();
    });
    $("#" + $(this).attr("toggles")).show();
});

// --------------------------------------------------------------------------------
// TOGGLE CONTROLS BASED UPON NUMBER OF QUALIFICATIONS SELECTED
// --------------------------------------------------------------------------------
$(document).on('change', 'input[name="qualificationSendToUcl"]:checkbox', function() {
    var maxAllowed = 2;
    var checked = $('input[name="qualificationSendToUcl"]:checked').length;
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
$(document).on("click","#qualificationSaveButton", function () {
    postQualificationsData();
    $('html,body').animate({ scrollTop: $('#qualifications-H2').offset().top }, 'fast');
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
    
    var explanationText = $.trim($("#explanationText").val());
    if(explanationText.length > 0){
    	data.emptyQualificationsExplanation = explanationText;
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
    $('input[name="qualificationSendToUcl"]:checked').each(function() {
        qualifications.push($(this).val());
    });
    return qualifications;
}

function disableExplanation(){
	$('#explanationTextLabel').addClass("grey-label").parent().find('.hint').addClass("grey");
 	$('#explanationText').val("");
 	$('#explanationText').prop('disabled', 'disabled');
 	$('#explanationText').next().remove();
 	addCounter();
}