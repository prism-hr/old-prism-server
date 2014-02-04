$(document).ready(function() {
    bindDatePicker($('#applicationStartDate'));
    $('.selectpicker').selectpicker();

    $(window).bind('resize', function() {
        setHsize();
    });
    setHsize();
    refreshControls();

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
                    options.append($("<option />").val(institutions[i][1]).text(institutions[i][2]));
                }
                options.append($("<option />").val("OTHER").text("Other"));
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
