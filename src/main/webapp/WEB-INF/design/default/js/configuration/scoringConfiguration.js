var errorCodes= {
		401 : function() { window.location.reload(); },
		500 : function() { window.location.href = "/pgadmissions/error"; },
		404 : function() { window.location.href = "/pgadmissions/404"; },
		400 : function() { window.location.href = "/pgadmissions/400"; },
		403 : function() { window.location.href = "/pgadmissions/404"; }
		};

$(document).ready(function() {
	$(document).on('change', 'select#programSelect', function() {
		updateScoringDefinition();
	});
	
	$(document).on('change', 'select#stageSelect', function() {
		updateScoringDefinition();
	});
    
    $('#save-scoring').click(function() {
    	$('div.content-box-inner').css({position : 'relative'}).append('<div class="ajax" />');
    	$.ajax({
	        type : 'POST',
		    statusCode : errorCodes,
		    url : "/pgadmissions/configuration/editScoringDefinition",
		    data: {
		    	programCode : $('#programSelect').val(),
		    	scoringStage : $('#stageSelect').val(),
		    	scoringContent : $('#scoringConfigurationContent').val()
	    	},
		    success : function(data) {
		    	$("#scoringConfiguration").parent().find('.alert-error').remove();
		    	if (data.scoringContent != null) {
		    		$("#scoringConfigurationContent").parent().append('<div class="alert alert-error"><i class="icon-warning-sign"></i> ' + data.scoringContent + ' </div>');
				}
		    	if (data.programCode != null) {
		    		$("#programSelect").parent().append('<div class="alert alert-error"><i class="icon-warning-sign"></i> ' + data.programCode + ' </div>');
				}
	   		},
	   		complete : function() {
	   			$('div.ajax').remove();
			}
    	});
    });
    
});

function updateScoringDefinition(){
	if($('#programSelect').val() == ""){
		$('#scoringConfigurationContent').val("");
	} else {
		$.ajax({
	        type : 'GET',
		    statusCode : errorCodes,
		    url : "/pgadmissions/configuration/getScoringDefinition",
		    data: {
		    	programCode : $('#programSelect').val(),
		    	scoringStage : $('#stageSelect').val()
	    	},
		    success : function(data) {
		    	$('#scoringConfigurationContent').val(data);
	   		},
	   		complete : function() {
	   			$('div.ajax').remove();
			}
    	});
	}
}
