$(document).ready(function() {
    
    $('#badgeSaveButton').click(function() {
        updateBadge();
        $.ajax({
             type: 'POST',
             statusCode: {
                  401: function() {
                      window.location.reload();
                  },
                  500: function() {
                      window.location.href = "/pgadmissions/error";
                  },
                  404: function() {
                      window.location.href = "/pgadmissions/404";
                  },
                  400: function() {
                      window.location.href = "/pgadmissions/400";
                  },                  
                  403: function() {
                      window.location.href = "/pgadmissions/404";
                  }
              },
            url:"/pgadmissions/badge/saveBadge",
            data: {
                closingDate: $('#batchdeadline').val(),
                projectTitle: $('#project').val(),
                program: $('#programme').val()
            },
            success: function(data) {
                //$('#badgeSection').html(data);
            },
            completed: function() {
            }
        });
    });
});