$(document).ready(function() {
    $('#opportunityRequestsList').on('click', '.opportunityRequestActionType', function() {
        requestId = $(this).attr('data-request-id');
        
        $('#ajaxloader').show();
        switch ($(this).attr('data-value')) {
        case 'approve':
            window.location.href = "/pgadmissions/requests/edit/"+ requestId;
        };
    });
});
