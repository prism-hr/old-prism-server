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
    				enabled : $('input:radio[name=switch]')[0].checked,
    				batchSize : $('#batchSizeId').val(),
    				processingDelay : $('#processingDelay').val(),
    				processingDelayUnit : $('#processingDelayUnit').val()
    			};
		 $('#ajaxloader').show();
		 
    	 $.ajax({
    	        type : 'POST',
    	        statusCode : errorCodes,
    	        url : "/pgadmissions/configuration/updateThrottle/",
    	        data: options,
    	        success : function(data) {
					$("form.portico-configuration-form").find('.alert-error').remove();
					
					if (data.enabled) {
						$("#throttoleSwitchOnId").parent().append('<div class="alert alert-error"><i class="icon-warning-sign"></i>' + data.enabled + '</div>');
					}
					
					if(data.batchSize) {
						$("#batchSizeId").parent().append('<div class="alert alert-error"><i class="icon-warning-sign"></i>' + data.batchSize + '</div>');
					}
					
					if(data.processingDelay) {
						$("#processingDelay").parent().append('<div class="alert alert-error"><i class="icon-warning-sign"></i>' + data.processingDelay + '</div>');
					} else if(data.processingDelayUnit) {
						$("#processingDelayUnit").parent().append('<div class="alert alert-error"><i class="icon-warning-sign"></i>' + data.processingDelayUnit + '</div>');
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
			 $('#processingDelay').val(data.processingDelay);
			 $('#processingDelayUnit').val(data.processingDelayUnit);
		 },
		 complete : function() {}
	 });
    
  
});
