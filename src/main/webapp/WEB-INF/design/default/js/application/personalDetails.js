$(document).ready(function() {

    showOrHideNationalityButton();
    if ($("input[name='passportAvailable']:checked").val() == "true") {
    	enablePassportInformation();
    } else {
    	disablePassportInformation();
    }
    
    isRequireVisa();
    
    if ($("input[name='languageQualificationAvailable']:checked").val() == "true") {
    	enableLanguageQualifications();
    } else {
    	disableLanguageQualifications();
    }
    
	var selectedType = $('#qualificationType').val();
	if (selectedType === "IELTS_ACADEMIC") {
	    $('#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect').show();
	    $('#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree').hide();
	    $('#overallScoreSelect').val($("#overallScoreFree").val());
	    $('#readingScoreSelect').val($("#readingScoreFree").val());
	    $('#writingScoreSelect').val($("#writingScoreFree").val());
	    $('#speakingScoreSelect').val($("#speakingScoreFree").val());
	    $('#listeningScoreSelect').val($("#listeningScoreFree").val());
	    $('#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree').val("");
	}
	
	if (selectedType === "OTHER") {
		enableOtherLanguageQualification();
	} else {
		disableOtherLanguageQualification();
	}
    
    if ($('#languageQualificationsTable tr').length > 0) {
        disableLanguageQualifications();
    }
    
    isEnglishFirstLanguage();
    
    $("#acceptTermsPEDValue").val("NO");
    
    // -------------------------------------------------------------------------------
    // Hide or show the national
    // -------------------------------------------------------------------------------
    function showOrHideNationalityButton() {
        numberOfNationalities = $("#my-nationality-div .nationality-item").size();
        if (numberOfNationalities >= 2) {
            $('#addCandidateNationalityButton').hide();
            $('#candidateNationalityCountry').hide();
        } else {
            $('#addCandidateNationalityButton').show();
            $('#candidateNationalityCountry').show();
        }
    }

    // -------------------------------------------------------------------------------
    // Close button.
    // -------------------------------------------------------------------------------
    $('#personalDetailsCloseButton').click(function() {
        $('#personalDetails-H2').trigger('click');
        return false;
    });

    // -------------------------------------------------------------------------------
    // Clear button.
    // -------------------------------------------------------------------------------
    $('#personalDetailsClearButton').click(function() {
        $('#personalDetailsSection > div').append('<div class="ajax" />');
        loadPersonalDetails(true);
    });

    // -------------------------------------------------------------------------------
    // Add a nationality (candidate).
    // -------------------------------------------------------------------------------
    $('#addCandidateNationalityButton').on(
            "click",
            function() {
                var selected = $('#candidateNationalityCountry option:selected').val();
                if (selected != '') {
                    // Find duplicate nationalities.
                    var duplicate = false;
                    $('#my-nationality-div input[type="hidden"]').each(function() {
                        if ($(this).val() == selected) {
                            duplicate = true;
                            return false;
                        }
                    });

                    if (!duplicate) {
                        numberOfNationalities++;
                        var html = '	<div class="nationality-item">' + '		<label class="full">'
                                + $('#candidateNationalityCountry option:selected').text() + '</label>'
                                + "		<input type='hidden' name='candidateNationalities' value='"
                                + $('#candidateNationalityCountry option:selected').val() + "'/>"
                                + '		<button class="button-delete" data-desc="Delete">Delete</button><br/>'
                                + '	</div>';
                        $('#my-nationality-div').append(html);
                        $('#nationality-em').remove();
                        addToolTips();

                        // Reset field.
                        $('#candidateNationalityCountry').val('');
                    }

                    showOrHideNationalityButton();
                }
            });

    // -------------------------------------------------------------------------------
    // Remove a nationality (all sections).
    // -------------------------------------------------------------------------------
    $(document).on('click', '.nationality-item button.button-delete', function() {
        // Clear the corresponding dropdown box if the deleted
        // nationality is the same
        // as the one currently selected.
        var $select = $(this).parent().parent().next('select');
        var $field = $(this).parent().find('input:hidden');
        if ($select.val() == $field.val()) {
            $select.val('');
        }
        $(this).parent().remove();
        
        if ($select.attr('id') == "candidateNationalityCountry") {
            showOrHideNationalityButton();
        }
        
        return false;
    });

    // -------------------------------------------------------------------------------
    // "Accept terms" checkbox.
    // -------------------------------------------------------------------------------
    $("input[name*='acceptTermsPEDCB']").click(function() {
        if ($("#acceptTermsPEDValue").val() == 'YES') {
            $("#acceptTermsPEDValue").val("NO");
        } else {
            $("#acceptTermsPEDValue").val("YES");
        }
    });
    
    // -------------------------------------------------------------------------------
    // Require visa
    // -------------------------------------------------------------------------------
    $("input[name='requiresVisa']").bind('change', function() {
    	isRequireVisa();
    }); 
    
    // -------------------------------------------------------------------------------
    // Passport Available
    // -------------------------------------------------------------------------------
    $("input[name='passportAvailable']").bind('change', function() {
    	var selected_radio = $("input[name='passportAvailable']:checked").val();
    	if (selected_radio == 'true')   {
    		enablePassportInformation();
    	} else {
    		disablePassportInformation();
    	}
    }); 

    // -------------------------------------------------------------------------------
    // Is English your first language?*
    // -------------------------------------------------------------------------------
    $("input[name='englishFirstLanguage']").bind('change', function() {
    	var returnVal = isEnglishFirstLanguage();
    	if (returnVal) {
    	    ajaxDeleteAllLanguageQualifications();
    	}
    });
    
    // -------------------------------------------------------------------------------
    // Language Qualification available
    // -------------------------------------------------------------------------------
    $("input[name='languageQualificationAvailable']").bind('change', function() {
    	$('#languageQualification_div div.alert').remove();
    	var selected_radio = $("input[name='languageQualificationAvailable']:checked").val();
    	if (selected_radio == 'true') {
    		enableLanguageQualifications();
        } else {
        	disableLanguageQualifications();
        	ajaxDeleteAllLanguageQualifications();
        }
    });  
    
    // -------------------------------------------------------------------------------
    // Save button.
    // -------------------------------------------------------------------------------
    $('#personalDetailsSaveButton').on("click", function() {
        $("span[name='nonAcceptedPED']").html('');

        // Attempt saving of "dirty" nationalities.
        $('#addCandidateNationalityButton').trigger('click');
        $('#addMaternalNationalityButton').trigger('click');
        $('#addPaternalNationalityButton').trigger('click');

        postPersonalDetailsData('close');
    });

    // To make uncompleted functionalities disabled
    $(".disabledEle").attr("disabled", "disabled");

    // / delete collection items
    $("#existingCandidateNationalities").on("click", "a", function() {
        $(this).parent("div").remove();

        if ($('#existingCandidateNationalities').children().length <= 1) {
            $('#candidateNationalitiesLabel').remove();
            $('#my-nationality-lb').html("My Nationality");
        }

        if ($('#existingCandidateNationalities').children().length == 1) {

            $('#my-hint').remove();
            $('#my-nationality-hint').show();
        }

    });

    $("#existingMaternalNationalities").on("click", "a", function() {
        $(this).parent("div").remove();

        if ($('#existingMaternalNationalities').children().length <= 1) {
            $('#maternalNationalitiesLabel').remove();
            $('#maternal-nationality-lb').html("Mother's Nationality");
        }
    });

    $("#existingPaternalNationalities").on("click", "a", function() {
        $(this).parent("div").remove();

        if ($('#existingPaternalNationalities').children().length <= 1) {
            $('#paternalNationalitiesLabel').remove();
            $('#paternal-nationality-lb').html("Father's Nationality");
        }
    });

    bindDatePicker('#dateOfBirth');
    bindDatePicker('#passportExpiryDate');
    bindDatePicker('#passportIssueDate');
    bindDatePicker('#dateOfExamination');
    addToolTips();
    watchUpload($('#languageQualificationDocument'), ajaxLanguageQualificationDocumentDelete);	
    
    // -------------------------------------------------------------------------------
	// Language Qualification Type Change
	// -------------------------------------------------------------------------------
	$('#qualificationType').live('change', function() {
		var selectedType = $('#qualificationType').val();
		if (selectedType === "IELTS_ACADEMIC") {
			$('#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect').show();
			$('#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree').hide();
		} else {
			$('#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree').show();
			$('#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect').hide();
		}
		
		if (selectedType === "OTHER") {
			enableOtherLanguageQualification();
		} else {
			disableOtherLanguageQualification();
		}
		
		$('#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree').val("");
		$('#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect').val("");
		$("#examTakenOnlineNo").removeAttr("checked");
		$("#examTakenOnlineYes").removeAttr("checked");
		
		$('#languageQualification_div div.alert').remove();
		
		$("#otherQualificationTypeName, #dateOfExamination").val("");
		
		ajaxLanguageQualificationDocumentDelete();
		deleteQualificationDocumentFile();
	});
	
});

function ajaxLanguageQualificationDocumentDelete() {
	if ($('#document_LANGUAGE_QUALIFICATION') && $('#document_LANGUAGE_QUALIFICATION').val() && $('#document_LANGUAGE_QUALIFICATION').val() != '') {
		$.ajax({
			type : 'POST',
			statusCode : {
				401 : function() { window.location.reload(); },
				500 : function() { window.location.href = "/pgadmissions/error"; },
				404 : function() { window.location.href = "/pgadmissions/404"; },
				400 : function() { window.location.href = "/pgadmissions/400"; },
				403 : function() { window.location.href = "/pgadmissions/404"; }
			},
			url : "/pgadmissions/update/deleteLanguageQualificationsDocument",
			data : { 
				applicationId: $('#applicationId').val(),
				documentId: $('#document_LANGUAGE_QUALIFICATION').val(),
				cacheBreaker: new Date().getTime()
			},
			success : function(data) {
			},
			complete : function() {
			}
		});
		$("#languageQualificationDocument").val("");
	}
	
}

function ajaxDeleteAllLanguageQualifications() {
    var postData = {
            applicationId: $('#applicationId').val(),
            cacheBreaker: new Date().getTime(),
    };
    
    if ($('input:radio[name=englishFirstLanguage]:checked').length > 0) {
        postData.englishFirstLanguage = $('input:radio[name=englishFirstLanguage]:checked').val();
    }
    
    if ($('input:radio[name=languageQualificationAvailable]:checked').length > 0) {
        postData.languageQualificationAvailable = $('input:radio[name=languageQualificationAvailable]:checked').val();
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
        url : "/pgadmissions/update/deleteAllLanguageQualifications",
        data : postData,
        success : function(data) {
            $('#languageQualification_div').html(data);
            bindDatePicker('#dateOfExamination');
            addToolTips();
        },
        complete : function() {
        }
    });
}

function selectValue(elementName) {
	var selectedType = $('#qualificationType').val();
	if (selectedType === "IELTS_ACADEMIC") {
		return $("select[name='" + elementName + "']")[0].value;
	} else {
		return $("input[name='" + elementName + "']")[0].value;
	}
}

function disablePassportInformation() {
	$("#passportNumber").attr("disabled", "disabled");
	$("#passportNumber").val("");
	$("#lbl_passportNumber").addClass("grey-label");
	$("#lbl_passportNumber").html("Passport Number");
	
	$("#nameOnPassport").attr("disabled", "disabled");
	$("#nameOnPassport").val("");
	$("#lbl_nameOnPassport").addClass("grey-label");
	$("#lbl_nameOnPassport").html("Name on Passport");
	
	$("#passportIssueDate").attr("disabled", "disabled");
	$("#passportIssueDate").val("");
	$("#lbl_passportIssueDate").addClass("grey-label");
	$("#lbl_passportIssueDate").html("Passport Issue Date");
	
	$("#passportExpiryDate").attr("disabled", "disabled");
	$("#passportExpiryDate").val("");
	$("#lbl_passportExpiryDate").addClass("grey-label");
	$("#lbl_passportExpiryDate").html("Passport Expiry Date");
}

function enablePassportInformation() {
	$("#passportNumber").removeAttr("disabled", "disabled");
	$("#passportNumber").removeAttr("readonly", "readonly");
	$("#lbl_passportNumber").removeClass("grey-label");
	$("#lbl_passportNumber").html("Passport Number<em>*</em>");
	
	$("#nameOnPassport").removeAttr("disabled", "disabled");
	$("#nameOnPassport").removeAttr("readonly", "readonly");
	$("#lbl_nameOnPassport").removeClass("grey-label");
	$("#lbl_nameOnPassport").html("Name on Passport<em>*</em>");
	
	$("#passportIssueDate").removeAttr("disabled", "disabled");
	$("#lbl_passportIssueDate").removeClass("grey-label");
	$("#lbl_passportIssueDate").html("Passport Issue Date<em>*</em>");
	
	$("#passportExpiryDate").removeAttr("disabled", "disabled");
	$("#lbl_passportExpiryDate").removeClass("grey-label");
	$("#lbl_passportExpiryDate").html("Passport Expiry Date<em>*</em>");	
}

function enableLanguageQualifications() {
	$("#qualificationType, #dateOfExamination, #examTakenOnlineYes, #examTakenOnlineNo, #languageQualificationDocument").removeAttr("disabled", "disabled");
	$("#qualificationType, #examTakenOnlineYes, #examTakenOnlineNo, #languageQualificationDocument").removeAttr("readonly", "readonly");
	
	$('#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect').removeAttr("disabled", "disabled");
	$('#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect').removeAttr("readonly", "readonly");
	$('#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree').removeAttr("disabled", "disabled");
	$('#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree').removeAttr("readonly", "readonly");
	
	$("#lbl-qualificationType, #lbl-dateOfExamination, #lbl-overallScore, #lbl-readingScore, #lbl-writingScore, #lbl-speakingScore, #lbl-listeningScore, #lbl-examTakenOnline, #lbl-languageQualificationDocument, #lbl-englishLanguageQualifications").removeClass("grey-label");
	
	if ($("#languageQualificationsTable tr").length <= 0) {
	    $("#addLanguageQualificationButton").show();	    
	}
	
	$("#updateLanguageQualificationButton").hide();
}

function disableLanguageQualifications() {
	$("#qualificationType, #dateOfExamination, #examTakenOnlineYes, #examTakenOnlineNo, #languageQualificationDocument").attr("disabled", "disabled");
	$("#qualificationType, #dateOfExamination, #examTakenOnlineYes, #examTakenOnlineNo, #languageQualificationDocument").attr("readonly", "readonly");
	
	$('#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect').attr("disabled", "disabled");
	$('#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect').attr("readonly", "readonly");
	
	$('#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree').attr("disabled", "disabled");
	$('#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree').attr("readonly", "readonly");
	
	$("#lbl-qualificationType, #lbl-dateOfExamination, #lbl-overallScore, #lbl-readingScore, #lbl-writingScore, #lbl-speakingScore, #lbl-listeningScore, #lbl-examTakenOnline, #lbl-languageQualificationDocument, #lbl-englishLanguageQualifications").addClass("grey-label");
	
	$("#addLanguageQualificationButton").hide();
	$("#updateLanguageQualificationButton").hide();
	
	clearLanguageQualification();
}

function clearLanguageQualification() {
	$("#qualificationType, #otherQualificationTypeName, #dateOfExamination").val("");
	$("#examTakenOnlineNo").removeAttr("checked");
	$("#examTakenOnlineYes").removeAttr("checked");
	
	$('#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree').val("");
	$('#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect').val("");
	
	$('#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree').show();
	$('#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect').hide();
	
	ajaxLanguageQualificationDocumentDelete();
	$("#languageQualificationDocument").val();
}

function disableOtherLanguageQualification() {
	$('#otherQualificationTypeName').attr("disabled", "disabled");
	$('#otherQualificationTypeName').attr("readonly", "readonly");
	$('#lbl-otherQualificationTypeName').addClass("grey-label");
}

function enableOtherLanguageQualification() {
	$('#otherQualificationTypeName').removeAttr("disabled");
	$('#otherQualificationTypeName').removeAttr("readonly");
	$('#lbl-otherQualificationTypeName').removeClass("grey-label");
}

function isEnglishFirstLanguage() {
	var selected_radio = $("input[name='englishFirstLanguage']:checked").val();
	if (selected_radio == 'true')   {
		$("#lbl-languageQualificationAvailable").addClass("grey-label");
		$("input[name='languageQualificationAvailable']").attr("disabled", "disabled");
		$("input[name='languageQualificationAvailable']").prop('checked', false);
		disableLanguageQualifications();
		disableOtherLanguageQualification();
		$("#languageQualificationsTable").empty();
		return true;
	} else if (selected_radio == 'false') {
	    $("#lbl-languageQualificationAvailable").removeClass("grey-label");
		$("input[name='languageQualificationAvailable']").removeAttr("disabled");
		return false;
	}
	return false;
}

function isRequireVisa() {
	var selected_radio = $("input[name='requiresVisa']:checked").val();
	if (selected_radio == 'true')   {
		$("#lbl-passportAvailable").removeClass("grey-label");
		$("input[name='passportAvailable']").removeAttr("disabled", "disabled");
	} else {
		$("#lbl-passportAvailable").addClass("grey-label");
		$("input[name='passportAvailable']").attr("disabled", "disabled");
		$("input[name='passportAvailable']").prop('checked', false);
		disablePassportInformation();
	}
}

function postPersonalDetailsData(message) {
    // candidate nationalities
    if ($('#candidateNationalityCountry option:selected').val() != '') {
        var html = "<span><input type='hidden' name='candidateNationalities' value='"
                + $('#candidateNationalityCountry option:selected').val() + "'/>" + '</span>';
        $('#existingCandidateNationalities').append(html);
    }

    var acceptedTheTerms;
    if ($("#acceptTermsPEDValue").val() == 'NO') {
        acceptedTheTerms = false;
    } else {
        acceptedTheTerms = true;
    }
    
    var examTakenOnline = "";
    if ($("input[name='examTakenOnline']:checked").length > 0) {
    	examTakenOnline = $("input[name='examTakenOnline']:checked").val();
    }
    
    var postData = {
        title : $("#title").val(),
        firstName : $("#firstName").val(),
        firstName2 : $("#firstName2").val(),
        firstName3 : $("#firstName3").val(),
        lastName : $("#lastName").val(),
        country : $("#country").val(),
        dateOfBirth : $("#dateOfBirth").val(),
        residenceCountry : $("#residenceCountry").val(),
        messenger : $("#pd_messenger").val(),
        phoneNumber : $('#pd_telephone').val(),
        application : $('#applicationId').val(),
        applicationId : $('#applicationId').val(),
        candidateNationalities : "",
        ethnicity : $("#ethnicity").val(),
        disability : $("#disability").val(),
        'passportInformation.passportNumber' : $("#passportNumber").val(),
        'passportInformation.nameOnPassport' : $("#nameOnPassport").val(),
        'passportInformation.passportIssueDate' : $("#passportIssueDate").val(),
        'passportInformation.passportExpiryDate' : $("#passportExpiryDate").val(),
    	'languageQualifications[0].languageQualificationId': $('#languageQualificationId').val(),
    	'languageQualifications[0].qualificationType': $('#qualificationType').val(),
    	'languageQualifications[0].otherQualificationTypeName' : $('#otherQualificationTypeName').val(),
    	'languageQualifications[0].dateOfExamination' : $('#dateOfExamination').val(),
    	'languageQualifications[0].overallScore' : selectValue('overallScore'),
    	'languageQualifications[0].readingScore' : selectValue('readingScore'),
    	'languageQualifications[0].writingScore' : selectValue('writingScore'),
    	'languageQualifications[0].speakingScore' : selectValue('speakingScore'),
    	'languageQualifications[0].listeningScore' : selectValue('listeningScore'),
    	'languageQualifications[0].examTakenOnline' : examTakenOnline,
    	'languageQualifications[0].languageQualificationDocument' : $('#document_LANGUAGE_QUALIFICATION').val(),
        message : message,
        acceptedTerms : acceptedTheTerms,
        cacheBreaker: new Date().getTime()
    };

    if ($('input:radio[name=englishFirstLanguage]:checked').length > 0) {
        postData.englishFirstLanguage = $('input:radio[name=englishFirstLanguage]:checked').val();
    }

    if ($('input:radio[name=requiresVisa]:checked').length > 0) {
        postData.requiresVisa = $('input:radio[name=requiresVisa]:checked').val();
    }
    
    if ($('input:radio[name=languageQualificationAvailable]:checked').length > 0) {
        postData.languageQualificationAvailable = $('input:radio[name=languageQualificationAvailable]:checked').val();
    }
    
    if ($('input:radio[name=passportAvailable]:checked').length > 0) {
        postData.passportAvailable = $('input:radio[name=passportAvailable]:checked').val();
    }
    
    var gender = $("input[name='genderRadio']:checked").val();
    if (gender) {
        postData.gender = gender;
    }
    
    // do the post!
    $('#personalDetailsSection > div').append('<div class="ajax" />');

    $.ajax({
        type : 'POST',
        statusCode : {
            401 : function() { window.location.reload(); },
            500 : function() { window.location.href = "/pgadmissions/error"; },
            404 : function() { window.location.href = "/pgadmissions/404"; },
            400 : function() { window.location.href = "/pgadmissions/400"; },
            403 : function() { window.location.href = "/pgadmissions/404"; }
        },
        url : "/pgadmissions/update/editPersonalDetails",
        data : $.param(postData) 
        		+ "&" + $('input[name="candidateNationalities"]').serialize(),
        		cacheBreaker: new Date().getTime(),
        success : function(data) {
            $('#personalDetailsSection').html(data);

            if (message == 'close') {
                // Close the section only if there are no errors.
                var errorCount = $('#personalDetailsSection .alert-error:visible').length;
                if (errorCount == 0) {
                    $('#personalDetails-H2').trigger('click');
                } else {
                    markSectionError('#personalDetailsSection');
                }
            }
        },
        complete : function() {
            $('#personalDetailsSection div.ajax').remove();
        }
    });
}

function deleteQualificationDocumentFile(){
	
	var $container  = $('#languageQualificationDocument').parent('div.field');
	var $hidden  = $container.find('input.file');

	$container.find('span a').each(function()
	{
		$(this).remove();
	});

	$hidden.val(''); // clear field value.
	$container.removeClass('uploaded');

}