$(document).ready(function() {
    
    addToolTips();
    bindRatings();
    bindFileUploaders();
    
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
		$('#editedRefereeId').val('newReferee');
		clearRefereeFormErrors();
		clearRefereeForm($("#referee_newReferee"));
		$('html,body').animate({ scrollTop: $('#referee-H2').offset().top }, 'fast');
	});
    
    // --------------------------------------------------------------------------------
    // Close button.
    // --------------------------------------------------------------------------------
	$('#refereeCloseButton').live("click", function() {
		$('#referee_newReferee').parent().find('*[id*=referee_]:visible').hide();
		$('html,body').animate({ scrollTop: $('#referee-H2').offset().top }, 'fast');
	});
	
    // -------------------------------------------------------------------------------
    // Clear button.
    // -------------------------------------------------------------------------------
    $('#refereeClearButton').live("click", function() {
        clearRefereeFormErrors();
        
        var refereeBeingEdited = null;
        
        $("div[id^=referee_]").each(function() {
    		if (this.style.display != "none") {
    			refereeBeingEdited = $(this).attr("id");
    		};
    	});
        
        if (refereeBeingEdited == null) {
        	$('input[name="refereeSendToUcl"]').each(function() {
        		$(this).removeAttr("checked");
            });
        }
        else {
        	clearRefereeFormErrors();
        	clearRefereeForm($("#" + refereeBeingEdited));
        }
        
		$('html,body').animate({ scrollTop: $('#referee-H2').offset().top }, 'fast');
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
    $('#refereeSaveButton').live("click", function(event) {
        postRefereesData(true, false, event);
    });

    // --------------------------------------------------------------------------------
    // POST ADD REFERENCE DATA
    // --------------------------------------------------------------------------------
	$(".addReferenceButton").live("click", function(event) {
		postRefereesData(false, true, event);
    });
    
    // --------------------------------------------------------------------------------
    // EDIT REFERENCE DATA
    // --------------------------------------------------------------------------------
	attachReferenceEditListeners();
    attachFileUploaders();
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
     	$(this).next().remove();
     	addCounter();
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
    
    form.find(".icon-star.hover").each(function() {
    	$(this).removeClass("icon-star hover").addClass("icon-star-empty");
    });
    
    form.find(".icon-thumbs-down.hover").each(function() {
    	$(this).removeClass("hover");
    });
}

function showProperRefereeEntry() {
	var $refereeId = $('#editedRefereeId').val();
	
	if($refereeId == "newReferee") {
		$("#referee_newReferee").show();
	} else {
		$('a[name="showRefereeLink"]').each(function() {
			if($refereeId == $(this).attr("toggles").replace("referee_", "")) {
				$("#" + $(this).attr("toggles")).show();
			}
		});
	}
	attachReferenceEditListeners();
    attachFileUploaders();
}

function attachReferenceEditListeners () {
    $('.editReferenceButton').each(function() {
    	$(this).live("click", function(event) {
    		editReferenceData(event);
        });
    });
}

function attachFileUploaders() {
    $(".file").each(function() {
    	watchUpload($(this));
    });
}

function postRefereesData(postSendToPorticoData, forceSavingReference, event) {
    var refereeId = $('#editedRefereeId').val();
    
    var suitableForUCL = "";
    if ($('input:radio[name=suitableForUCL_' + refereeId + ']:checked').length > 0) {
        suitableForUCL = $('input:radio[name=suitableForUCL_' + refereeId + ']:checked').val();
    }

    var suitableForProgramme = "";
    if ($('input:radio[name=suitableForProgramme_' + refereeId + ']:checked').length > 0) {
        suitableForProgramme = $('input:radio[name=suitableForProgramme_' + refereeId + ']:checked').val();
    }
    
    var $ref_doc_upload_field = $('input:file[id=referenceDocument_' + refereeId + ']');
    var $ref_doc_container  = $ref_doc_upload_field.closest('div.field');
    var $ref_doc_hidden     = $ref_doc_container.find('.uploaded-files .file');
    
    var containsRefereeData = false;
	
    postData =  {
            applicationId : $('#applicationId').val(),
            comment: $('#refereeComment_' + refereeId).val(),
            referenceDocument: $ref_doc_hidden.val(),
            suitableForUCL : suitableForUCL,
            suitableForProgramme : suitableForProgramme, 
            applicantRating : $('#applicantRating_' + refereeId).val(),
            editedRefereeId : $('#editedRefereeId').val(),
            cacheBreaker: new Date().getTime(),
            scores : getScores($("#scoring-questions_"+refereeId))
        };
    
    if(refereeId == "newReferee") {
    	containsRefereeData = true;
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
    
    postData['containsRefereeData'] = containsRefereeData;
    postData['forceSavingReference'] = forceSavingReference;
    
	var checkedReferees = collectRefereesSendToPortico();
    
    if(postSendToPorticoData){
    	postData['refereesSendToPortico'] = JSON.stringify(checkedReferees);
    }
    
    postData['scores'] = getScores($("div[id=referee_" + refereeId + "]"));

    $('#ajaxloader').show();
    $.ajax({
        type : 'POST',
        statusCode : {
            401 : function() { window.location.reload(); },
            500 : function() { window.location.href = "/pgadmissions/error";},
            404 : function() { window.location.href = "/pgadmissions/404";},
            400 : function() { window.location.href = "/pgadmissions/400";},
            403 : function() { window.location.href = "/pgadmissions/404";}
        },
        url : $postRefereesDataUrl,
        data : postData,
        success : function(data) {
        	if ($("#referee_" + refereeId).find("button[id=addReferenceButton_" + refereeId + "]").length == 1) {
        		// Process the new referee entry
    	        var doRefreshPane = false;
    	        var trySelectForPortico = false;
				if ((event.target.id).indexOf("addReferenceButton") == 0) {
					// We are adding something new - refresh the panel
					doRefreshPane = true;
					if (data.indexOf("alert alert-error") == -1) {
						trySelectForPortico = true;
					}
				}
				else if ((event.target.id) == "refereeSaveButton") {
					// We tried to save something new
					if (data.indexOf("alert alert-error") != -1) {
						// We got some errors from the server
						doRefreshPane = true;
					}
					else {
						trySelectForPortico = true;
					}
				}
				$("#referencesSection").html(data);
				if (doRefreshPane == true) {
		    		showProperRefereeEntry();
				}
				else {
					$("#referee_" + refereeId).hide();
				}
				if (trySelectForPortico) {
	    			if ($('input[name="refereeSendToUcl"]:checked').length < 2 &&
	    				$("#refereeSendToUcl_" + refereeId).attr('disabled') != 'disabled') {
	    				$("#refereeSendToUcl_" + refereeId).attr('checked', 'checked');
	    				$("#refereeSendToUcl_" + refereeId).removeAttr('disabled');
	    			}
				}
				else {
					$("#refereeSendToUcl_" + refereeId).removeAttr('checked');
	    			$("#refereeSendToUcl_" + refereeId).attr('disabled', 'disabled');
				}
	            addCounter();
				addToolTips();
				bindRatings();
				bindFileUploaders();
        	}
        	else {
        		// Process the edited referee entry
        		editReferenceData(event);
        	}
        },
        complete : function() {
        	$('#ajaxloader').fadeOut('fast');
        }
    });
}

function editReferenceData(event) {
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
        applicantRating : $('#applicantRating_' + refereeId).val(),
        scores: getScores($("div[id=referee_" + refereeId + "]"))
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
        url : $editRefereesDataUrl,
        data :  postData,
        success : function(data) {
        	clearRefereeFormErrors();
        	if (data.success == "false") {
        		if (data.comment != null) {
        			$("#field_container_refereeComment_" + refereeId).append('<div class="alert alert-error"> <i class="icon-warning-sign"></i>' + data.comment + '</div>');
    				$("#refereeSendToUcl_" + refereeId).removeAttr('checked');
        			$("#refereeSendToUcl_" + refereeId).attr('disabled', 'disabled');
        		}
        		for (var i = 0; i < $("div[id=referee_" + refereeId + "]").find("div[id^=question_container_]").length; i ++) {
        			if (data["scores[" + i + "]"] != undefined) {
        				$("div[id=referee_" + refereeId + "]").find($("div[id=question_container_" + i + "]")).append('<div class="alert alert-error"> <i class="icon-warning-sign"></i>' + data["scores[" + i + "]"] + '</div>');
        			}	
        		}
        	}
    		else {
    			if ($('input[name="refereeSendToUcl"]:checked').length < 2) {
    				$("#refereeSendToUcl_" + refereeId).attr('checked', 'checked');
    				$("#refereeSendToUcl_" + refereeId).removeAttr('disabled');
    			}
    			if ((event.target.id) == "refereeSaveButton") {
    				$("#referee_" + refereeId).hide();
    			}
    		}
        },
        complete : function() {
            $('#ajaxloader').fadeOut('fast');
        },
        
    });
}

function collectRefereesSendToPortico(){
    var referees = new Array();
    $('input[name="refereeSendToUcl"]:checkbox').each(function() {
        var checked = $(this).attr("checked");
        if (checked) {
        	referees.push($(this).val());
        }
    });
    return referees;
}

function bindRatings() {
	$items = $('.rating-list li i');
	$($items).click(function() {
		rating('select', $(this) );
	});
	$($items).hover(function() {
	    rating('hover', $(this));
	});
	$items.mouseout(function(){
		rating('out', $(this));
	});
	rating('check', $(this));
}

function bindFileUploaders() {
    $(".file").each(function() {
    	watchUpload($(this));
    });
}