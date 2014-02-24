$(document).ready(function() {
    bindDatePicker($("#programAdvertClosingDateInput"));
    bindAddClosingDateButtonAction();
    bindSaveButtonAction();
    bindProgramSelectChangeAction();
    bindClosingDatesActions();
    bindCancelNewProgramAction();
    bindChangeInstitutionCountryAction();
    bindChangeOtherInstitutionAction();

    initEditors();
    $('select.selectpicker').selectpicker();

    $("#programAdvertInstitutionOtherName").typeaheadmap({
        source : {},
        key : "name",
        displayer : function(that, item, highlighted) {
            return highlighted;
        }
    });

    checkToDisable();
});

function bindChooseSuggestedInstitutionNameAction() {
    $("a[name=didYouMeanInstitutionButtonYes]").bind('click', function() {
        var text = $(this).text();
        $("#programAdvertInstitutionOtherName").val(text);
        $("#didYouMeanInstitutionDiv").remove();
    });

    $("a[name=didYouMeanInstitutionButtonNo]").bind('click', function() {
        $("#didYouMeanInstitutionDiv").remove();
        $("#programAdvertForceCreatingNewInstitution").val("true");
    });
}

function bindChangeOtherInstitutionAction() {
    $('#programAdvertInstitutionOtherName').change(function() {
        $("#programAdvertForceCreatingNewInstitution").val("false");
    });
}

function bindChangeInstitutionCountryAction() {
    $('#programAdvertInstitutionCountry').change(function() {
        getInstitutionData();
    });
}

function programChanged(){
    clearProgramAdvertErrors();
    changeHeaderInfoBars();
    var programme_code = $("#programAdvertProgramSelect").val();
    if (programme_code == "") {
        clearProgramSection();
        checkToDisable();
    } else {
        getAdvertData(programme_code);
        getClosingDatesData(programme_code);
    }
}

function bindCancelNewProgramAction() {
    $("#programAdvertCancelNewProgramBtn").bind('click', function() {
        $("#programAdvertSelectProgramDiv").show();
        $("#programAdvertNewProgramNameDiv").hide();
        programChanged();
    });
}

function bindProgramSelectChangeAction() {
    $("#programAdvertProgramSelect").bind('change', function() {
        programChanged();
    });
}

$(document).on('click', '#newProgamme', function() {
    $("#programAdvertSelectProgramDiv").hide();
    $("#programAdvertNewProgramNameDiv").show();
    $("#programAdvertProgramSelect").val("");
    programChanged();
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

            var typeahead = $("#programAdvertInstitutionOtherName").data("typeaheadmap");
            typeahead.source = userInstitutions.concat(otherInstitutions);

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
    var existingProgramSelected = selectedProgram != "";
    var newProgramSelected = $("#programAdvertNewProgramNameDiv").is(":visible");
    var programLocked = $("#programAdvertProgramLocked").val() == "true";
    var isCustom = $("#programAdvertIsCustom").val() == "true";
    if ((existingProgramSelected && !programLocked) || newProgramSelected) {
        // new or existing program
        $("#institutionGroup label, #advertGroup label #programAdvertAtasRequiredLabel").removeClass("grey-label").parent().find('.hint').removeClass("grey");
        $("#institutionGroup input, #institutionGroup select, #advertGroup input, #advertGroup textarea, #advertGroup select").removeAttr("readonly", "readonly");
        $("#institutionGroup input, #institutionGroup select, #advertGroup input, #advertGroup textarea, #advertGroup select, #advertGroup button, input[name=programAdvertAtasRequired]").removeAttr("disabled", "disabled");
    } else {
        $("#institutionGroup label, #advertGroup label #programAdvertAtasRequiredLabel").addClass("grey-label").parent().find('.hint').addClass("grey");
        $("#institutionGroup input, #institutionGroup select, #advertGroup input, #advertGroup textarea, #advertGroup select").attr("readonly", "readonly");
        $("#institutionGroup input, #institutionGroup select, #advertGroup input, #advertGroup textarea, #advertGroup select, #advertGroup button, input[name=programAdvertAtasRequired]").attr("disabled", "disabled");
    }
    
    if ((existingProgramSelected && !programLocked && isCustom) || newProgramSelected) {
        $('#programAdvertAdvertisingDeadlineYear, #programAdvertStudyOptionsSelect').removeAttr("readonly", "readonly").removeAttr("disabled", "disabled");
    } else {
        $('#programAdvertAdvertisingDeadlineYear, #programAdvertStudyOptionsSelect').prop("readonly", true).attr("disabled", "disabled");
    }

    if (existingProgramSelected != "" && !programLocked) {
        // existing program
        $("#programAdvertClosingDateGroup label").removeClass("grey-label").parent().find('.hint').removeClass("grey");
        $("#programAdvertClosingDateGroup input").removeAttr("readonly", "readonly");
        $("#programAdvertClosingDateGroup input, #programAdvertClosingDateGroup a").removeAttr("disabled", "disabled");
    } else {
        $("#programAdvertClosingDateGroup label").addClass("grey-label").parent().find('.hint').addClass("grey");
        $("#programAdvertClosingDateGroup input").attr("readonly", "readonly");
        $("#programAdvertClosingDateGroup input, #programAdvertClosingDateGroup a").attr("disabled", "disabled");
    }

    otherInstitutionCheck();
    
    $('select.selectpicker').selectpicker('refresh');
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
            checkToDisable();
        },
        complete : function() {
            $('#ajaxloader').fadeOut('fast');
        }
    });
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
    $("#programAdvertProgramLocked").val(map["programLocked"]);
    $("#programAdvertIsCustom").val(map["isCustomProgram"]);

    $("[name=programAdvertAtasRequired][value=" + map["atasRequired"] + "]").prop("checked", true);

    $("#programAdvertInstitutionCountry").selectpicker('val', map["institutionCountryCode"]);
    getInstitutionData(function() {
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
            forceCreatingNewInstitution : $("#programAdvertForceCreatingNewInstitution").val(),
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
                var newProgramCode = map["programCode"];

                if (programCode != "") {
                    // modfy existing program option
                    $("#programAdvertProgramSelect option").filter("[value='" + programCode + "']").val(newProgramCode);
                } else {
                    // add new program option
                    $("#programAdvertProgramSelect").append($("<option />").val(newProgramCode).text(programName));
                    $("#programAdvertProgramSelect").val(newProgramCode);
                }

                // show select control
                $("#programAdvertSelectProgramDiv").show();
                $("#programAdvertNewProgramNameDiv").hide();

                // reload program data
                getAdvertData(newProgramCode);

                var programme_name = $("#programAdvertProgramSelect option:selected").text();
                infohtml = "<i class='icon-ok-sign'></i> Your advert for <b>" + programme_name + "</b> has been saved.";
                $('#infoBarProgram').addClass('alert-success').removeClass('alert-info').html(infohtml);

                $('#resourcesModal').modal('show');
            } else if (map["changeRequestCreated"]) {
                $('#dialog-box h3').text('Program Change Request');
                $('#dialog-box #dialog-message').html('<p>You will be notified after a member of the staff approve your change</p>');
                $('#dialog-box #popup-cancel-button').hide();
                $('#dialog-box').modal('show');

                $("#programAdvertProgramLocked").val(true);
                checkToDisable();
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
                    if (map.otherInstitution.errorCode == "institution.did.you.mean") {
                        var message = "Did you mean: ";
                        var institutionNames = map.otherInstitution.institutions.split("::");
                        for ( var i = 0; i < institutionNames.length; i++) {
                            message += '<a name="didYouMeanInstitutionButtonYes">' + institutionNames[i] + '</a>';
                            message += i == institutionNames.length - 1 ? ". " : ", ";
                        }
                        message += '<a name="didYouMeanInstitutionButtonNo">Use original</a>.';
                        $("#programAdvertInstitutionOtherNameDiv").append($(getErrorMessageHTML(message)).attr("id", "didYouMeanInstitutionDiv"));
                        bindChooseSuggestedInstitutionNameAction();
                    } else {
                        $("#programAdvertInstitutionOtherNameDiv").append(getErrorMessageHTML(map['otherInstitution']));
                    }
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

function clearAdvert() {
    $("[name=programAdvertAtasRequired]").prop("checked", false);
    $("#institutionGroup input, #advertGroup input, #advertGroup textarea, #programAdvertClosingDateGroup input, #programAdvertLinkToApply, #programAdvertButtonToApply, #advertGroup select").val('');
    $("#programAdvertIsActiveRadioYes, #programAdvertIsActiveRadioNo").prop('checked', false);
    $('#institutionGroup select').selectpicker("val", "");
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
