var errorCodes= {
		401 : function() { window.location.reload(); },
		500 : function() { window.location.href = "/pgadmissions/error"; },
		404 : function() { window.location.href = "/pgadmissions/404"; },
		400 : function() { window.location.href = "/pgadmissions/400"; },
		403 : function() { window.location.href = "/pgadmissions/404"; }
		};

function toggleButtons(disable) {
	$('#delete-go').prop('disabled', disable);
	$('#save-go').prop('disabled', disable);
	$('#enable-go').prop('disabled', disable);
	var href = disable ? '' : '#previewModal';
	$('#modal-preview-go').attr('href', href);
	$('#modal-preview-go').attr('disabled', disable);
}

$(document).ready(function() {
	toggleButtons(true);
    $(document).on('change', 'select.templateType', function() {
            if ($(this).val()!='default') {
            	$('div.content-box-inner').css({position : 'relative'}).append('<div class="ajax" />');
            	var url = "/pgadmissions/configuration/editEmailTemplate/"+$(this).val();
            	$('.content-box-inner').append('<div class="ajax" />');
            	$.ajax({
        	        type : 'GET',
        	        statusCode : errorCodes,
        	        url : url,
        	        success : function(data) {
        	            		$('#templateContentId').val(data.content);
        	            		$('#templateContentId').prop('disabled', false);
        	            		toggleButtons(false);
        	            		$('#emailTemplateVersion').empty();
        	            		$.each(data, function(key,value){
        	            	        if (!isNaN(key)) {
        	            	        	if (value==null) {
        	            	        		value='default';
        	            	        	}
        	            	        	$('#emailTemplateVersion').append(new Option(value, key));
        	            	        }
        	            	    $('#emailTemplateVersion').val(data.activeVersion); 
        	            	    });
        	               		},
        	        complete : function() {
        	        				$('div.ajax').remove();
        	        			}
        	    });
            } else {
            	$('#templateContentId').prop('disabled', true);
            	$('#templateContentId').val('');
            	$('#emailTemplateVersion').empty();
            	toggleButtons(true);
            }
            
        });
    
    $(document).on('change', 'select.templateVersion', function() {
    	if ($(this).val()!=null) {
    		$('div.content-box-inner').css({position : 'relative'}).append('<div class="ajax" />');
    		var url = "/pgadmissions/configuration/editEmailTemplate/"+$(this).val();
    		$('.content-box-inner').append('<div class="ajax" />');
    		$.ajax({
    			type : 'GET',
    			statusCode : errorCodes,
    			url : url,
    			success : function(data) {
    				$('#templateContentId').val(data.content);
    				$('#templateContentId').prop('disabled', false);
    				toggleButtons(false);
    			},
    			complete : function() {
    				$('div.ajax').remove();
    			}
    		});
    	} else {
    		$('#templateContentId').prop('disabled', true);
    		$('#templateContentId').val('');
    		toggleButtons(true);
    	}
    	
    });
    
    $('#save-go').click(function() {
    	$('div.content-box-inner').css({position : 'relative'}).append('<div class="ajax" />');
    	 $.ajax({
    	        type : 'POST',
    	        statusCode : errorCodes,
    	        url : "/pgadmissions/configuration/saveEmailTemplate/"+$('#emailTemplateType').val(),
    	        data: {content : $('#templateContentId').val()},
    	        success : function(data) {
    	        				$('#emailTemplateVersion').append(new Option(data.version, data.id));
    	        				$('#emailTemplateVersion').val(data.id);
    	               		},
    	        complete : function() {
    	        				$('div.ajax').remove();
    	        			}
    	    });
    });
    
    
    $('#enable-go').click(function() {
    	$('div.content-box-inner').css({position : 'relative'}).append('<div class="ajax" />');
		 $.ajax({
			 type : 'POST',
			 statusCode : errorCodes,
			 url : "/pgadmissions/configuration/activateEmailTemplate/"+$('#emailTemplateType').val()+"/"+$('#emailTemplateVersion').val(),
			 success : function(data) {
			 },
			 complete : function() {
				 			$('div.ajax').remove();
			 }
		 });
    });
    
    
    $('#modal-preview-go').click(function() {
    	var html = $('#templateContentId').val();
    	html = html.replace(/\$\{host\}/g, "");
    	html = html.replace(/href=\"(.*)\"/g, "");
    	var header = $("#emailTemplateType option:selected").text();
    	$('#previewModalLabel').html(header);
    	$('#previewModalContent').html(html);
    });
    
    $('#delete-go').click(function() {
    	$('div.content-box-inner').css({position : 'relative'}).append('<div class="ajax" />');
    	$.ajax({
    		type : 'POST',
    		statusCode : errorCodes,
    		url : "/pgadmissions/configuration/deleteEmailTemplate/"+$('#emailTemplateVersion').val(),
    		data : null,
    		success : function(data) {
    			if (data.error!=null) {
    				alert(data.error);
    			}
    			else {
	    			var optionVal = $('#emailTemplateVersion').val();
	    			$("#emailTemplateVersion option[value="+optionVal+"]").remove();
	    			$('#emailTemplateVersion').val(data.activeTemplateId);
	    			$('#templateContentId').val(data.activeTemplateContent);
    			}
    		},
    		complete : function() {
    			$('div.ajax').remove();
    		}
    	});
    });
});
