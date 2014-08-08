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

var emailTemplateContent;
var emailTemplateSubject;

$(document).ready(function() {
	
	toggleButtons(true);
    $(document).on('change', 'select.templateType', function() {
            if ($(this).val()!='original template') {
            	$('#ajaxloader').show();
            	var url = "/pgadmissions/configuration/editEmailTemplate/"+$(this).val();
            	$('#ajaxloader').show();
            	$.ajax({
        	        type : 'GET',
        	        statusCode : errorCodes,
        	        url : url,
        	        success : function(data) {
        	            		$('#templateContentId').val(data.content);
        	            		$('#templateSubjectId').val(data.subject);
        	            		emailTemplateContent=$('#templateContentId').val();
        	            		emailTemplateSubject=$('#templateSubjectId').val();
        	            		$('#templateContentId').prop('disabled', false);
        	            		$('#templateSubjectId').prop('disabled', false);
        	            		toggleButtons(false);
        	            		$('#emailTemplateVersion').empty();
        	            		$.each(data, function(key,value){
        	            	        if (!isNaN(key)) {
        	            	        	if (value==null) {
        	            	        		value='original template';
        	            	        	}
        	            	        	if (data.activeVersion==key) {
        	            	        		value=value + ' (default)';
        	            	        	}
        	            	        	$('#emailTemplateVersion').append(new Option(value, key));
        	            	        }
        	            	    $('#emailTemplateVersion').val(data.activeVersion); 
        	            	    });
        	               		},
        	        complete : function() {
        	        				$('#ajaxloader').fadeOut('fast');
        	        			}
        	    });
            } else {
            	$('#templateContentId').prop('disabled', true);
            	$('#templateSubjectId').prop('disabled', true);
            	$('#templateContentId').val('');
            	$('#templateSubjectId').val('');
            	emailTemplateContent='';
            	emailTemplateSubject='';
            	$('#emailTemplateVersion').empty();
            	toggleButtons(true);
            }
            
        });
    
    $(document).on('change', 'select.templateVersion', function() {
    	if ($(this).val()!=null) {
    		$('#ajaxloader').show();
    		var url = "/pgadmissions/configuration/editEmailTemplate/"+$(this).val();
    		$('#ajaxloader').show();
    		$.ajax({
    			type : 'GET',
    			statusCode : errorCodes,
    			url : url,
    			success : function(data) {
    				$('#templateContentId').val(data.content);
    				$('#templateSubjectId').val(data.subject);
    				emailTemplateContent=$('#templateContentId').val();
    				emailTemplateSubject=$('#templateSubjectId').val();
    				$('#templateContentId').prop('disabled', false);
    				$('#templateSubjectId').prop('disabled', false);
    				toggleButtons(false);
    			},
    			complete : function() {
    				$('#ajaxloader').fadeOut('fast');
    			}
    		});
    	} else {
    		$('#templateContentId').prop('disabled', true);
    		$('#templateSubjectId').prop('disabled', true);
    		$('#templateContentId').val('');
    		$('#templateSubjectId').val('');
    		emailTemplateContent='';
    		emailTemplateSubject='';
    		toggleButtons(true);
    	}
    	
    });
    
    $('#save-go').click(function() {
		$("#templateContentId").parent().find('.alert-info').remove();
    	if ($('#templateContentId').val()!=emailTemplateContent || $('#templateSubjectId').val()!=emailTemplateSubject) {//user has changed template or the subject before re-enabling it
    		 $('#ajaxloader').show();
	    	 $.ajax({
	    	        type : 'POST',
	    	        statusCode : errorCodes,
	    	        url : "/pgadmissions/configuration/saveEmailTemplate/"+$('#emailTemplateType').val(),
	    	        data: {content : $('#templateContentId').val(), subject : $('#templateSubjectId').val()},
	    	        success : function(data) {
				    	        	if (data.error!=null) {
				    					$("#templateContentId").parent().append('<div class="alert alert-error"><i class="icon-warning-sign"></i> '+data.error+'</div>');
				        			}
				    	        	else {
		    	        				$('#emailTemplateVersion').append(new Option(data.version, data.id));
		    	        				$('#emailTemplateVersion').val(data.id);
										$("#templateContentId").parent().find('.alert-error').remove();
										emailTemplateContent=$('#templateContentId').val();
										emailTemplateSubject=$('#templateSubjectId').val();
				    	        	}
	    	               		},
	    	        complete : function() {
	    	        				$('#ajaxloader').fadeOut('fast');
	    	        			}
	    	    });
    	} else {
			$("#templateContentId").parent().append('<div class="alert alert-info"><i class="icon-info-sign"></i> You have not made any changes.</div>');
		}
    });
    
    
    $('#enable-go').click(function() {
    	$('#ajaxloader').show();
    	var options;
    	if ($('#templateContentId').val()!=emailTemplateContent || $('#templateSubjectId').val()!=emailTemplateSubject) {//user has changed template before rnabling it
    		options = {
    				saveCopy : true,
    				newContent : $('#templateContentId').val(),
    				newSubject : $('#templateSubjectId').val()
			};
    	}
    	else {
    		options = {
    		        saveCopy : false
			};
    	}
		 $.ajax({
			 type : 'POST',
			 statusCode : errorCodes,
			 url : "/pgadmissions/configuration/activateEmailTemplate/"+$('#emailTemplateType').val()+"/"+$('#emailTemplateVersion').val(),
			 data : options,
			 success : function(data) {
				 $("#templateContentId").parent().find('.alert-error').remove();
				 if (options.saveCopy) {
					$('#emailTemplateVersion').append(new Option(data.version, data.id));
     				$('#emailTemplateVersion').val(data.id);
				 }
				 var previouslyActiveTxt = $('#emailTemplateVersion option[value='+data.previousTemplateId+']').text();
				 previouslyActiveTxt = previouslyActiveTxt.replace('(default)', '');
				 $('#emailTemplateVersion option[value='+data.previousTemplateId+']').text(previouslyActiveTxt);

				 var selectedId=$('#emailTemplateVersion').val();
				 var text = $('#emailTemplateVersion option[value='+selectedId+']').text();
				 $('#emailTemplateVersion option[value='+selectedId+']').text(text+' (default)');
				 
			 },
			 complete : function() {
				 $('#ajaxloader').fadeOut('fast');
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
    
});
