var errorCodes = {
	401 : function() {
		window.location.reload();
	},
	500 : function() {
		window.location.href = "/pgadmissions/error";
	},
	404 : function() {
		window.location.href = "/pgadmissions/404";
	},
	400 : function() {
		window.location.href = "/pgadmissions/400";
	},
	403 : function() {
		window.location.href = "/pgadmissions/404";
	}
};

$(document)
		.ready(
				function() {
					$(document).on('change', 'select#programSelect',
							function() {
								updateScoringDefinition();
							});

					$(document).on('change', 'select#stageSelect', function() {
						updateScoringDefinition();
					});
					
					$('#scoringConfigurationContent').bind("propertychange keyup input paste", function() {
						enableOrDisablePreviewButton();
					});

					enableOrDisablePreviewButton();
					
					$('#scoring-config-preview').click(function (){
						$.ajax({
							type : 'POST',
							statusCode : errorCodes,
							url : "/pgadmissions/configuration/previewScoringDefinition",
							data : {
								programCode : $('#programSelect').val(),
								scoringStage : $('#stageSelect').val(),
								scoringContent : $('#scoringConfigurationContent').val()
							},
							success : function(data) {
								$("#scoringConfiguration").parent().find('.alert-error').remove();
								$('#previewModal')
										.modal('show')
										.addClass('large')
										.on('hidden', function () {
											$(this).removeClass('large');
										});
								var stage=$(stageSelect).find(":selected").text().trim();
								$('#previewModalLabel').html('Programme Specific Questions ('+stage+')');
								$('#previewModalContent').html(data);
								$('#previewModalContent').append('<div class="buttons"><button class="btn btn-primary" id="fake-submit" type="button">Submit</button></div>');
								applyTooltip('.hint');
								bindFakeSubmitButtonHandler();
							},
							error: function (errorMsg) {
								removeOldErrorMessage();
								showValidationErrorMessages($(errorMsg.responseText).find('u').first().text());
							}
						});
					});
					
					$('#save-scoring').click(
						function() {
							$('#ajaxloader').show();
							
							$.ajax({
								type : 'POST',
								statusCode : errorCodes,
								url : "/pgadmissions/configuration/editScoringDefinition",
								data : {
									programCode : $('#programSelect').val(),
									scoringStage : $('#stageSelect').val(),
									scoringContent : $('#scoringConfigurationContent')
											.val()
								},
								success : function(data) {
									removeOldErrorMessage();
									if (data.scoringContent != null) {
										showValidationErrorMessages(data.scoringContent);
									}
									if (data.programCode != null) {
										$("#programSelect").parent().append(
										'<div class="alert alert-error"><i class="icon-warning-sign"></i> '
												+ data.programCode
												+ ' </div>');
									}
								},
								complete : function() {
									$('#ajaxloader').fadeOut('fast');
								}
							});
						});
				});

function removeOldErrorMessage(){
	$("#scoringConfiguration").parent().find('.alert-error').remove();
}

function showValidationErrorMessages(message){
	$("#scoringConfigurationContent").parent().append(
			'<div class="alert alert-error"><i class="icon-warning-sign"></i><b>There is an error in your XML: </b>'
					+ message
					+ ' </div>');
}

function bindFakeSubmitButtonHandler(){
	$('#fake-submit').click(function(){		
		$.ajax({
			type : 'POST',
			statusCode : errorCodes,
			url : "/pgadmissions/configuration/fakeSubmitScores",
			data : {
				scores : getScores($('#previewModalContent').find('.scoring-questions')),
				scoringContent:$('#scoringConfigurationContent').val()
			},
			success : function(data) {
				$('#previewModalContent').html(data);
				$('#previewModalContent').append('<div class="buttons"><button class="btn btn-primary" id="fake-submit" type="button" value="Submit">Submit</button></div>');
				bindFakeSubmitButtonHandler();
			}
		});
	});
}

function updateScoringDefinition() {
	if ($('#programSelect').val() == "") {
		$('#scoringConfigurationContent').val("");
	} else {
		$.ajax({
			type : 'GET',
			statusCode : errorCodes,
			url : "/pgadmissions/configuration/getScoringDefinition",
			data : {
				programCode : $('#programSelect').val(),
				scoringStage : $('#stageSelect').val()
			},
			success : function(data) {
				$('#scoringConfigurationContent').val(data);
				if(data!=""){
					enablePreviewButton();
				}else{
					disablePreviewButton();
				}
			},
			complete : function() {
				$('#ajaxloader').fadeOut('fast');
			}
		});
	}
}

function enableOrDisablePreviewButton() {
	if ($('#scoringConfigurationContent').val() == "") {
		disablePreviewButton();
	} else {
		enablePreviewButton();
	}
}

function enablePreviewButton(){
	$('#scoring-config-preview').unbind('click', false);
	$('#scoring-config-preview').attr('disabled', false);
}

function disablePreviewButton(){
	$('#scoring-config-preview').bind('click', false);
	$('#scoring-config-preview').attr('disabled', true);
}
