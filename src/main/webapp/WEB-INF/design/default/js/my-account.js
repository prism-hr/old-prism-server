$(document).ready(function() {
    
    getAccountDetailsSection();
    
    $(document).on('click', '.button-delete', function(event) {
        var postData ={ 
                email : $(this).attr("email"),
        };
        
        $('.content-box-inner').append('<div class="ajax" />');
        
        $.ajax({
            type: 'POST',
             statusCode: {
                  401: function() { window.location.reload(); },
                  500: function() { window.location.href = "/pgadmissions/error"; },
                  404: function() { window.location.href = "/pgadmissions/404"; },
                  400: function() { window.location.href = "/pgadmissions/400"; },                  
                  403: function() { window.location.href = "/pgadmissions/404"; }
             },
             url:"/pgadmissions/myAccount/deleteLinkedAccount", 
             data:$.param(postData),
             success: function(data) {
                 if(data == "OK") {    
                     window.location.href = '/pgadmissions/myAccount?messageCodeLink=account.unlinked';
                } else {                
                    $('#accountdetails').html(data);
                    addToolTips();
                }
             },
             complete: function() {
                 $('.content-box-inner div.ajax').remove();
             }
        });
    });
    
    $('#accountdetails').on('click', "#saveChanges", function() {
        var postData ={ 
                email : $('#email').val(),
                firstName : $('#firstName').val(),
                firstName2 : $('#firstName2').val(),
                firstName3 : $('#firstName3').val(),
                lastName : $('#lastName').val(),
                password : $('#currentPassword').val(),
                newPassword : $('#newPassword').val(),
                confirmPassword : $('#confirmNewPass').val()
        };
        
        $('.content-box-inner').append('<div class="ajax" />');
        
        $.ajax({
            type: 'POST',
             statusCode: {
                  401: function() { window.location.reload(); },
                  500: function() { window.location.href = "/pgadmissions/error"; },
                  404: function() { window.location.href = "/pgadmissions/404"; },
                  400: function() { window.location.href = "/pgadmissions/400"; },                  
                  403: function() { window.location.href = "/pgadmissions/404"; }
             },
             url:"/pgadmissions/myAccount/submit", 
             data:$.param(postData),
             success: function(data) {
                 if(data == "OK"){    
                     window.location.href = '/pgadmissions/myAccount?messageCode=account.updated';
                }else{                
                    $('#accountdetails').html(data);
                    addToolTips();
                }
             },
             complete: function() {
                 $('.content-box-inner div.ajax').remove();
             }
        });
    });
    
    $('#accountdetails').on('click', "#cancelMyACnt", function() {
        var $form = $(this).closest('form');
        clearForm($form);
    });
    
    $('#accountdetails').on('click', '#linkAccounts', function() {
        var postData ={ 
                email : $('#linkEmail').val(),
                currentPassword : $('#linkCurrentPassword').val(),
                password : $('#linkPassword').val()
        };
        
        $('.content-box-inner').append('<div class="ajax" />');
        
        $.ajax({
            type: 'POST',
             statusCode: {
                  401: function() { window.location.reload(); },
                  500: function() { window.location.href = "/pgadmissions/error"; },
                  404: function() { window.location.href = "/pgadmissions/404"; },
                  400: function() { window.location.href = "/pgadmissions/400"; },                  
                  403: function() { window.location.href = "/pgadmissions/404"; }
             },
             url:"/pgadmissions/myAccount/link", 
             data:$.param(postData),
             success: function(data) {
                 if(data == "OK") {    
                     window.location.href = '/pgadmissions/myAccount?messageCodeLink=account.linked';
                } else {                
                    $('#accountdetails').html(data);
                    addToolTips();
                }
             },
             complete: function() {
                 $('.content-box-inner div.ajax').remove();
             }
        });
    });
});

function getAccountDetailsSection() {
    $('.content-box-inner').append('<div class="ajax" />');
    var url = "/pgadmissions/myAccount/section";    
    if($('#messageCode').val() != ''){
        url = url + "?messageCode=" + $('#messageCode').val();
    }
    if($('#messageCodeLink').val() != ''){
        url = url + "?messageCodeLink=" + $('#messageCodeLink').val();
    }
    $.ajax({
        type: 'GET',
        statusCode: {
            401: function() { window.location.reload(); },
            500: function() { window.location.href = "/pgadmissions/error"; },
            404: function() { window.location.href = "/pgadmissions/404"; },
            400: function() { window.location.href = "/pgadmissions/400"; },                  
            403: function() { window.location.href = "/pgadmissions/404"; }
        },
        url:url,
        success: function(data) {
            $('#accountdetails').html(data);
            addToolTips();
        },
        complete: function() {
            $('.content-box-inner div.ajax').remove();
            var urllocation = location.href;
            if (urllocation.indexOf("#") != -1 && urllocation.indexOf("#linkAcountDetailsSection") > -1) {
                window.location.hash = "linkAcountDetailsSectionChromeFix";
                window.location.hash = "linkAcountDetailsSection";
            }
        }
    });
}
