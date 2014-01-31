$(document).ready(function() {
    bindDatePicker($("#programAdvertClosingDateInput"));
    bindAddClosingDateButtonAction();
    bindSaveButtonAction();
    bindProgramSelectChangeAction();
    bindClosingDatesActions();
    bindCancelNewProgramAction();
    initEditors();
    checkDates();
    $('.selectpicker').selectpicker();
    checkToDisable();
});

function bindCancelNewProgramAction() {
    $("#programAdvertCancelNewProgramBtn").bind('click', function() {
        $("#programAdvertSelectProgramDiv").show();
        $("#programAdvertNewProgramNameDiv").hide();
        $("#programAdvertProgramSelect").selectpicker('val', '');
    });
}

function bindProgramSelectChangeAction() {
    $("#programAdvertProgramSelect").bind('change', function() {
        getProgramData();
    });
}

function checkToDisable() {
    var selectedProgram = $("#programAdvertProgramSelect").val();
    if (selectedProgram == "") {
        $("#advertGroup label, #programAdvertClosingDateGroup label").addClass("grey-label").parent().find('.hint').addClass("grey");
        $("#advertGroup input, #advertGroup textarea, #advertGroup select, #programAdvertClosingDateGroup input").attr("readonly", "readonly");
        $("#advertGroup input, #advertGroup textarea, #advertGroup select, #advertGroup button, #programAdvertClosingDateGroup input").attr("disabled", "disabled");
        $("#programAdvertClosingDateGroup a").addClass("disabled");
        $("#programAdvertClosingDates").css("display", "none");
    } else {
        $("#advertGroup label, #programAdvertClosingDateGroup label").removeClass("grey-label").parent().find('.hint').removeClass("grey");
        $("#advertGroup input, #advertGroup textarea, #advertGroup select, #programAdvertClosingDateGroup input").removeAttr("readonly", "readonly");
        $("#advertGroup input, #advertGroup textarea, #advertGroup select, #advertGroup button, #programAdvertClosingDateGroup input").removeAttr("disabled", "disabled");
        $("#programAdvertClosingDateGroup a").removeClass("disabled");
    }
}

function getProgramData() {
    clearProgramAdvertErrors();
    var programme_code = $("#programAdvertProgramSelect").val();
    var programme_name = $("#programAdvertProgramSelect option:selected").text();
    checkToDisable();
    changeHeaderInfoBars(programme_name);
    if (programme_code == "NEW_PROGRAM") {
        clearProgramSection();
        $("#programAdvertSelectProgramDiv").hide();
        $("#programAdvertNewProgramNameDiv").show();
    } else if (programme_code == "") {
        clearProgramSection();
    } else {
        getAdvertData(programme_code);
        getClosingDatesData(programme_code);

    }
}

function changeHeaderInfoBars(text) {
    if (text == "Select..." || text == "") {
        infohtml = "<i class='icon-info-sign'></i> Manage the advert for your programme here.";
        infodate = "<i class='icon-info-sign'></i> Manage closing dates for your programme here.";
        inforesource = "<i class='icon-info-sign'></i> Embed these resources to provide applicants with links to apply for your programme.";
    } else {
        infohtml = "<i class='icon-info-sign'></i> Manage the advert for: <b>" + text + "</b>.";
        infodate = "<i class='icon-info-sign'></i> Manage closing dates for: <b>" + text + "</b>.";
        inforesource = "<i class='icon-info-sign'></i> Embed these resources to provide applicants with links to apply for: <b>" + text + "</b>.";
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
        saveClosingDate();
    });
}

function saveClosingDate() {
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
            program : $("#programAdvertProgramSelect").val(),
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
                if (update) {
                    replaceClosingDateRow(map['programClosingDate']);
                } else {
                    appendClosingDateRow(map['programClosingDate']);
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
}

function replaceClosingDateRow(closingDate) {
    $('#cdr-' + closingDate.id).html(closingDateTd(closingDate));
}

function appendClosingDateRow(closingDate) {
    $('#programAdvertClosingDates tbody').append('<tr>' + '<td id="cdr-' + closingDate.id + '">' + closingDateTd(closingDate) + '</td>' + '<td>' + '<button class="button-edit" type="button" data-desc="Edit">Edit</button>' + '</td>' + '<td>' + '<button class="button-delete" type="button" data-desc="Remove">Remove</button>' + '</td>' + '</tr>');
}

function closingDateTd(closingDate) {
    var date = formatProgramClosingDate(new Date(closingDate.closingDate));
    var studyPlaces = "";
    if (closingDate.studyPlaces > 0) {
        studyPlaces = " (" + closingDate.studyPlaces + " Places)";
    }
    return date + studyPlaces + '<input id="cdr-id" type="hidden" value="' + closingDate.id + '"/>' + '<input id="cdr-closingDate" type="hidden" value="' + date + '"/>' + '<input id="cdr-studyPlaces" type="hidden" value="' + closingDate.studyPlaces + '"/>';
}

function formatProgramClosingDate(date) {
    return $.datepicker.formatDate('d M yy', date);
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
        }
    });
}

function getAdvertData(programme_code) {
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
        },
        complete : function() {
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
    var advert = map['advert'];
    if (advert) {
        setTextAreaValue($("#programAdvertDescriptionText"), advert['description']);
        setTextAreaValue($("#programAdvertFundingText"), advert['funding']);
        $("#programAdvertId").val(advert.id);
        var durationOfStudyInMonths = advert['studyDuration'];
        if (durationOfStudyInMonths % 12 == 0) {
            $("#programAdvertStudyDurationInput").val((durationOfStudyInMonths / 12).toString());
            $("#programAdvertStudyDurationUnitSelect").val('Years');
        } else {
            $("#programAdvertStudyDurationInput").val(durationOfStudyInMonths.toString());
            $("#programAdvertStudyDurationUnitSelect").val('Months');
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
            $("#programAdvertStudyOptionsSelect option[value='" + option + "']").attr("selected", true);
        });
    }
    
    $('#programAdvertAdvertisingDeadlineYear').find('option').remove().end();
    if (map['possibleAdvertisingDeadlines']) {
        var advertisingDeadline = map['advertisingDeadline'];
        map['possibleAdvertisingDeadlines'].forEach(function(deadline) {
            var selected = deadline == advertisingDeadline ? "selected" : "";
            $('#programAdvertAdvertisingDeadlineYear').append('<option value="' + deadline + '" ' + selected + '>30 September ' + deadline + '</option>');
        });
    }

    if (map['isCustomProgram']) {
        $('#programAdvertAdvertisingDeadlineYear, #programAdvertStudyOptionsSelect').removeAttr("readonly", "readonly").removeAttr("disabled", "disabled");
    } else {
        $('#programAdvertAdvertisingDeadlineYear, #programAdvertStudyOptionsSelect').attr("readonly", "readonly").attr("disabled", "disabled");
    }
}

function bindSaveButtonAction() {
    $("#programAdvertSave").bind('click', function() {
        saveAdvert();
    });
}

function saveAdvert() {
    clearProgramAdvertErrors();
    var duration = {
        value : $("#programAdvertStudyDurationInput").val(),
        unit : $("#programAdvertStudyDurationUnitSelect").val()
    };
    var acceptApplications = "";
    if ($("#programAdvertIsActiveRadioYes").prop("checked")) {
        acceptApplications = "true";
    } else if ($("#programAdvertIsActiveRadioNo").prop("checked")) {
        acceptApplications = "false";
    }
    var studyOptions = $("#programAdvertStudyOptionsSelect option:selected").map(function(){ return this.value; }).get().join(",");

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
            programCode : $("#programAdvertProgramSelect").val(),
            description : addBlankLinks(tinymce.get('programAdvertDescriptionText').getContent()),
            studyDuration : JSON.stringify(duration),
            funding : tinymce.get('programAdvertFundingText').getContent(),
            active : acceptApplications,
            studyOptions : studyOptions,
            advertiseDeadlineYear : $("#programAdvertAdvertisingDeadlineYear").val()
        },
        success : function(data) {
            var map = JSON.parse(data);
            if (!map['success']) {
                if (map['program']) {
                    $("#programAdvertSelectProgramDiv").append(getErrorMessageHTML(map['program']));
                }
                if (map['description']) {
                    $("#programAdvertDescriptionDiv").append(getErrorMessageHTML(map['description']));
                }
                if (map['funding']) {
                    $("#programAdvertFundingDiv").append(getErrorMessageHTML(map['funding']));
                }
                if (map['studyDuration']) {
                    $("#programAdvertStudyDurationDiv").append(getErrorMessageHTML(map['studyDuration']));
                }
                if (map['active']) {
                    $("#programAdvertIsActiveDiv").append(getErrorMessageHTML(map['active']));
                }
                if (map['studyOptions']) {
                    $("#programAdvertStudyOptionsDiv").append(getErrorMessageHTML(map['studyOptions']));
                }
                if (map['advertiseDeadlineYear']) {
                    $("#programAdvertAdvertisingDeadlineYearDiv").append(getErrorMessageHTML(map['advertiseDeadlineYear']));
                }
                checkIfErrors();
            } else {
                var programme_name = $("#programAdvertProgramSelect option:selected").text();
                infohtml = "<i class='icon-ok-sign'></i> Your advert for <b>" + programme_name + "</b> has been saved.";
                $('#infoBarProgram').addClass('alert-success').removeClass('alert-info').html(infohtml);
                
                $('#resourcesModal').modal('show');
            }

        },
        complete : function() {
            $('#ajaxloader').fadeOut('fast');
        }
    });
}

function clearAdvert() {
    $("#advertGroup input, #advertGroup textarea, #programAdvertClosingDateGroup input, #programAdvertLinkToApply, #programAdvertButtonToApply, #programAdvertStudyOptionsSelect, #programAdvertAdvertisingDeadlineYear").val('');
    $("#programAdvertIsActiveRadioYes, #programAdvertIsActiveRadioNo").prop('checked', false);
    tinyMCE.execCommand("mceRepaint");
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
    clearProgramAdvertErrors();
    clearAdvert();
    $("#programAdvertButtonToApply").val("");
    $("#programAdvertLinkToApply").val("");
    clearClosingDate();
    $('#programAdvertClosingDates tr').remove();
    checkDates();
    initEditors();
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