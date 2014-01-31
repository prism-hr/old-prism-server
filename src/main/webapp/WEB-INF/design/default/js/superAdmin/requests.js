$(document).ready(function() {
    $('.selectpicker').selectpicker();

    $(document).on('change', 'select.opportunityRequestActionType', function() {
        requestId = $(this).attr('data-request-id');
        
        $('#ajaxloader').show();
        switch ($(this).val()) {
        case 'approve':
            window.location.href = "/pgadmissions/requests/edit/"+ requestId;
        };
    });
});
