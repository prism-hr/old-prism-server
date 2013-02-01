$(document).ready(function() {
    
    addToolTips();
    
    showProperRefereeEntry();
    
    // --------------------------------------------------------------------------------
    // Close button.
    // --------------------------------------------------------------------------------
	$('#refereeCloseButton').click(function(){
		$('#referee-H2').trigger('click');
		return false;
	});
	
    // -------------------------------------------------------------------------------
    // Clear button.
    // -------------------------------------------------------------------------------
    $('#refereeClearButton').click(function() {
    	$('input[name="refereeSendToUcl"]').each(function() {
    		$(this).attr("checked", false);
        });
    });
    
    // --------------------------------------------------------------------------------
    // SHOW SELECTED REFEREE
    // --------------------------------------------------------------------------------
    $('a[name="showRefereeLink"]').on("click", function() {
        $('a[name="showRefereeLink"]').each(function() {
            $("#" + $(this).attr("toggles")).hide();
        });
        $("#" + $(this).attr("toggles")).show();
        $('#editedRefereeId').val($(this).attr("toggles").replace("referee_", "")); // set the id of the referee we are looking at
        clearRefereeFormErrors();
        clearRefereeForm();
    });
    
    // --------------------------------------------------------------------------------
    // ONLY ALLOW A MAXIMUM OF 2 REFEREES TO BE SELECTED AT THE SAME TIME
    // --------------------------------------------------------------------------------
    $('input[name="refereeSendToUcl"]:checkbox').on("change", function() {
        var maxAllowed = 2;
        var checked = $('input[name="refereeSendToUcl"]:checked').size();
        if (checked > maxAllowed) {
            $(this).attr("checked", false);
        }
    });
    
    // --------------------------------------------------------------------------------
    // POST SEND TO PORTICO REFEREE DATA
    // --------------------------------------------------------------------------------
    $('#refereeSaveButton').on("click", function() {
        postRefereesData();
    });

    // --------------------------------------------------------------------------------
    // POST ADD REFERENCE DATA
    // --------------------------------------------------------------------------------
    $('button[id="addReferenceButton"]').each(function() {
    	$(this).on("click", function() {
            postAddReferenceData();
        });
    });
    
    $("input:file").each(function() {
    	watchUpload($(this));
    });
});

function clearRefereeFormErrors() {
    $("#referencesSection").find('span.invalid').remove(); // remove all previous form errors
}

function clearRefereeForm() {
    $("input:text").each(function() {
        $(this).val("");
    });
    
    $("textarea").each(function() {
        $(this).val("");
    });
    
    $("input:radio").each(function() {
        $(this).attr('checked', false);
    });
    
    $("input:file").each(function() {
    	var $container  = $(this).parent('div.field');
    	$deleteButton = $container.find('a.button-delete');

    	// TODO deleting on the server is not working
    	// for some reason $hidden.val() returns empty string even if the value is set
    	var $hidden  = $container.find('input.file');
    	deleteUploadedFile($hidden);
    	
    	$container.find('span a').each(function()
		{
			$(this).remove();
		});
    	
    	$hidden.val(''); // clear field value.
    	$container.removeClass('uploaded');

		var newField = $container.find('input.full');
		newField.val("");
    });
    
}

function showProperRefereeEntry() {
	var $refereeId = $('#editedRefereeId').val(); 
	if($refereeId == "") {
		// referee ID not set yet, display first one
	    $('a[name="showRefereeLink"]').each(function() {
	        $("#" + $(this).attr("toggles")).show();
	        $('#editedRefereeId').val($(this).attr("toggles").replace("referee_", ""));
	        return false;
	    });
	} else {
		// referee ID already set, display this one
		$('a[name="showRefereeLink"]').each(function() {
			if($refereeId == $(this).attr("toggles").replace("referee_", "")){
				$("#" + $(this).attr("toggles")).show();
			}
		});
	}
}

function postRefereesData() {
    var refereeId = $('#editedRefereeId').val();
    
    var suitableUCL = "";
    if ($('input:radio[name=suitableForUCL_' + refereeId + ']:checked').length > 0) {
        suitableUCL = $('input:radio[name=suitableForUCL_' + refereeId + ']:checked').val();
    }

    var suitableForProgramme = "";
    if ($('input:radio[name=suitableForProgramme_' + refereeId + ']:checked').length > 0) {
        suitableForProgramme = $('input:radio[name=suitableForProgramme_' + refereeId + ']:checked').val();
    }
    
    var $ref_doc_upload_field = $('input:file[id=referenceDocument_' + refereeId + ']');
    var $ref_doc_container  = $ref_doc_upload_field.parent('div.field');
    var $ref_doc_hidden     = $ref_doc_container.find('span input');
	
    var sendToPorticoData = {
            referees : new Array()
    };
    
    $('input[name="refereeSendToUcl"]:checkbox').each(function() {
        var checked = $(this).attr("checked");
        if (checked) {
        	sendToPorticoData.referees.push($(this).val());
        }
    });
    
    $('#referencesSection > div').append('<div class="ajax" />');
    $.ajax({
        type : 'POST',
        statusCode : {
            401 : function() { window.location.reload(); },
            500 : function() { window.location.href = "/pgadmissions/error"; },
            404 : function() { window.location.href = "/pgadmissions/404"; },
            400 : function() { window.location.href = "/pgadmissions/400"; },
            403 : function() { window.location.href = "/pgadmissions/404"; }
        },
        url : "/pgadmissions/editApplicationFormAsProgrammeAdmin/postRefereesData",
        data :  {
            applicationId : $('#applicationId').val(),
            sendToPorticoData: JSON.stringify(sendToPorticoData),
            comment: $('#refereeComment_' + refereeId).val(),
            referenceDocument: $ref_doc_hidden.val(),
            suitableForUCL : suitableUCL,
            suitableForProgramme : suitableForProgramme, 
            editedRefereeId : $('#editedRefereeId').val(),
            cacheBreaker: new Date().getTime()
        },
        success : function(data) {
        	$("#referencesSection").html(data);
        	$("#referee_" + $("#editedRefereeId").val()).show();
        	if($('#anyReferenceErrors').val() == 'false'){
        		$('#referee-H2').trigger('click');
        	}
            
        },
        complete : function() {
            $('#referencesSection div.ajax').remove();
        }
    });
}

function postAddReferenceData() {
    var refereeId = $('#editedRefereeId').val();
    
    var suitableUCL = "";
    if ($('input:radio[name=suitableForUCL_' + refereeId + ']:checked').length > 0) {
        suitableUCL = $('input:radio[name=suitableForUCL_' + refereeId + ']:checked').val();
    }

    var suitableForProgramme = "";
    if ($('input:radio[name=suitableForProgramme_' + refereeId + ']:checked').length > 0) {
        suitableForProgramme = $('input:radio[name=suitableForProgramme_' + refereeId + ']:checked').val();
    }
    
    var $ref_doc_upload_field = $('input:file[id=referenceDocument_' + refereeId + ']');
    var $ref_doc_container  = $ref_doc_upload_field.parent('div.field');
    var $ref_doc_hidden     = $ref_doc_container.find('span input');
    
    $('#referencesSection > div').append('<div class="ajax" />');
    $.ajax({
        type : 'POST',
        statusCode : {
            401 : function() { window.location.reload(); },
            500 : function() { window.location.href = "/pgadmissions/error"; },
            404 : function() { window.location.href = "/pgadmissions/404"; },
            400 : function() { window.location.href = "/pgadmissions/400"; },
            403 : function() { window.location.href = "/pgadmissions/404"; }
        },
        url : "/pgadmissions/editApplicationFormAsProgrammeAdmin/postReference",
        data :  {
            applicationId : $('#applicationId').val(),
            comment: $('#refereeComment_' + refereeId).val(),
            referenceDocument: $ref_doc_hidden.val(),
            suitableForUCL : suitableUCL,
            suitableForProgramme : suitableForProgramme, 
            editedRefereeId : $('#editedRefereeId').val(),
            cacheBreaker: new Date().getTime()
        },
        success : function(data) {
        	$("#referencesSection").html(data);
        	$("#referee_" + $("#editedRefereeId").val()).show();
        },
        complete : function() {
            $('#referencesSection div.ajax').remove();
        }
    });
}
