$(document).ready(function() {
    bindDatePicker($("#programAdvertClosingDateInput"));
    bindAddClosingDateButtonAction();
    bindSaveButtonAction();
    bindProgramSelectChangeAction();
    bindClosingDatesActions();
    bindCancelNewProgramAction();
    bindChangeInstitutionCountryAction();
    initEditors();
    $('.selectpicker').selectpicker();
    checkToDisable();
    otherInstitutionCheck();
    lockFormFunction(null);
});

function bindChangeInstitutionCountryAction() {
    $('#programAdvertInstitutionCountry').change(function() {
        getInstitutionData();
    });
}

function bindCancelNewProgramAction() {
    $("#programAdvertCancelNewProgramBtn").bind('click', function() {
        $("#programAdvertSelectProgramDiv").show();
        $("#programAdvertNewProgramNameDiv").hide();
        $("#programAdvertProgramSelect").selectpicker('val', '');
    });
}

function bindProgramSelectChangeAction() {
    $("#programAdvertProgramSelect").bind('change', function() {
        clearProgramAdvertErrors();
        checkToDisable();
        changeHeaderInfoBars();
        var programme_code = $("#programAdvertProgramSelect").val();
        if (programme_code == "") {
            clearProgramSection();
        } else {
            getAdvertData(programme_code);
            getClosingDatesData(programme_code);
        }
    });
}

$(document).on('click', '#newProgamme', function() {
    $("#programAdvertSelectProgramDiv").hide();
    $("#programAdvertNewProgramNameDiv").show();
    clearProgramAdvertErrors();
    lockFormFunction(null);
    checkToDisable();
    changeHeaderInfoBars();
    clearProgramSection();
});

function getInstitutionData(successCallback) {
    $("#programAdvertInstitution").selectpicker("val", "");
    $("#programAdvertInstitutionOtherName").val("");
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
        url : "/pgadmissions/update/getUserInstitutionInformation",
        data : {
            country_id : $("#programAdvertInstitutionCountry").val(),
            cacheBreaker : new Date().getTime()
        },
        success : function(data) {
            userInstitutions = data["userInstitutions"];
            otherInstitutions = data["otherInstitutions"];

            $("#userInstitutions").empty();
            $("#otherInstitutions").empty();

            for ( var i = 0; i < userInstitutions.length; i++) {
                $("#userInstitutions").append($("<option />").val(userInstitutions[i]["code"]).text(userInstitutions[i]["name"]));
            }
            for ( var i = 0; i < otherInstitutions.length; i++) {
                $("#otherInstitutions").append($("<option />").val(otherInstitutions[i]["code"]).text(otherInstitutions[i]["name"]));
            }

            if (successCallback) {
                successCallback();
            }

            $('#programAdvertInstitution').selectpicker('refresh');
        },
        complete : function() {
        }
    });
}

function otherInstitutionCheck() {
    if ($('#programAdvertInstitution').val() == 'OTHER') {
        $("#programAdvertInstitutionOtherNameDiv label").removeClass("grey-label").parent().find('.hint').removeClass("grey");
        $("#programAdvertInstitutionOtherNameDiv input").prop("readonly", false).prop("disabled", false);
    } else {
        $("#programAdvertInstitutionOtherNameDiv label").addClass("grey-label").parent().find('.hint').addClass("grey");
        $("#programAdvertInstitutionOtherNameDiv input").prop("readonly", true).prop("disabled", true).val('');
    }
};

$(document).on('change', '#programAdvertInstitution', function() {
    otherInstitutionCheck();
});

function checkToDisable() {
    var selectedProgram = $("#programAdvertProgramSelect").val();
    if (selectedProgram != "" || $("#programAdvertNewProgramNameDiv").is(":visible")) {
        // new or existing program
        $("#advertGroup label").removeClass("grey-label").parent().find('.hint').removeClass("grey");
        $("#advertGroup input, #advertGroup textarea, #advertGroup select").removeAttr("readonly", "readonly");
        $("#advertGroup input, #advertGroup textarea, #advertGroup select, #advertGroup button, input[name=programAdvertAtasRequired]").removeAttr("disabled", "disabled");
    } else {
        $("#advertGroup label").addClass("grey-label").parent().find('.hint').addClass("grey");
        $("#advertGroup input, #advertGroup textarea, #advertGroup select").attr("readonly", "readonly");
        $("#advertGroup input, #advertGroup textarea, #advertGroup select, #advertGroup button, input[name=programAdvertAtasRequired]").attr("disabled", "disabled");
    }

    if (selectedProgram != "" && $("#programAdvertSelectProgramDiv").is(":visible")) {
        // existing program
        $("#programAdvertClosingDateGroup label").removeClass("grey-label").parent().find('.hint').removeClass("grey");
        $("#programAdvertClosingDateGroup input").removeAttr("readonly", "readonly");
        $("#programAdvertClosingDateGroup input, #programAdvertClosingDateGroup a").removeAttr("disabled", "disabled");
    } else {
        $("#programAdvertClosingDateGroup label").addClass("grey-label").parent().find('.hint').addClass("grey");
        $("#programAdvertClosingDateGroup input").attr("readonly", "readonly");
        $("#programAdvertClosingDateGroup input, #programAdvertClosingDateGroup a").attr("disabled", "disabled");
    }

}

function changeHeaderInfoBars(programval) {
    var programmeCode = $("#programAdvertProgramSelect").val();
    if (programmeCode == "" || $("#programAdvertNewProgramNameDiv").is(":visible")) {
        infohtml = "<i class='icon-info-sign'></i> Manage the advert for your programme here.";
        infodate = "<i class='icon-info-sign'></i> Manage closing dates for your programme here.";
        inforesource = "<i class='icon-info-sign'></i> Embed these resources to provide applicants with links to apply for your programme.";
    } else {
        var programmeName = $("#programAdvertProgramSelect option:selected").text();
        infohtml = "<i class='icon-info-sign'></i> Manage the advert for: <b>" + programmeName + "</b>.";
        infodate = "<i class='icon-info-sign'></i> Manage closing dates for: <b>" + programmeName + "</b>.";
        inforesource = "<i class='icon-info-sign'></i> Embed these resources to provide applicants with links to apply for: <b>" + programmeName + "</b>.";
    }
    $('#infodates').html(infodate);
    $('#infoResources').html(inforesource);
    if ($('#infoBarProgram').hasClass('alert-success')) {
        $('#infoBarProgram').addClass('alert-info').removeClass('alert-success').html(infohtml);
    } else {
        $('#infoBarProgram').html(infohtml);
    }
}

function getClosingDatesData(program_code) {
    clearClosingDate();
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
        url : "/pgadmissions/prospectus/programme/getClosingDates",
        data : {
            programCode : program_code,
        },
        success : function(data) {
            var map = JSON.parse(data);
            refreshClosingDates(map['closingDates']);
            checkIfErrors();
        },
        complete : function() {
            $('#ajaxloader').fadeOut('fast');
        }
    });
}

function refreshClosingDates(closingDates) {
    $('#programAdvertClosingDates tr').remove();
    jQuery.each(closingDates, function(index, closingDate) {
        appendClosingDateRow(closingDate);
    });
    sortClosingDates();
    checkDates();
}

function checkDates() {
    if ($('#programAdvertClosingDates td').length == 0) {
        $('#programAdvertClosingDates').hide();
    } else {
        $('#programAdvertClosingDates').show();
    }
}

function bindAddClosingDateButtonAction() {
    $("#addProgramAdvertClosingDate").bind('click', function() {
        clearProgramAdvertClosingDateErrors();
        $('#ajaxloader').show();
        var btnAction = $("#addProgramAdvertClosingDate").text();
        var update = btnAction.indexOf("Edit") !== -1;
        var url = "/pgadmissions/prospectus/programme/addClosingDate";
        if (update) {
            url = "/pgadmissions/prospectus/programme/updateClosingDate";
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
            url : url,
            data : {
                programCode : $("#programAdvertProgramSelect").val(),
                id : $('#programAdvertClosingDateId').val(),
                closingDate : $('#programAdvertClosingDateInput').val(),
                studyPlaces : $('#programAdvertStudyPlacesInput').val()
            },
            success : function(data) {
                var map = JSON.parse(data);
                if (!map['programClosingDate']) {
                    if (map['program']) {
                        $("#programAdvertSelectProgramDiv").append(getErrorMessageHTML(map['program']));
                    }
                    if (map['closingDate']) {
                        $("#programAdvertClosingDateDiv").append(getErrorMessageHTML(map['closingDate']));
                    }
                    if (map['studyPlaces']) {
                        $("#programAdvertStudyPlacesDiv").append(getErrorMessageHTML(map['studyPlaces']));
                    }
                } else {
                    var closingDate = map['programClosingDate'];
                    if (update) {
                        $('#cdr-' + closingDate.id).html(closingDateTd(closingDate));
                    } else {
                        appendClosingDateRow(closingDate);
                    }
                    clearClosingDate();
                    sortClosingDates();
                    checkDates();
                }
                checkIfErrors();
            },
            complete : function() {
                $('#ajaxloader').fadeOut('fast');
            }
        });
    });
}

function appendClosingDateRow(closingDate) {
    $('#programAdvertClosingDates tbody').append('<tr>' + '<td id="cdr-' + closingDate.id + '">' + closingDateTd(closingDate) + '</td>' + '<td>' + '<button class="button-edit" type="button" data-desc="Edit">Edit</button>' + '</td>' + '<td>' + '<button class="button-delete" type="button" data-desc="Remove">Remove</button>' + '</td>' + '</tr>');
}

function closingDateTd(closingDate) {
    var date = new Date(closingDate.closingDate);
    return $.datepicker.formatDate('d M yy', date);
    var studyPlaces = "";
    if (closingDate.studyPlaces > 0) {
        studyPlaces = " (" + closingDate.studyPlaces + " Places)";
    }
    return date + studyPlaces + '<input id="cdr-id" type="hidden" value="' + closingDate.id + '"/>' + '<input id="cdr-closingDate" type="hidden" value="' + date + '"/>' + '<input id="cdr-studyPlaces" type="hidden" value="' + closingDate.studyPlaces + '"/>';
}

function sortClosingDates() {
    var $table = $('#programAdvertClosingDates');
    var $rows = $('tbody > tr', $table);
    $rows.sort(function(a, b) {
        var keyA = new Date($('#cdr-closingDate', a).val());
        var keyB = new Date($('#cdr-closingDate', b).val());
        return (keyA > keyB) ? 1 : (keyA == keyB) ? 0 : -1;
    });
    $.each($rows, function(index, row) {
        $table.append(row);
    });
}

function bindClosingDatesActions() {
    $('#programAdvertClosingDates').on('click', '.button-edit', function() {
        var $row = $(this).closest('tr');
        editDate($row);
    });
    $('#programAdvertClosingDates').on('click', '.button-delete', function() {
        var $row = $(this).closest('tr');
        removeClosingDate($row, $row.find("#cdr-id").val());
    });
}

function editDate(row) {
    clearClosingDate();
    $('#programAdvertClosingDateId').val(row.find("#cdr-id").val());
    $('#programAdvertClosingDateInput').val(row.find("#cdr-closingDate").val());
    var placesValue = row.find("#cdr-studyPlaces").val();
    if (placesValue != "undefined") {
        $('#programAdvertStudyPlacesInput').val(placesValue);
    }
    $('#addProgramAdvertClosingDate').text("Edit");
    $('#programAdvertClosingDateHeading').text("Edit Closing Date");

}

function removeClosingDate(row, id) {
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
        url : "/pgadmissions/prospectus/programme/removeClosingDate",
        data : {
            programCode : $("#programAdvertProgramSelect").val(),
            closingDateId : id
        },
        success : function(data) {
            var map = JSON.parse(data);
            if (map['removedDate']) {
                row.remove();
                checkDates();
            }
        },
        complete : function() {
            $('#ajaxloader').fadeOut('fast');
        }
    });
}

function getAdvertData(programme_code) {
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
        url : "/pgadmissions/prospectus/programme/getAdvertData",
        data : {
            programCode : programme_code,
        },
        success : function(data) {
            var map = JSON.parse(data);
            updateAdvertSection(map);
            updateProgramSection(map);
            lockFormFunction(map);
        },
        complete : function() {
            $('#ajaxloader').fadeOut('fast');
        }
    });
}
function lockFormFunction(map) {
    var lock;
    if (map == null) {
        lock = false;
    } else if (map == true){
        lock = true;
    } else {
        lock = map['programLocked'];
    }
    if (lock) {
         $('#programAdvertInstitutionCountry, #programAdvertInstitution').prop('disabled',true);
         $('#programAdvertInstitutionCountry, #programAdvertInstitution').selectpicker('refresh');
         $('#programAdvertSave, #programAdvertAdvertisingDeadlineYear, #programAdvertStudyOptionsSelect, #programAdvertIsActiveRadioYes, #programAdvertIsActiveRadioNo, #programAdvertStudyDurationInput, #programAdvertStudyDurationUnitSelect, #programAdvertAtasRequired_true, #programAdvertAtasRequired_false').prop('disabled', true);
         $('#programAdvertAtasRequiredDiv label, #programAdvertInstitutionCountryDiv label, #programAdvertInstitutionDiv label, #programAdvertDescriptionDiv label, #programAdvertStudyDurationDiv label, #programAdvertFundingDiv label, #programAdvertIsActiveDiv label, #programAdvertStudyOptionsDiv label, #programAdvertAdvertisingDeadlineYearDiv label').addClass("grey-label").parent().find('.hint').addClass("grey");
         $('#infoBarProgram').removeClass('alert-info').addClass('alert-warning').html('<i class="icon-warning-sign"></i> Your program is been moderate by our staff ');
    } else {
         $('#programAdvertInstitutionCountry, #programAdvertInstitution').prop('disabled',false);
         $('#programAdvertInstitutionCountry, #programAdvertInstitution').selectpicker('refresh');
         $('#programAdvertSave, #programAdvertAdvertisingDeadlineYear, #programAdvertStudyOptionsSelect, #programAdvertIsActiveRadioYes, #programAdvertIsActiveRadioNo, #programAdvertStudyDurationInput, #programAdvertStudyDurationUnitSelect, #programAdvertAtasRequired_true, #programAdvertAtasRequired_false').prop('disabled', false);
         $('#programAdvertAtasRequiredDiv label, #programAdvertInstitutionCountryDiv label, #programAdvertInstitutionDiv label, #programAdvertDescriptionDiv label, #programAdvertStudyDurationDiv label, #programAdvertFundingDiv label, #programAdvertIsActiveDiv label, #programAdvertStudyOptionsDiv label, #programAdvertAdvertisingDeadlineYearDiv label').removeClass("grey-label").parent().find('.hint').removeClass("grey");
    }
}

function updateAdvertSection(map) {
    var linkToApply = map['linkToApply'];
    var titleSeleted = $("#programAdvertProgramSelect option:selected").text();
    var sharethisvar = 'http://api.addthis.com/oexchange/0.8/offer?url=' + linkToApply + '&title=' + titleSeleted;

    $("#programAdvertButtonToApply").val(map['buttonToApply']);
    $("#modalButtonToApply").val(map['buttonToApply']);

    $("#programAdvertLinkToApply").val(linkToApply);
    $("#modalLinkToApply").val(linkToApply);

    $('#sharethis').prop("href", sharethisvar);
}

function updateProgramSection(map) {
    $("[name=programAdvertAtasRequired][value=" + map["atasRequired"] + "]").prop("checked", true);

    $("#programAdvertInstitutionCountry").val(map["institutionCountryCode"]);
    $("#programAdvertInstitutionCountry").selectpicker('refresh');
    getInstitutionData(function(){
        $("#programAdvertInstitution").val(map["institutionCode"]);
    });
    $("#programAdvertInstitutionOtherName").val("");

    var advert = map['advert'];
    if (advert) {
        tinyMCE.get('programAdvertDescriptionText').setContent(advert['description']);
        tinyMCE.get('programAdvertFundingText').setContent(advert['funding'] ? advert['funding'] : "");
        $("#programAdvertId").val(advert.id);
        var durationOfStudyInMonths = advert['studyDuration'];
        if (durationOfStudyInMonths % 12 == 0) {
            $("#programAdvertStudyDurationInput").val((durationOfStudyInMonths / 12).toString());
            $("#programAdvertStudyDurationUnitSelect").val('YEARS');
        } else {
            $("#programAdvertStudyDurationInput").val(durationOfStudyInMonths.toString());
            $("#programAdvertStudyDurationUnitSelect").val('MONTHS');
        }
        if (advert['active']) {
            $("#programAdvertIsActiveRadioYes").prop("checked", true);
        } else {
            $("#programAdvertIsActiveRadioNo").prop("checked", true);
        }
    } else {
        clearAdvert();
    }

    $("#programAdvertStudyOptionsSelect").find('option:selected').removeAttr("selected").end();
    if (map['studyOptions']) {
        var studyOptions = map['studyOptions'];
        studyOptions.forEach(function(option) {
            $("#programAdvertStudyOptionsSelect option[value='" + option + "']").prop("selected", true);
        });
    }

    var advertisingDeadline = map['advertisingDeadline'];
    $('#programAdvertAdvertisingDeadlineYear').val(advertisingDeadline);

    if (map['isCustomProgram']) {
        $('#programAdvertAdvertisingDeadlineYear, #programAdvertStudyOptionsSelect').removeAttr("readonly", "readonly").removeAttr("disabled", "disabled");
    } else {
        $('#programAdvertAdvertisingDeadlineYear, #programAdvertStudyOptionsSelect').prop("readonly", true).attr("disabled", "disabled");
    }
    $('.selectpicker').selectpicker('refresh');
}

function bindSaveButtonAction() {
    $("#programAdvertSave").bind('click', function() {
        saveAdvert();
    });
}

function saveAdvert() {
    clearProgramAdvertErrors();

    var programCode = $("#programAdvertProgramSelect").val();
    var programName = $("#programAdvertNewProgramName").val();
    var acceptApplications = "";
    if ($("#programAdvertIsActiveRadioYes").prop("checked")) {
        acceptApplications = "true";
    } else if ($("#programAdvertIsActiveRadioNo").prop("checked")) {
        acceptApplications = "false";
    }
    var studyOptions = $("#programAdvertStudyOptionsSelect option:selected").map(function() {
        return this.value;
    }).get().join(",");
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
        url : "/pgadmissions/prospectus/programme/saveProgramAdvert",
        data : {
            sourceProgram : programCode,
            programTitle : programName,
            atasRequired : $("[name=programAdvertAtasRequired]:checked").val(),
            institutionCountry : $("#programAdvertInstitutionCountry").val(),
            institutionCode : $("#programAdvertInstitution").val(),
            otherInstitution : $("#programAdvertInstitutionOtherName").val(),
            programDescription : addBlankLinks(tinymce.get('programAdvertDescriptionText').getContent()),
            studyDurationNumber : $("#programAdvertStudyDurationInput").val(),
            studyDurationUnit : $("#programAdvertStudyDurationUnitSelect").val(),
            funding : tinymce.get('programAdvertFundingText').getContent(),
            acceptingApplications : acceptApplications,
            studyOptions : studyOptions,
            advertisingDeadlineYear : $("#programAdvertAdvertisingDeadlineYear").val()
        },
        success : function(data) {
            var map = JSON.parse(data);
            if (map['success']) {
                if ($("#programAdvertNewProgramNameDiv").is(":visible")) {
                    // new program created
                    var newProgramCode = map["programCode"];
                    $("#programAdvertProgramSelect").append($("<option />").val(newProgramCode).text(programName));
                    $("#programAdvertProgramSelect").val(newProgramCode);
                    $("#programAdvertSelectProgramDiv").show();
                    $("#programAdvertNewProgramNameDiv").hide();
                    checkToDisable();
                    getAdvertData(newProgramCode);
                }
                var programme_name = $("#programAdvertProgramSelect option:selected").text();
                infohtml = "<i class='icon-ok-sign'></i> Your advert for <b>" + programme_name + "</b> has been saved.";
                $('#infoBarProgram').addClass('alert-success').removeClass('alert-info').html(infohtml);

                $('#resourcesModal').modal('show');
            } else if (map["changeRequestRequired"]) {
                $('#dialog-box h3').text('Program Change Request');
                $('#dialog-box #dialog-message').html('<p>You will be notified after a member of the staff approve your change</p>');
                $('#dialog-box #popup-cancel-button').hide();
                $('#dialog-box').modal('show');
                lockFormFunction(true);
                confirmOpportunityChangeRequest();
            } else {
                if (map['program']) {
                    $("#programAdvertSelectProgramDiv").append(getErrorMessageHTML(map['program']));
                }
                if (map['programName']) {
                    $("#programAdvertNewProgramNameDiv").append(getErrorMessageHTML(map['programName']));
                }
                if (map['atasRequired']) {
                    $("#programAdvertAtasRequiredDiv").append(getErrorMessageHTML(map['atasRequired']));
                }
                if (map['institutionCountry']) {
                    $("#programAdvertInstitutionCountryDiv").append(getErrorMessageHTML(map['institutionCountry']));
                }
                if (map['institutionCode']) {
                    $("#programAdvertInstitutionDiv").append(getErrorMessageHTML(map['institutionCode']));
                }
                if (map['otherInstitution']) {
                    $("#programAdvertInstitutionOtherNameDiv").append(getErrorMessageHTML(map['otherInstitution']));
                }
                if (map['programDescription']) {
                    $("#programAdvertDescriptionDiv").append(getErrorMessageHTML(map['programDescription']));
                }
                if (map['funding']) {
                    $("#programAdvertFundingDiv").append(getErrorMessageHTML(map['funding']));
                }
                if (map['studyDurationNumber']) {
                    $("#programAdvertStudyDurationDiv").append(getErrorMessageHTML(map['studyDurationNumber']));
                }
                if (map['studyDurationUnit']) {
                    $("#programAdvertStudyDurationDiv").append(getErrorMessageHTML(map['studyDurationUnit']));
                }
                if (map['active']) {
                    $("#programAdvertIsActiveDiv").append(getErrorMessageHTML(map['active']));
                }
                if (map['studyOptions']) {
                    $("#programAdvertStudyOptionsDiv").append(getErrorMessageHTML(map['studyOptions']));
                }
                if (map['advertisingDeadlineYear']) {
                    $("#programAdvertAdvertisingDeadlineYearDiv").append(getErrorMessageHTML(map['advertisingDeadlineYear']));
                }
                checkIfErrors();
            }

        },
        complete : function() {
            $('#ajaxloader').fadeOut('fast');
        }
    });
}

function confirmOpportunityChangeRequest() {
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
        url : "/pgadmissions/prospectus/programme/confirmOpportunityChangeRequest",
        data : {},
        success : function(data) {
            var map = JSON.parse(data);
            if (map['success']) {
                $('#dialog-box h3').text('Program Change Request');
                $('#dialog-box #dialog-message').html('<p>You will be notified after a member of the staff approve your change</p>');
                $('#dialog-box #popup-cancel-button').hide();
                $('#dialog-box').modal('show');
            }
        },
        complete : function() {
        }
    });
}

function clearAdvert() {
    $("[name=programAdvertAtasRequired]").prop("checked", false);
    $("#institutionGroup select, #institutionGroup input, #advertGroup input, #advertGroup textarea, #advertGroup select, #programAdvertClosingDateGroup input, #programAdvertLinkToApply, #programAdvertButtonToApply").val('');
    $("#programAdvertIsActiveRadioYes, #programAdvertIsActiveRadioNo").prop('checked', false);
    $('.selectpicker').selectpicker('refresh');
    tinyMCE.get('programAdvertDescriptionText').setContent('');
    tinyMCE.get('programAdvertFundingText').setContent('');
}

function setTextAreaValue(textArea, value) {
    if (value == null) {
        value = "";
    }
    textArea.val(value);
    triggerKeyUp(textArea);
    idselect = textArea.attr("id");
    tinymce.get(idselect).setContent(value);
}

function triggerKeyUp(element) {
    var keyup = jQuery.Event("keyup");
    element.trigger(keyup);
}

function clearProgramSection() {
    clearAdvert();
    $("#programAdvertButtonToApply").val("");
    $("#programAdvertLinkToApply").val("");
    clearClosingDate();
    $('#programAdvertClosingDates tr').remove();
    checkDates();
}

function clearClosingDate() {
    $("#programAdvertDescriptionText").val("");
    $("#programAdvertClosingDateId").val("");
    $("#programAdvertClosingDateInput").val("");
    $("#programAdvertStudyPlacesInput").val("");
    $('#addProgramAdvertClosingDate').text("Add");
    $('#programAdvertClosingDateHeading').text("Add Closing Date");
    clearProgramAdvertClosingDateErrors();
}

function clearProgramAdvertClosingDateErrors() {
    $('#programAdvertClosingDateGroup .error').remove();
    clearInfoBarWarning();
}

function clearProgramAdvertErrors() {
    $("#programAdvertDiv .error").remove();
    clearInfoBarWarning();
}

function clearInfoBarWarning() {
    $('#infoBarProgram').removeClass('alert-error').addClass('alert-info').find('i').removeClass('icon-warning-sign').addClass('icon-info-sign');
    checkIfErrors();
}