$(document).ready(function() {
    bindDatePicker($("#applicationStartDate"));
    $('.selectpicker').selectpicker();

    refreshControls();

    $("a[name=didYouMeanInstitutionButtonYes]").bind('click', function() {
        var text = $(this).text();
        $("#otherInstitution").val(text);
        $("#didYouMeanInstitutionDiv").remove();
    });

    $("a[name=didYouMeanInstitutionButtonNo]").bind('click', function() {
        $("#didYouMeanInstitutionDiv").remove();
        $("#forceCreatingNewInstitution").val("true");
    });

    $('#otherInstitution').change(function() {
        $("#forceCreatingNewInstitution").val("false");
    });

    $("#otherInstitution").typeaheadmap({
        source : {},
        key : "name",
        displayer : function(that, item, highlighted) {
            return highlighted;
        }
    });

    var availableInstitutions = [];
    $('#institution option').each(function() {
        var v = $(this).val();
        if (v != "OTHER" && v != "") {
            availableInstitutions.push({
                name : $(this).text()
            });
        }
    });
    
    var typeahead = $("#otherInstitution").data("typeaheadmap");
    typeahead.source = availableInstitutions;

    $('#institution').change(function() {
        $("#otherInstitution").val("");
        refreshControls();
    });

    $('#institutionCountry').change(function() {
        institutionCountryChanged();
    });

    $('#submitOpportunityRequestButton').click(function(e) {
        $('#opportunityRequestEditForm').submit();
    });


    initEditors();
    exStatus();
    checkFormErrors();
});

function checkFormErrors() {
    var errorCount = $('#opportunityRequestEditForm .alert-error').length;
    if (errorCount > 0) {
        $('#opportunityRequestEditForm').prepend('<div id="info-section" class="alert alert-error"><i class="icon-warning-sign"></i>You have some errors in the form</div>');
    } else {
        if ($('#info-section').length > 0) {
            $('#info-section').remove();
        }
    }
}
function institutionCountryChanged() {
    $("#institution").val("");
    $("#otherInstitution").val("");

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
            var options = $("#institution");
            $("#institution").empty();

            options.append($("<option />").val("").text("Select..."));
            for ( var i = 0; i < institutions.length; i++) {
                options.append($("<option />").val(institutions[i]["code"]).text(institutions[i]["name"]));
            }
            options.append($("<option />").val("OTHER").text("Other"));

            var typeahead = $("#otherInstitution").data("typeaheadmap");
            typeahead.source = institutions;
        },
        complete : function() {
            refreshControls();
        }
    });
}

function refreshControls() {
    if ($('#institutionCountry').val() === "") {
        $("#institution").attr("readonly", "readonly");
        $("#institution").attr("disabled", "disabled");
    } else {
        $("#institution").removeAttr("readonly", "readonly");
        $("#institution").removeAttr("disabled", "disabled");
    }

    if ($('#institution').val() === "OTHER") {
        $("#otherInstitution").removeAttr("readonly", "readonly");
        $("#otherInstitution").removeAttr("disabled", "disabled");
    } else {
        $("#otherInstitution").attr("readonly", "readonly");
        $("#otherInstitution").attr("disabled", "disabled");
    }
    $("#institution").selectpicker('refresh');
}

function initEditors() {
    tinymce.init({
        selector : "#programDescription",
        width : 480,
        height : 180,
        menubar : false,
        content : "",
        toolbar : "bold italic  | bullist numlist outdent indent | link unlink | undo redo"
    });
}

$(document).on('click', '#commentsBtn', function() {
    // Set the current tab.
    $('.tabsContent ul.tabs li').removeClass('current');
    $(this).parent('li').addClass('current');
    $('#requestTab').hide();
    $('#commentsTab').show();
});

$(document).on('click', '#requestBtn', function() {
    // Set the current tab.
    $('.tabsContent ul.tabs li').removeClass('current');
    $(this).parent('li').addClass('current');
    $('#commentsTab').hide();
    $('#requestTab').show();
});
