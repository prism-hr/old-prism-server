var errorCodes= {
		401 : function() { window.location.reload(); },
		500 : function() { window.location.href = "/pgadmissions/error"; },
		404 : function() { window.location.href = "/pgadmissions/404"; },
		400 : function() { window.location.href = "/pgadmissions/400"; },
		403 : function() { window.location.href = "/pgadmissions/404"; }
		};
$(document).ready(function() {

       
    $('#apply-throttle-go').click(function() {
    	var options =
    			{
    				id : $('#throttleId').val(),
    				enabled : $('input:radio[name=switch]')[0].checked,
    				batchSize : $('#batchSizeId').val()
    			};
		 $('#ajaxloader').show();
		 
    	 $.ajax({
    	        type : 'POST',
    	        statusCode : errorCodes,
    	        url : "/pgadmissions/configuration/updateThrottle/",
    	        data: options,
    	        success : function(data) {
							$("#batchSizeId").parent().find('.alert-error').remove();
    	        				if (data.error!=null) {
    	        					
									$("#batchSizeId").parent().append('<div class="alert alert-error"><i class="icon-warning-sign"></i> The throttling batch size must a number greater than 0.</div>');
    	        					$('#batchSizeId').val('');
    	        				}
    	               		},
    	        complete : function() {
    	        				$('#ajaxloader').fadeOut('fast');
								
    	        			}
    	    });
    });
    
    
	 $.ajax({
		 type : 'GET',
		 statusCode : errorCodes,
		 url : "/pgadmissions/configuration/getThrottle",
		 success : function(data) {
			 var index = data.enabled == true ? 0 : 1;
			 if (data.enabled!=null) {
				 if ($('#edit-throttle-section').length > 0) {
			 		$('input:radio[name=switch]')[index].checked=true;
				 }
		 	}
			 $('#batchSizeId').val(data.batchSize);
			 $('#throttleId').val(data.throttleId);
		 },
		 complete : function() {}
	 });
    
  
});
