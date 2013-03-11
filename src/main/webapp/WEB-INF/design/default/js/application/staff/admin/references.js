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
    	
    	// show new referee
    	$('a[name="showRefereeLink"]').each(function() {
            $("#" + $(this).attr("toggles")).hide();
        });
        $("#referee_newReferee").show();
        $('#editedRefereeId').val("newReferee");
        
        clearRefereeFormErrors();
    });
    
    // --------------------------------------------------------------------------------
    // SHOW SELECTED REFEREE
    // --------------------------------------------------------------------------------
    $('a[name="showRefereeLink"]').on("click", function() {
        $('a[name="showRefereeLink"]').each(function() {
            $("#" + $(this).attr("toggles")).hide();
        });
        $("#referee_newReferee").hide();
        $("#" + $(this).attr("toggles")).show();
        $('#editedRefereeId').val($(this).attr("toggles").replace("referee_", "")); // set the id of the referee we are looking at
        clearRefereeFormErrors();
        if(!$(this).attr("responded")){
        	var refereeId = $('#editedRefereeId').val();
        	clearRefereeForm($("#referee_" + refereeId));
        }
        
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
        postRefereesData(true, false);
    });

    // --------------------------------------------------------------------------------
    // POST ADD REFERENCE DATA
    // --------------------------------------------------------------------------------
    $('button[id="addReferenceButton"]').each(function() {
    	$(this).on("click", function() {
    		postRefereesData(false, true);
        });
    });
    
    // --------------------------------------------------------------------------------
    // EDIT REFERENCE DATA
    // --------------------------------------------------------------------------------
    $('button[id="editReferenceButton"]').each(function() {
    	$(this).on("click", function() {
    		editReferenceData(false, true);
        });
    });
    
    $("input:file").each(function() {
    	watchUpload($(this));
    });
});

function clearRefereeFormErrors() {
    $("#referencesSection").find('span.invalid').remove(); // remove all previous form errors
}

function clearRefereeForm(form) {
    form.find("input:text").each(function() {
        $(this).val("");
    });
    
    form.find("input[type=email]").each(function() {
        $(this).val("");
    });
    
    form.find("textarea").each(function() {
        $(this).val("");
    });
    
    form.find("input:radio").each(function() {
        $(this).attr('checked', false);
    });

    form.find("select").each(function() {
        $(this).val("");
    });
    
    form.find("input:file").each(function() {
    	var $container  = $(this).parent('div.field');
    	$deleteButton = $container.find('a.button-delete');

    	var $hidden  = $container.find('input.file');
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
		// display new one
		$("#referee_newReferee").show();
		$('#editedRefereeId').val("newReferee");
	} else {
		// referee ID already set, display this one
		$('a[name="showRefereeLink"]').each(function() {
			if($refereeId == $(this).attr("toggles").replace("referee_", "")){
				$("#" + $(this).attr("toggles")).show();
			}
		});
	}
}

function postRefereesData(postSendToPorticoData, forceSavingReference) {
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
	
    postData =  {
        applicationId : $('#applicationId').val(),
        refereesSendToPortico: JSON.stringify(refereesSendToPortico),
        comment: $('#refereeComment_' + refereeId).val(),
        referenceDocument: $ref_doc_hidden.val(),
        suitableForUCL : suitableUCL,
        suitableForProgramme : suitableForProgramme, 
        editedRefereeId : $('#editedRefereeId').val(),
        cacheBreaker: new Date().getTime()
    };
    
    if(forceSavingReference){
    	postData['forceSavingReference'] = true;
    }
    
    if(postSendToPorticoData){
    	var refereesSendToPortico = collectRefereesSendToPortico();
    	postData['refereesSendToPortico'] = JSON.stringify(refereesSendToPortico);
    }
    
    if(refereeId == "newReferee"){
        postData['containsRefereeData'] = true;
		postData['firstname'] = $("#firstname_" + refereeId).val();
		postData['lastname'] =$("#lastname_" + refereeId).val();
		postData['jobEmployer'] = $("#employer_" + refereeId).val(); 
		postData['jobTitle'] = $("#position_" + refereeId).val();
		postData['addressLocation.address1'] = $("#address_location1_" + refereeId).val();
		postData['addressLocation.address2'] = $("#address_location2_" + refereeId).val();
		postData['addressLocation.address3'] = $("#address_location3_" + refereeId).val();
		postData['addressLocation.address4'] = $("#address_location4_" + refereeId).val();
		postData['addressLocation.address5'] = $("#address_location5_" + refereeId).val();
		postData['addressLocation.country'] = $("#address_country_" + refereeId).val();
		postData['email'] = $("#email_" + refereeId).val();
		postData['phoneNumber'] = $("#phoneNumber_" + refereeId).val();
		postData['messenger'] = $("#messenger_" + refereeId).val();
	} else {
		postData['editedRefereeId'] = refereeId;
	}
    
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
        url : $postRefereesDataUrl,
        data :  postData,
        success : function(data) {
        	$("#referencesSection").html(data);
        	$("#referee_" + $("#editedRefereeId").val()).show();
        	if($closeReferenceSectionAfterSaving && postSendToPorticoData && $('#anyReferenceErrors').val() == 'false'){
        		$('#referee-H2').trigger('click');
        	}
            
        },
        complete : function() {
            $('#referencesSection div.ajax').remove();
        }
    });
}

function editReferenceData() {
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
	
    postData =  {
		editedRefereeId : refereeId,
        applicationId : $('#applicationId').val(),
        comment: $('#refereeComment_' + refereeId).val(),
        referenceDocument: $ref_doc_hidden.val(),
        suitableForUCL : suitableUCL,
        suitableForProgramme : suitableForProgramme, 
    };
    
    $('#referencesSection > div').append('<div class="ajax" />');
    $.ajax({
    	dataType: "json",
        type : 'POST',
        statusCode : {
            401 : function() { window.location.reload(); },
            500 : function() { window.location.href = "/pgadmissions/error"; },
            404 : function() { window.location.href = "/pgadmissions/404"; },
            400 : function() { window.location.href = "/pgadmissions/400"; },
            403 : function() { window.location.href = "/pgadmissions/404"; }
        },
        url : "/pgadmissions/editApplicationFormAsProgrammeAdmin/editReferenceData",
        data :  postData,
        success : function(data) {
        	clearRefereeFormErrors();
        	if(data.success == "false"){
        		if(data.comment != null){
        			$("#commentError_" + refereeId).html('<span class="invalid">' + data.comment + '</span>');
        		}
        	}
        },
        complete : function() {
            $('#referencesSection div.ajax').remove();
        },
        
    });
}

function collectRefereesSendToPortico(){
    referees = new Array();
    
    $('input[name="refereeSendToUcl"]:checkbox').each(function() {
        var checked = $(this).attr("checked");
        if (checked) {
        	referees.push($(this).val());
        }
    });
    return referees;
}
