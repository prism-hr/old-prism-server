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
    	$('div.content-box-inner').css({position : 'relative'}).append('<div class="ajax" />');
    	 $.ajax({
    	        type : 'POST',
    	        statusCode : errorCodes,
    	        url : "/pgadmissions/configuration/updateThrottle/",
    	        data: options,
    	        success : function(data) {
    	        				if (data.error!=null) {
    	        					alert(data.error);
    	        					$('#batchSizeId').val('');
    	        				}
    	               		},
    	        complete : function() {
    	        				$('div.ajax').remove();
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
			 	$('input:radio[name=switch]')[index].checked=true;
		 	}
			 $('#batchSizeId').val(data.batchSize);
			 $('#throttleId').val(data.throttleId);
		 },
		 complete : function() {}
	 });
    
  
});
