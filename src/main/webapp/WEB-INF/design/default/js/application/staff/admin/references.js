$(document).ready(function() {
    
    addToolTips();
    
    // if editedRefereeId is empty assume that new referee is the current one
	if($('#editedRefereeId').val() == "") {
		$('#editedRefereeId').val("newReferee");
	}
    
    // --------------------------------------------------------------------------------
    // New reference button.
    // --------------------------------------------------------------------------------
    $('#newReferenceButton').live("click", function () {
		$('#referee_newReferee').parent().find('*[id*=referee_]:visible').hide();
		$('#referee_newReferee').show();
		$('#referee_newReferee input').val('');
		$('#refereeComment_newReferee').attr('value', '');
		$('#editedRefereeId').val('newReferee');
		$('#suitableUCL_true, #suitableUCL_false, #suitableProgramme_true, #suitableProgramme_false').prop('checked', false);
		clearRefereeFormErrors();
	});
    
    // --------------------------------------------------------------------------------
    // Close button.
    // --------------------------------------------------------------------------------
	$('#refereeCloseButton').live("click", function(){
		$('#referee_newReferee').parent().find('*[id*=referee_]:visible').hide();
		$('html,body').animate({ scrollTop: $('#referee-H2').offset().top }, 'fast');
	});
	
    // -------------------------------------------------------------------------------
    // Clear button.
    // -------------------------------------------------------------------------------
    $('#refereeClearButton').live("click", function() {
    	$('input[name="refereeSendToUcl"]').each(function() {
    		$(this).attr("checked", false);
        });
    	
    	// show new referee
    	$('a[name="showRefereeLink"]').each(function() {
            $("#" + $(this).attr("toggles")).hide();
        });
        //$("#referee_newReferee").show();
        $('#editedRefereeId').val("newReferee");
        
        clearRefereeFormErrors();
    });
    
    // --------------------------------------------------------------------------------
    // SHOW SELECTED REFEREE
    // --------------------------------------------------------------------------------
    $('a[name="showRefereeLink"]').live("click", function() {
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
    $('input[name="refereeSendToUcl"]:checkbox').live("change", function() {
        var maxAllowed = 2;
        var checked = $('input[name="refereeSendToUcl"]:checked').size();
        if (checked > maxAllowed) {
            $(this).attr("checked", false);
        }
    });
    
    // --------------------------------------------------------------------------------
    // POST SEND TO PORTICO REFEREE DATA
    // --------------------------------------------------------------------------------
    $('#refereeSaveButton').live("click", function() {
        postRefereesData(true, false);
    });

    // --------------------------------------------------------------------------------
    // POST ADD REFERENCE DATA
    // --------------------------------------------------------------------------------
	$(".addReferenceButton").live("click", function() {
		postRefereesData(false, true);
    });
    
    // --------------------------------------------------------------------------------
    // EDIT REFERENCE DATA
    // --------------------------------------------------------------------------------
    $('.editReferenceButton').each(function() {
    	$(this).live("click", function() {
    		editReferenceData(false, true);
        });
    });
    
    $(".file").each(function() {
    	watchUpload($(this));
    });
});

function clearRefereeFormErrors() {
    $("#referencesSection").find('div.alert-error').remove(); // remove all previous form errors
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
		addCounter();
		$(this).parent().find('.badge').html('2000 Characters left');
    });
    
    form.find("input:radio").each(function() {
        $(this).attr('checked', false);
    });

    form.find("select").each(function() {
        $(this).val("");
    });
    
    form.find("input.file").each(function() {
    	var $container  = $(this).closest('div.field');
    	$deleteButton = $container.find('.delete');

    	var $hidden  = $container.find('.uploaded-files .file');
    	$container.find('.uploaded-files li').each(function()
		{
			$(this).remove();
		});
    	
    	$hidden.val(''); // clear field value.
    	$container.removeClass('uploaded');

		$container.find('.fileupload').fileupload('clear');

    });
    
    form.find(".rating-input").each(function() {
    	$(this).val("");
    });
    
}

function showProperRefereeEntry() {
	var $refereeId = $('#editedRefereeId').val(); 
	
	if($refereeId == "newReferee") {
		// show new referee form
		$("#referee_newReferee").show();
	} else {
		// show existing referee form
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
    var $ref_doc_container  = $ref_doc_upload_field.closest('div.field');
    var $ref_doc_hidden     = $ref_doc_container.find('.uploaded-files .file');
	
    postData =  {
        applicationId : $('#applicationId').val(),
        refereesSendToPortico: JSON.stringify(refereesSendToPortico),
        comment: $('#refereeComment_' + refereeId).val(),
        referenceDocument: $ref_doc_hidden.val(),
        suitableForUCL : suitableUCL,
        suitableForProgramme : suitableForProgramme, 
        applicantRating : $('#applicantRating_' + refereeId).val(),
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
		postData['addressLocation.domicile'] = $("#address_country_" + refereeId).val();
		postData['email'] = $("#email_" + refereeId).val();
		postData['phoneNumber'] = $("#phoneNumber_" + refereeId).val();
		postData['messenger'] = $("#messenger_" + refereeId).val();
	} else {
		postData['editedRefereeId'] = refereeId;
	}
    
    var scoreData=getScores($("#scoring-questions_"+refereeId));
    postData['scores']=scoreData;
    
	var checkedReferees = collectRefereesSendToPortico();
	var uncheckedReferees = collectRefereesNotSendToPortico();
	var editedReferee =$('#editedRefereeId').val();

    
    $('#ajaxloader').show();
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
        	if($closeReferenceSectionAfterSaving && postSendToPorticoData && $('#anyReferenceErrors').val() == 'false'){
        		$('#referee-H2').trigger('click');
        	}
            addCounter();
            refreshRefereesTable(checkedReferees, editedReferee, uncheckedReferees);
			showProperRefereeEntry();
			addToolTips();
        },
        complete : function() {
           $('#ajaxloader').fadeOut('fast');
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
    var $ref_doc_container  = $ref_doc_upload_field.closest('div.field');
    var $ref_doc_hidden     = $ref_doc_container.find('.uploaded-files .file');	
	
	
    postData =  {
		editedRefereeId : refereeId,
        applicationId : $('#applicationId').val(),
        comment: $('#refereeComment_' + refereeId).val(),
        referenceDocument: $ref_doc_hidden.val(),
        suitableForUCL : suitableUCL,
        suitableForProgramme : suitableForProgramme,
        applicantRating : $('#applicantRating_' + refereeId).val()
    };

    $('#ajaxloader').show();
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
        			$("#commentError_" + refereeId).html('<div class="alert alert-error"> <i class="icon-warning-sign"></i>' + data.comment + '</div>');
        		}
        	}
        },
        complete : function() {
            $('#ajaxloader').fadeOut('fast');
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

function collectRefereesNotSendToPortico(){
	referees = new Array();
	
	$('input[name="refereeSendToUcl"]:checkbox').each(function() {
		var checked = $(this).attr("checked");
		if (!checked && $(this).val() != $('#editedRefereeId').val()) {
			referees.push($(this).val());
		}
	});
	return referees;
}

function refreshRefereesTable(selectedValues, newRefereeValue, unchekedValues) {
	if (selectedValues.length == 2) {
		$("input[value="+selectedValues[i]+"]").attr("checked", false); 
		return;
	}
	
	if($('#anyReferenceErrors').val() == 'false') {
		$("input[value=" + newRefereeValue +"]").attr("checked", true); 
	}
	
	for (var i = 0; i<unchekedValues.length; i++) {
		$("input[value=" + unchekedValues[i] +"]").attr("checked", false); 
	}
}
