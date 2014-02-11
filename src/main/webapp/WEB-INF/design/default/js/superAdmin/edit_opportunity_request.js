$(document).ready(function() {
    bindDatePicker($("#applicationStartDate"));
    $('.selectpicker').selectpicker();
    
    refreshControls();

    $('#institution').change(function() {
        $("#otherInstitution").val("");
        refreshControls();
    });

    $('#institutionCountry').change(function() {
        institutionCountryChanged();
    });
    
    $('#approveOpportunityButton').click(function(e) {
        $("#respondToOpportunityActionInput").val("APPROVE");
        $('#opportunityRequestEditForm').submit();
    });
    
    $('#rejectOpportunityButton').click(function(e) {
        $("#respondToOpportunityActionInput").val("REJECT");
        $('#opportunityRequestEditForm').submit();
    });

    initEditors();
    exStatus();
});

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
        },
        complete : function() {
            refreshControls();
        }
    });
}

function refreshControls(){
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
        selector: "#programDescription",
        width: 480,
        height : 180,
        menubar: false,
        content: "",
        toolbar: "bold italic  | bullist numlist outdent indent | link unlink | undo redo"
    });
}

$(document).on('click', '#commentsBtn', function(){
    // Set the current tab.
    $('.tabsContent ul.tabs li').removeClass('current');
    $(this).parent('li').addClass('current');
    $('#requestTab').hide();
    $('#commentsTab').show();
});

$(document).on('click', '#requestBtn', function(){
    // Set the current tab.
    $('.tabsContent ul.tabs li').removeClass('current');
    $(this).parent('li').addClass('current');
    $('#commentsTab').hide();
    $('#requestTab').show();
});

