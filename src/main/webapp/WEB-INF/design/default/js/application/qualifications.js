$(document).ready(function() {
    $('#qualificationsSection .selectpicker').selectpicker();
    $("#acceptTermsQDValue").val("NO");
    showOrHideAddQualificationButtonOnly();
    showOrHideQualificationInstitution();
    
    if ($('#qualificationInstitution').val() === "OTHER") {
        $("#lbl-otherInstitutionProviderName").removeClass("grey-label").parent().find('.hint').removeClass("grey");
        $("#otherInstitutionProviderName").removeAttr("readonly", "readonly");
        $("#otherInstitutionProviderName").removeAttr("disabled", "disabled");
    }

    // -------------------------------------------------------------------------------
    // Show or hide the qualificationInstitution text field.
    // -------------------------------------------------------------------------------
    function showOrHideQualificationInstitution() {
        $("#qualificationInstitution").prop("disabled", "disabled");
        $("#lbl-providerName").addClass("grey-label").parent().find('.hint').addClass("grey");
        if ($("#institutionCountry").val() != "") {
            $("#qualificationInstitution").removeAttr("disabled", "disabled");
            $("#lbl-providerName").removeClass("grey-label").parent().find('.hint').removeClass("grey");
        }
    }

    $('#qualificationInstitution').change(function() {
        if ($('#qualificationInstitution').val() === "OTHER") {
            $("#lbl-otherInstitutionProviderName").removeClass("grey-label").parent().find('.hint').removeClass("grey");
            $("#otherInstitutionProviderName").removeAttr("readonly", "readonly");
            $("#otherInstitutionProviderName").removeAttr("disabled", "disabled");
        } else {
            $("#otherInstitutionProviderName").val("");
            $("#lbl-otherInstitutionProviderName").addClass("grey-label").parent().find('.hint').addClass("grey");
            $("#otherInstitutionProviderName").prop("readonly", "readonly");
            $("#otherInstitutionProviderName").prop("disabled", "disabled");
        }
    });

    $('#institutionCountry').click(function() {
        showOrHideQualificationInstitution();
    });

    $('#institutionCountry').change(function() {
        showOrHideQualificationInstitution();
		$("#otherInstitutionProviderName").val("");
		$("#lbl-otherInstitutionProviderName").addClass("grey-label").parent().find('.hint').addClass("grey");
		$("#otherInstitutionProviderName").prop("readonly", "readonly");
		$("#otherInstitutionProviderName").prop("disabled", "disabled");
        if ($('#institutionCountry').val() == "") {
            $("#qualificationInstitution").empty();
            $("#otherInstitutionProviderName").val("");
            $("#lbl-otherInstitutionProviderName").addClass("grey-label").parent().find('.hint').addClass("grey");
            $("#otherInstitutionProviderName").prop("readonly", "readonly");
            $("#otherInstitutionProviderName").prop("disabled", "disabled");
            return;
        }

        $.ajax({
            type : 'GET',
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
            url : "/pgadmissions/update/getInstitutionInformation",
            data : {
                country_id : $("#institutionCountry").val(),
                cacheBreaker : new Date().getTime()
            },
            success : function(data) {
                institutions = data;
                var options = $("#qualificationInstitution");
                $("#qualificationInstitution").empty();

                options.append($("<option />").val("").text("Select..."));
                for ( var i = 0; i < institutions.length; i++) {
                    options.append($("<option />").val(institutions[i]["code"]).text(institutions[i]["name"]));
                }
                options.append($("<option />").val("OTHER").text("Other"));
            },
            complete : function() {
                $('#qualificationInstitution').selectpicker('refresh');
            }
        });
    });

    // -------------------------------------------------------------------------------
    // Close button.
    // -------------------------------------------------------------------------------
    $('#qualificationsCloseButton').click(function() {
        $('#qualifications-H2').trigger('click');
        return false;
    });

    // -------------------------------------------------------------------------------
    // Show or hide the AddPositionButton.
    // -------------------------------------------------------------------------------
    function showOrHideAddQualificationButton() {
        numberOfSavedPositions = $("#qualificationsSection .existing .button-edit").length;
        if (numberOfSavedPositions >= 6) {
            $("#qualificationsSaveButton").addClass("clear");
            $('#qualifications-H2').trigger('click');
        } else {
            // enable save
            $("#addQualificationButton").show();
            $("#qualificationsSaveButton").removeClass("clear");
        }
    }

    function showOrHideAddQualificationButtonOnly() {
        numberOfSavedPositions = $("#qualificationsSection .existing .button-edit").length;
        if (numberOfSavedPositions >= 6 && $("#qualificationSubject").val() == "") {
            $("#qualificationsSaveButton").addClass("clear");
            $("#addQualificationButton").attr('disabled', 'true');
        } else {
            $("#addQualificationButton").show();
            $("#addQualificationButton").removeAttr('disabled');
            $("#qualificationsSaveButton").removeClass("clear");
        }
    }

    // -------------------------------------------------------------------------------
    // Checkbox to mark the qualification as current.
    // -------------------------------------------------------------------------------
    $("input[name*='currentQualificationCB']").click(function() {
        $('#qualification-awarddate-error').remove();
        if ($("#currentQualification").val() == 'YES') {
            // Uncheck the box
            $("#currentQualification").val("NO");
            $("#quali-grad-id em").remove();
            $("#quali-grad-id").text("Expected Grade / Result / GPA").append('<em>*</em>');
            $("#quali-award-date-lb").text("Expected Award Date").append('<em>*</em>');
            $("#quali-proof-of-award-lb").text("Interim Transcript (PDF)");
        } else {
            // Check the box
            $("#currentQualification").val("YES");
            $("#quali-grad-id em").remove();
            $("#quali-grad-id").text("Grade / Result / GPA").append('<em>*</em>');
            $("#quali-award-date-lb em").remove();
            $("#quali-award-date-lb").text("Award Date").append('<em>*</em>');
            $("#quali-proof-of-award-lb").text("Proof of award (PDF)");
        }
    });

    // -------------------------------------------------------------------------------
    // Delete a qualification.
    // -------------------------------------------------------------------------------
    $('a[name="deleteQualificationButton"]').click(function() {
        var id = $(this).attr("id").replace("qualification_", "");
        $('#ajaxloader').show();
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
            url : "/pgadmissions/deleteentity/qualification",
            data : {
                id : id
            },
            success : function(data) {
                $('#qualificationsSection').html(data);
				$('#qualifications-H2').trigger('click');
            },
            complete : function() {
                $('#ajaxloader').fadeOut('fast');
                showOrHideAddQualificationButton();
            }
        });
    });

    // -------------------------------------------------------------------------------
    // "Accept terms" checkbox.
    // -------------------------------------------------------------------------------
    $("input[name*='acceptTermsQDCB']").click(function() {
        if ($("#acceptTermsQDValue").val() == 'YES') {
            $("#acceptTermsQDValue").val("NO");
        } else {
            $("#acceptTermsQDValue").val("YES");
        }
    });

    // -------------------------------------------------------------------------------
    // Add a qualification.
    // -------------------------------------------------------------------------------
    $('#addQualificationButton').click(function() {
        $("span[name='nonAcceptedQD']").html('');
        postQualificationData('add');
    });

    // -------------------------------------------------------------------------------
    // Save qualification.
    // -------------------------------------------------------------------------------
    $('#qualificationsSaveButton').click(function() {
        if (numberOfSavedPositions >= 6 && $("#qualificationSubject").val() == "") {
            $('#qualifications-H2').trigger('click');
            return;
        }

        $("span[name='nonAcceptedQD']").html('');

        // Check for a "dirty" qualification form. If there is data try to
        // submit it.
        if (!isFormEmpty('#qualificationsSection form')) {
            postQualificationData('close');
        } else {
            unmarkSection('#qualificationsSection');
            $('#qualificationsCloseButton').trigger('click');
        }
    });

    // -------------------------------------------------------------------------------
    // Edit a qualification.
    // -------------------------------------------------------------------------------
    $('a[name="editQualificationLink"]').click(function() {
        $('#editClicked').val("1");
        var id = this.id;
        id = id.replace('qualification_', '');
        $('#ajaxloader').show();
        $.ajax({
            type : 'GET',
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
            url : "/pgadmissions/update/getQualification",
            data : {
                applicationId : $('#applicationId').val(),
                qualificationId : id,
                message : 'edit',
                cacheBreaker : new Date().getTime()
            },
            success : function(data) {
                $('#qualificationsSection').html(data);
                if ($("#currentQualificationCB").is(":checked")) {
                    $("#quali-grad-id em").remove();
                    $("#quali-grad-id").text("Grade / Result / GPA").append('<em>*</em>');
                    $("#quali-award-date-lb").text("Award Date").append('<em>*</em>');
                    $("#quali-proof-of-award-lb").text("Proof of award (PDF)");
                } else {
                    $("#quali-grad-id em").remove();
                    $("#quali-grad-id").text("Expected Grade / Result / GPA").append('<em>*</em>');
                    $("#quali-award-date-lb").text("Expected Award Date").append('<em>*</em>');
                    $("#quali-proof-of-award-lb").text("Interim Transcript (PDF)");
                }
                
                if ($('#qualificationInstitution').val() === "OTHER") {
                    $("#lbl-otherInstitutionProviderName").removeClass("grey-label").parent().find('.hint').removeClass("grey");
                    $("#otherInstitutionProviderName").removeAttr("readonly", "readonly");
                    $("#otherInstitutionProviderName").removeAttr("disabled", "disabled");
					
                }

                // Cheap way of changing the button text.
                $('#addQualificationButton').html('Update');
                $("#addQualificationButton").show();
            },
            complete : function() {
                $('#ajaxloader').fadeOut('fast');
				$('#qualifications-H2').trigger('click');
                // showOrHideAddQualificationButton();
            }
        });
    });

    // -------------------------------------------------------------------------------
    // Clear button.
    // -------------------------------------------------------------------------------
    $('#qualificationClearButton').click(function() {
        $('#ajaxloader').show();
        loadQualificationsSection(true);
    });

    bindDatePicker('#qualificationStartDate');
    bindDatePicker('#qualificationAwardDate');
    addToolTips();

    // Generic file upload solution...
    watchUpload($('#proofOfAward'));
});

function postQualificationData(message) {
    $('#ajaxloader').show();
    var acceptedTheTerms;
    if ($("#acceptTermsQDValue").val() == 'NO') {
        acceptedTheTerms = false;
    } else {
        acceptedTheTerms = true;
    }

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
        url : "/pgadmissions/update/editQualification",
        data : {
            qualificationSubject : $("#qualificationSubject").val(),
            qualificationTitle : $("#qualificationTitle").val(),
            qualificationInstitution : $("#qualificationInstitution option:selected").text(),
            otherQualificationInstitution : $("#otherInstitutionProviderName").val(),
            qualificationInstitutionCode : $("#qualificationInstitution").val(),
            qualificationType : $("#qqualificationType").val(),
            qualificationGrade : $("#qualificationGrade").val(),
            qualificationScore : $("#qualificationScore").val(),
            qualificationStartDate : $("#qualificationStartDate").val(),
            qualificationLanguage : $("#qualificationLanguage").val(),
            qualificationAwardDate : $("#qualificationAwardDate").val(),
            completed : $("#currentQualification").val(),
            qualificationId : $("#qualificationId").val(),
            applicationId : $('#applicationId').val(),
            application : $('#applicationId').val(),
            institutionCountry : $('#institutionCountry').val(),
            proofOfAward : $('#document_PROOF_OF_AWARD').val(),
            message : message,
            acceptedTerms : acceptedTheTerms
        },
        success : function(data) {
            $('#qualificationsSection').html(data);
            var errorCount = $('#qualificationsSection .alert-error').length;
			
            if (errorCount > 0) {
                markSectionError('#qualificationsSection');
				$('#qualifications-H2').trigger('click');
            } else {
				if (message == 'add') {
					$('#qualifications-H2').trigger('click');
				}
			}
        },
        complete : function() {
            $('#ajaxloader').fadeOut('fast');
        }
    });
}

function ajaxProofOfAwardDelete() {
    if ($('#document_PROOF_OF_AWARD') && $('#document_PROOF_OF_AWARD').val() && $('#document_PROOF_OF_AWARD').val() != '') {
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
            url : "/pgadmissions/delete/asyncdelete",
            data : {
                documentId : $('#document_PROOF_OF_AWARD').val()
            }
        });

    }
}

function ajaxProofOfAwardUpload() {
    // Showing/hiding progress bar when we're uploading the file via AJAX.
    $("#progress").ajaxStart(function() {
        $(this).show();
    }).ajaxComplete(function() {
        $(this).hide();
        $('#progress').html("");
    });

    // Remove any previous error messages.
    $('#qualUploadedDocument').find('div.alert-error').remove();

    // Functionality for uploading files via AJAX (immediately on selection).
    $.ajaxFileUpload({
        url : '/pgadmissions/documents/async',
        secureuri : false,
        fileElementId : 'proofOfAward',
        dataType : 'text',
        data : {
            type : 'PROOF_OF_AWARD'
        },
        success : function(data) {
            if ($(data).find('div.alert-error').length == 0) {
                // i.e. if there are no uploading errors, which would be
                // indicated by the presence of a div.alert-error tag.
                $('#qualUploadedDocument').html(data);
                $('#qualUploadedDocument').show();
                $('#uploadFields').addClass('uploaded');
                $('span[name="supportingDocumentSpan"] a.button-edit').attr({
                    'id' : 'editQualiPOA',
                    'data-desc' : 'Edit Proof Of Award'
                });
            } else {
                $('#qualUploadedDocument').append(data);
            }
        }
    });

}