$(document).ready(function() {
    bindDatePicker($('#applicationStartDate'));
    $('.selectpicker').selectpicker();

    $(window).bind('resize', function() {
        setHsize();
    });
    setHsize();
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
                options.empty();

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
    });
    initEditors();
});

function setHsize() {
    var container;
    var paddings = 32;
    var header = $('#pholder header').height();
    var footer = $('#pholder footer').height();
    var isEmbed = window != window.parent;
    if (isEmbed) {
        container = $(window).height();
    } else {
        container = $('#pholder').parent().parent().height();
    }
    var sum = container - header - footer - paddings;
    $('#plist').height(sum);
}

function refreshControls() {
    if ($('#institutionCountry').val() === "") {
        $("#institution").attr("readonly", "readonly");
        $("#institution").attr("disabled", "disabled");
        $("#lbl-providerName").addClass("grey-label").parent().find('.hint').addClass("grey");
    } else {
        $("#institution").removeAttr("readonly", "readonly");
        $("#institution").removeAttr("disabled", "disabled");
        $("#lbl-providerName").removeClass("grey-label").parent().find('.hint').removeClass("grey");
    }
    if ($('#institution').val() === "OTHER") {
        $("#otherInstitution").removeAttr("readonly", "readonly");
        $("#otherInstitution").removeAttr("disabled", "disabled");
        $("#lbl-otherInstitutionProviderName").removeClass("grey-label").parent().find('.hint').removeClass("grey");
    } else {
        $("#otherInstitution").attr("readonly", "readonly");
        $("#otherInstitution").attr("disabled", "disabled");
        $("#lbl-otherInstitutionProviderName").addClass("grey-label").parent().find('.hint').addClass("grey");
    }
    $("#institution").selectpicker('refresh');
    refreshAtasRequiredField();
}

function refreshAtasRequiredField() {
    if($("#institutionCountry option:selected").text().trim() == "United Kingdom") {
        $("#atasRequiredLabel").removeClass("grey-label").parent().find('.hint').removeClass("grey");
        $("[name=atasRequired]").removeAttr("disabled", "disabled");
        $("[name=atasRadioValueText]").removeClass("grey-label");
    } else {
        $("#atasRequiredLabel").addClass("grey-label").parent().find('.hint').addClass("grey");
        $("[name=atasRequired]").attr("disabled", "disabled");
        $("[name=atasRadioValueText]").addClass("grey-label");
        $("[name=atasRequired]").prop("checked", false);
    }
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
