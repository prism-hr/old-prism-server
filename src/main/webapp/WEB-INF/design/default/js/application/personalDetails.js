$(document).ready(
        function() {

            var persImgCount = 0;
            showOrHideNationalityButton();
            
            if ($("input[name='requiresVisa']:checked").val() == "true") {
            	enablePassportInformation();
            } else {
            	disablePassportInformation();
            }
            
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

                    persImgCount = 0;

                }
            });
            
            // -------------------------------------------------------------------------------
            // Require visa
            // -------------------------------------------------------------------------------
            $("input[name='requiresVisa']").bind('change', function() {
            	var selected_radio = $("input[name='requiresVisa']:checked").val();
            	if (selected_radio == 'true')   {
            		enablePassportInformation();
            	} else {
            		disablePassportInformation();
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
            addToolTips();

        });

function disablePassportInformation() {
	$("#passportNumber").attr("disabled", "disabled");
	$("#passportNumber").val("");
	$("#lbl_passportNumber").addClass("grey-label");
	$("#nameOnPassport").attr("disabled", "disabled");
	$("#nameOnPassport").val("");
	$("#lbl_nameOnPassport").addClass("grey-label");
	$("#passportIssueDate").attr("disabled", "disabled");
	$("#passportIssueDate").val("");
	$("#lbl_passportIssueDate").addClass("grey-label");
	$("#passportExpiryDate").attr("disabled", "disabled");
	$("#passportExpiryDate").val("");
	$("#lbl_passportExpiryDate").addClass("grey-label");
}

function enablePassportInformation() {
	$("#passportNumber").removeAttr("disabled", "disabled");
	$("#passportNumber").removeAttr("readonly", "readonly");
	$("#lbl_passportNumber").removeClass("grey-label");
	$("#nameOnPassport").removeAttr("disabled", "disabled");
	$("#nameOnPassport").removeAttr("readonly", "readonly");
	$("#lbl_nameOnPassport").removeClass("grey-label");
	$("#passportIssueDate").removeAttr("disabled", "disabled");
	$("#passportIssueDate").removeAttr("readonly", "readonly");
	$("#lbl_passportIssueDate").removeClass("grey-label");
	$("#passportExpiryDate").removeAttr("disabled", "disabled");
	$("#passportExpiryDate").removeAttr("readonly", "readonly");
	$("#lbl_passportExpiryDate").removeClass("grey-label");	
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
    // general post data
    var postData = {
        title : $("#title").val(),
        firstName : $("#firstName").val(),
        lastName : $("#lastName").val(),
        email : $("#email").val(),
        country : $("#country").val(),
        dateOfBirth : $("#dateOfBirth").val(),
        residenceCountry : $("#residenceCountry").val(),
        messenger : $("#pd_messenger").val(),
        phoneNumber : $('#pd_telephone').val(),
        application : $('#applicationId').val(),
        applicationId : $('#applicationId').val(),
        candidateNationalities : "",
        maternalGuardianNationalities : "",
        paternalGuardianNationalities : "",
        ethnicity : $("#ethnicity").val(),
        disability : $("#disability").val(),
        passportNumber : $("#passportNumber").val(),
        nameOnPassport : $("#nameOnPassport").val(),
        passportIssueDate : $("#passportIssueDate").val(),
        passportExpiryDate : $("#passportExpiryDate").val(),
        message : message,
        acceptedTerms : acceptedTheTerms
    };

    if ($('input:radio[name=englishFirstLanguage]:checked').length > 0) {
        postData.englishFirstLanguage = $('input:radio[name=englishFirstLanguage]:checked').val();
    }

    if ($('input:radio[name=requiresVisa]:checked').length > 0) {
        postData.requiresVisa = $('input:radio[name=requiresVisa]:checked').val();
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
            401 : function() {
                window.location.reload();
            },
            500 : function() {
                window.location.href = "/pgadmissions/error";
            },
            404 : function() {
                window.location.href = "/pgadmissions/404";
            },
            400 : function() {
                window.location.href = "/pgadmissions/400";
            },
            403 : function() {
                window.location.href = "/pgadmissions/404";
            }
        },
        url : "/pgadmissions/update/editPersonalDetails",
        data : $.param(postData) + "&" + $('input[name="candidateNationalities"]').serialize() + "&"
                + $('input[name="maternalGuardianNationalities"]').serialize() + "&"
                + $('input[name="paternalGuardianNationalities"]').serialize(),

        success : function(data) {
            $('#personalDetailsSection').html(data);

            if (message == 'close') {
                // Close the section only if there are no errors.
                var errorCount = $('#personalDetailsSection .invalid:visible').length;
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
