$(document).ready(function() {
    $('.selectpicker').selectpicker();

    $(document).on('change', 'select.opportunityRequestActionType', function() {
        requestId = $(this).attr('data-request-id');
        
        $('#ajaxloader').show();
        switch ($(this).val()) {
        case 'approve':
            window.location.href = "/pgadmissions/requests/edit/"+ requestId;
            break;
        case 'view':  window.location.href = "/pgadmissions/requests/edit/"+ requestId;
            break;
        case 'email':
            $('#ajaxloader').fadeOut('fast');
            $(this).val($("select.actionType option:first").val());
            var subject = "Question Regarding UCL Prism Program request: " + $(this).attr('data-program');
            var email = $(this).attr('data-email');
            window.location.href ="mailto:" + email + "?subject=" + subject;
            skip = true;
            break;
        };
    });
});
