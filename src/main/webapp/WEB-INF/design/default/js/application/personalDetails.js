$(document)
		.ready(
				function() {
					if ($("input[name='passportAvailable']:checked").val() == "true") {
						enablePassportInformation();
					} else {
						disablePassportInformation();
					}

					isRequireVisa();

					if ($(
							"input[name='languageQualificationAvailable']:checked")
							.val() == "true") {
						enableLanguageQualifications();
					} else {
						disableLanguageQualifications();
					}

					var selectedType = $('#qualificationType').val();
					if (selectedType === "IELTS_ACADEMIC") {
						$(
								'#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect')
								.show();
						$(
								'#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree')
								.hide();
						$('#overallScoreSelect').val(
								$("#overallScoreFree").val());
						$('#readingScoreSelect').val(
								$("#readingScoreFree").val());
						$('#writingScoreSelect').val(
								$("#writingScoreFree").val());
						$('#speakingScoreSelect').val(
								$("#speakingScoreFree").val());
						$('#listeningScoreSelect').val(
								$("#listeningScoreFree").val());
						$(
								'#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree')
								.val("");
					}

					if (selectedType === "OTHER") {
						enableOtherLanguageQualification();
					} else {
						disableOtherLanguageQualification();
					}

					if ($('#languageQualificationsTable tr').length > 0) {
						disableLanguageQualifications();
					}

					isEnglishFirstLanguage();

					$("#acceptTermsPEDValue").val("NO");

					// -------------------------------------------------------------------------------
					// Close button.
					// -------------------------------------------------------------------------------
					$('#personalDetailsCloseButton').click(function() {
						$('#personalDetails-H2').trigger('click');
						return false;
					});

					// -------------------------------------------------------------------------------
					// Clear button.
					// -------------------------------------------------------------------------------
					$('#personalDetailsClearButton').click(
							function() {
								$('#ajaxloader').show();
								loadPersonalDetails(true);
								
							});

					// -------------------------------------------------------------------------------
					// "Accept terms" checkbox.
					// -------------------------------------------------------------------------------
					$("input[name*='acceptTermsPEDCB']").click(function() {
						if ($("#acceptTermsPEDValue").val() == 'YES') {
							$("#acceptTermsPEDValue").val("NO");
						} else {
							$("#acceptTermsPEDValue").val("YES");
						}
					});

					// -------------------------------------------------------------------------------
					// Require visa
					// -------------------------------------------------------------------------------
					$("input[name='requiresVisa']").bind('change', function() {
						isRequireVisa();
					});

					// -------------------------------------------------------------------------------
					// Passport Available
					// -------------------------------------------------------------------------------
					$("input[name='passportAvailable']")
							.bind(
									'change',
									function() {
										var selected_radio = $(
												"input[name='passportAvailable']:checked")
												.val();
										if (selected_radio == 'true') {
											enablePassportInformation();
										} else {
											disablePassportInformation();
										}
									});

					// -------------------------------------------------------------------------------
					// Is English your first language?*
					// -------------------------------------------------------------------------------
					$("input[name='englishFirstLanguage']").bind('change',
							function() {
								var returnVal = isEnglishFirstLanguage();
								if (returnVal) {
									ajaxDeleteAllLanguageQualifications();
								}
							});

					// -------------------------------------------------------------------------------
					// Language Qualification available
					// -------------------------------------------------------------------------------
					$("input[name='languageQualificationAvailable']")
							.bind(
									'change',
									function() {
										$(
												'#languageQualification_div div.alert')
												.remove();
										var selected_radio = $(
												"input[name='languageQualificationAvailable']:checked")
												.val();
										if (selected_radio == 'true') {
											enableLanguageQualifications();
										} else {
											disableLanguageQualifications();
											ajaxDeleteAllLanguageQualifications();
										}
									});

					// -------------------------------------------------------------------------------
					// Save button.
					// -------------------------------------------------------------------------------
					$('#personalDetailsSaveButton').on("click", function() {
						$("span[name='nonAcceptedPED']").html('');

						// Attempt saving of "dirty" nationalities.
						$('#addCandidateNationalityButton').trigger('click');
						$('#addMaternalNationalityButton').trigger('click');
						$('#addPaternalNationalityButton').trigger('click');

						postPersonalDetailsData('close');
					});

					// To make uncompleted functionalities disabled
					$(".disabledEle").attr("disabled", "disabled");

					// / delete collection items
					$("#existingCandidateNationalities").on(
							"click",
							"a",
							function() {
								$(this).parent("div").remove();

								if ($('#existingCandidateNationalities')
										.children().length <= 1) {
									$('#candidateNationalitiesLabel').remove();
									$('#my-nationality-lb').html(
											"My Nationality");
								}

								if ($('#existingCandidateNationalities')
										.children().length == 1) {

									$('#my-hint').remove();
									$('#my-nationality-hint').show();
								}

							});

					$("#existingMaternalNationalities").on(
							"click",
							"a",
							function() {
								$(this).parent("div").remove();

								if ($('#existingMaternalNationalities')
										.children().length <= 1) {
									$('#maternalNationalitiesLabel').remove();
									$('#maternal-nationality-lb').html(
											"Mother's Nationality");
								}
							});

					$("#existingPaternalNationalities").on(
							"click",
							"a",
							function() {
								$(this).parent("div").remove();

								if ($('#existingPaternalNationalities')
										.children().length <= 1) {
									$('#paternalNationalitiesLabel').remove();
									$('#paternal-nationality-lb').html(
											"Father's Nationality");
								}
							});

					bindDatePicker('#dateOfBirth');
					bindDatePicker('#passportExpiryDate');
					bindDatePicker('#passportIssueDate');
					bindDatePicker('#dateOfExamination');
					addToolTips();
					watchUpload($('#languageQualificationDocument'),
							ajaxLanguageQualificationDocumentDelete);

					// -------------------------------------------------------------------------------
					// Language Qualification Type Change
					// -------------------------------------------------------------------------------
					$('#qualificationType')
							.live(
									'change',
									function() {
										var selectedType = $(
												'#qualificationType').val();
										if (selectedType === "IELTS_ACADEMIC") {
											$(
													'#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect')
													.show();
											$(
													'#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree')
													.hide();
										} else {
											$(
													'#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree')
													.show();
											$(
													'#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect')
													.hide();
										}

										if (selectedType === "OTHER") {
											enableOtherLanguageQualification();
										} else {
											disableOtherLanguageQualification();
										}

										$(
												'#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree')
												.val("");
										$(
												'#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect')
												.val("");
										$("#examTakenOnlineNo").removeAttr(
												"checked");
										$("#examTakenOnlineYes").removeAttr(
												"checked");

										$(
												'#languageQualification_div div.alert')
												.remove();

										$(
												"#otherQualificationTypeName, #dateOfExamination")
												.val("");

										ajaxLanguageQualificationDocumentDelete();
										deleteQualificationDocumentFile();
									});

				});

function ajaxLanguageQualificationDocumentDelete() {
	if ($('#document_LANGUAGE_QUALIFICATION')
			&& $('#document_LANGUAGE_QUALIFICATION').val()
			&& $('#document_LANGUAGE_QUALIFICATION').val() != '') {
		$.ajax({
			type : 'POST',
			statusCode : {
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
			},
			url : "/pgadmissions/update/deleteLanguageQualificationsDocument",
			data : {
				applicationId : $('#applicationId').val(),
				documentId : $('#document_LANGUAGE_QUALIFICATION').val(),
				cacheBreaker : new Date().getTime()
			},
			success : function(data) {
			},
			complete : function() {
			}
		});
		$("#languageQualificationDocument").val("");
	}

}

function ajaxDeleteAllLanguageQualifications() {
	var postData = {
		applicationId : $('#applicationId').val(),
		cacheBreaker : new Date().getTime(),
	};

	if ($('input:radio[name=englishFirstLanguage]:checked').length > 0) {
		postData.englishFirstLanguage = $(
				'input:radio[name=englishFirstLanguage]:checked').val();
	}

	if ($('input:radio[name=languageQualificationAvailable]:checked').length > 0) {
		postData.languageQualificationAvailable = $(
				'input:radio[name=languageQualificationAvailable]:checked')
				.val();
	}

	$.ajax({
		type : 'POST',
		statusCode : {
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
		},
		url : "/pgadmissions/update/deleteAllLanguageQualifications",
		data : postData,
		success : function(data) {
			$('#languageQualification_div').html(data);
			bindDatePicker('#dateOfExamination');
			addToolTips();
		},
		complete : function() {
		}
	});
}

function selectValue(elementName) {
	var selectedType = $('#qualificationType').val();
	if (selectedType === "IELTS_ACADEMIC") {
		return $("select[name='" + elementName + "']")[0].value;
	} else {
		return $("input[name='" + elementName + "']")[0].value;
	}
}

function disablePassportInformation() {
	$("#passportNumber").attr("disabled", "disabled");
	$("#passportNumber").val("");
	$("#lbl_passportNumber").addClass("grey-label").parent().find('.hint')
			.addClass("grey");
	$("#lbl_passportNumber").html("Passport Number");

	$("#nameOnPassport").attr("disabled", "disabled");
	$("#nameOnPassport").val("");
	$("#lbl_nameOnPassport").addClass("grey-label").parent().find('.hint')
			.addClass("grey");
	$("#lbl_nameOnPassport").html("Name on Passport");

	$("#passportIssueDate").attr("disabled", "disabled");
	$("#passportIssueDate").val("");
	$("#lbl_passportIssueDate").addClass("grey-label").parent().find('.hint')
			.addClass("grey");
	$("#lbl_passportIssueDate").html("Passport Issue Date");

	$("#passportExpiryDate").attr("disabled", "disabled");
	$("#passportExpiryDate").val("");
	$("#lbl_passportExpiryDate").addClass("grey-label").parent().find('.hint')
			.addClass("grey");
	$("#lbl_passportExpiryDate").html("Passport Expiry Date");
}

function enablePassportInformation() {
	$("#passportNumber").removeAttr("disabled", "disabled");
	$("#passportNumber").removeAttr("readonly", "readonly");
	$("#lbl_passportNumber").removeClass("grey-label").parent().find('.hint')
			.removeClass("grey");
	$("#lbl_passportNumber").html("Passport Number<em>*</em>");

	$("#nameOnPassport").removeAttr("disabled", "disabled");
	$("#nameOnPassport").removeAttr("readonly", "readonly");
	$("#lbl_nameOnPassport").removeClass("grey-label").parent().find('.hint')
			.removeClass("grey");
	$("#lbl_nameOnPassport").html("Name on Passport<em>*</em>");

	$("#passportIssueDate").removeAttr("disabled", "disabled");
	$("#lbl_passportIssueDate").removeClass("grey-label").parent()
			.find('.hint').removeClass("grey");
	$("#lbl_passportIssueDate").html("Passport Issue Date<em>*</em>");

	$("#passportExpiryDate").removeAttr("disabled", "disabled");
	$("#lbl_passportExpiryDate").removeClass("grey-label").parent().find(
			'.hint').removeClass("grey");
	$("#lbl_passportExpiryDate").html("Passport Expiry Date<em>*</em>");
}

function enableLanguageQualifications() {
	$(
			"#qualificationType, #dateOfExamination, #examTakenOnlineYes, #examTakenOnlineNo, #languageQualificationDocument")
			.removeAttr("disabled", "disabled");
			$('#examTakenOnlineYes, #examTakenOnlineNo').parent().removeClass("grey-label");
	$(
			"#qualificationType, #examTakenOnlineYes, #examTakenOnlineNo, #languageQualificationDocument")
			.removeAttr("readonly", "readonly");
	
	$(
			'#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect')
			.removeAttr("disabled", "disabled");
	$(
			'#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect')
			.removeAttr("readonly", "readonly");
	$(
			'#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree')
			.removeAttr("disabled", "disabled");
	$(
			'#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree')
			.removeAttr("readonly", "readonly");

	$(
			"#lbl-qualificationType, #lbl-dateOfExamination, #lbl-overallScore, #lbl-readingScore, #lbl-writingScore, #lbl-speakingScore, #lbl-listeningScore, #lbl-examTakenOnline, #lbl-languageQualificationDocument, #lbl-englishLanguageQualifications")
			.removeClass("grey-label");
	$(
			"#lbl-qualificationType, #lbl-dateOfExamination, #lbl-overallScore, #lbl-readingScore, #lbl-writingScore, #lbl-speakingScore, #lbl-listeningScore, #lbl-examTakenOnline, #lbl-languageQualificationDocument, #lbl-englishLanguageQualifications")
			.parent().find('.hint').removeClass("grey");

	if ($("#languageQualificationsTable tr").length <= 0) {
		$("#addLanguageQualificationButton").show();
	}
	$("#languageQualification_div").find(".disabled").removeClass("disabled");
	
	$("#updateLanguageQualificationButton").hide();
}

function disableLanguageQualifications() {
	$(
			"#qualificationType, #dateOfExamination, #examTakenOnlineYes, #examTakenOnlineNo, #languageQualificationDocument")
			.attr("disabled", "disabled");
			$('#examTakenOnlineYes, #examTakenOnlineNo').parent().addClass("grey-label");
	$(
			"#qualificationType, #dateOfExamination, #examTakenOnlineYes, #examTakenOnlineNo, #languageQualificationDocument")
			.attr("readonly", "readonly");

	$(
			'#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect')
			.attr("disabled", "disabled");
	$(
			'#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect')
			.attr("readonly", "readonly");

	$(
			'#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree')
			.attr("disabled", "disabled");
	$(
			'#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree')
			.attr("readonly", "readonly");

	$(
			"#lbl-qualificationType, #lbl-dateOfExamination, #lbl-overallScore, #lbl-readingScore, #lbl-writingScore, #lbl-speakingScore, #lbl-listeningScore, #lbl-examTakenOnline, #lbl-languageQualificationDocument, #lbl-englishLanguageQualifications")
			.addClass("grey-label");
	$(
			"#lbl-qualificationType, #lbl-dateOfExamination, #lbl-overallScore, #lbl-readingScore, #lbl-writingScore, #lbl-speakingScore, #lbl-listeningScore, #lbl-examTakenOnline, #lbl-languageQualificationDocument, #lbl-englishLanguageQualifications")
			.parent().find('.hint').addClass("grey");

	$("#addLanguageQualificationButton").hide();
	$("#updateLanguageQualificationButton").hide();
	
	
	
	clearLanguageQualification();
}

function clearLanguageQualification() {
	$("#qualificationType, #otherQualificationTypeName, #dateOfExamination")
			.val("");
	$("#examTakenOnlineNo").removeAttr("checked");
	$("#examTakenOnlineYes").removeAttr("checked");

	$(
			'#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree')
			.val("");
	$(
			'#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect')
			.val("");

	$(
			'#overallScoreFree, #readingScoreFree, #writingScoreFree, #speakingScoreFree, #listeningScoreFree')
			.show();
	$(
			'#overallScoreSelect, #readingScoreSelect, #writingScoreSelect, #speakingScoreSelect, #listeningScoreSelect')
			.hide();

	ajaxLanguageQualificationDocumentDelete();
	$("#languageQualificationDocument").val();
}

function disableOtherLanguageQualification() {
	$('#otherQualificationTypeName').attr("disabled", "disabled");
	$('#otherQualificationTypeName').attr("readonly", "readonly");
	$('#lbl-otherQualificationTypeName').addClass("grey-label").parent().find(
			'.hint').addClass("grey");
}

function enableOtherLanguageQualification() {
	$('#otherQualificationTypeName').removeAttr("disabled");
	$('#otherQualificationTypeName').removeAttr("readonly");
	$('#lbl-otherQualificationTypeName').removeClass("grey-label").parent()
			.find('.hint').removeClass("grey");
}

function isEnglishFirstLanguage() {
	var selected_radio = $("input[name='englishFirstLanguage']:checked").val();
	if (selected_radio == 'true') {
		$("#lbl-languageQualificationAvailable").addClass("grey-label")
				.parent().find('.hint').addClass("grey");
		$("#lbl-languageQualificationAvailable").parent().find('label').addClass("grey-label");
		$("input[name='languageQualificationAvailable']").attr("disabled",
				"disabled");
		$("input[name='languageQualificationAvailable']")
				.prop('checked', false);
		disableLanguageQualifications();
		disableOtherLanguageQualification();
		$("#languageQualificationsTable").empty();
		return true;
	} else if (selected_radio == 'false') {
		$("#lbl-languageQualificationAvailable").removeClass("grey-label")
				.parent().find('.hint').removeClass("grey");
		$("#lbl-languageQualificationAvailable").parent().find('label').removeClass("grey-label");
		$("input[name='languageQualificationAvailable']")
				.removeAttr("disabled");
		return false;
	}
	return false;
}

function isRequireVisa() {
	var selected_radio = $("input[name='requiresVisa']:checked").val();
	if (selected_radio == 'true') {
		$("#lbl-passportAvailable").removeClass("grey-label").parent().find(
				'.hint').removeClass("grey");
		$("#lbl-passportAvailable").parent().find('label').removeClass("grey-label");
		$("input[name='passportAvailable']").removeAttr("disabled", "disabled");
	} else {
		$("#lbl-passportAvailable").addClass("grey-label").parent().find(
				'.hint').addClass("grey");
		$("#lbl-passportAvailable").parent().find('label').addClass("grey-label");
		$("input[name='passportAvailable']").attr("disabled", "disabled");
		$("input[name='passportAvailable']").prop('checked', false);
		disablePassportInformation();
	}
}

function postPersonalDetailsData(message) {
	
	var first_nationality = $('#primaryNationality').val();
	var second_nationality  = $('#secondaryNationality').val();
	
	var acceptedTheTerms;
	if ($("#acceptTermsPEDValue").val() == 'NO') {
		acceptedTheTerms = false;
	} else {
		acceptedTheTerms = true;
	}

	var examTakenOnline = "";
	if ($("input[name='examTakenOnline']:checked").length > 0) {
		examTakenOnline = $("input[name='examTakenOnline']:checked").val();
	}
	var documentLanguageQualification;
	if ( $('#document_LANGUAGE_QUALIFICATION').length == 0) {
		documentLanguageQualification = '';	
	}  else {
		documentLanguageQualification = $('#document_LANGUAGE_QUALIFICATION').val()
	}
	var postData = {
		title : $("#title").val(),
		firstName : $("#firstName").val(),
		firstName2 : $("#firstName2").val(),
		firstName3 : $("#firstName3").val(),
		lastName : $("#lastName").val(),
		country : $("#country").val(),
		dateOfBirth : $("#dateOfBirth").val(),
		residenceCountry : $("#residenceCountry").val(),
		messenger : $("#pd_messenger").val(),
		phoneNumber : $('#pd_telephone').val(),
		application : $('#applicationId').val(),
		applicationId : $('#applicationId').val(),
		firstNationality : first_nationality,
		secondNationality : second_nationality,
		ethnicity : $("#ethnicity").val(),
		disability : $("#disability").val(),
		'passportInformation.passportNumber' : $("#passportNumber").val(),
		'passportInformation.nameOnPassport' : $("#nameOnPassport").val(),
		'passportInformation.passportIssueDate' : $("#passportIssueDate").val(),
		'passportInformation.passportExpiryDate' : $("#passportExpiryDate")
				.val(),
		'languageQualifications[0].languageQualificationId' : $(
				'#languageQualificationId').val(),
		'languageQualifications[0].qualificationType' : $('#qualificationType')
				.val(),
		'languageQualifications[0].otherQualificationTypeName' : $(
				'#otherQualificationTypeName').val(),
		'languageQualifications[0].dateOfExamination' : $('#dateOfExamination')
				.val(),
		'languageQualifications[0].overallScore' : selectValue('overallScore'),
		'languageQualifications[0].readingScore' : selectValue('readingScore'),
		'languageQualifications[0].writingScore' : selectValue('writingScore'),
		'languageQualifications[0].speakingScore' : selectValue('speakingScore'),
		'languageQualifications[0].listeningScore' : selectValue('listeningScore'),
		'languageQualifications[0].examTakenOnline' : examTakenOnline,
		'languageQualifications[0].languageQualificationDocument' : documentLanguageQualification,
		message : message,
		acceptedTerms : acceptedTheTerms,
		cacheBreaker : new Date().getTime()
	};

	if ($('input:radio[name=englishFirstLanguage]:checked').length > 0) {
		postData.englishFirstLanguage = $(
				'input:radio[name=englishFirstLanguage]:checked').val();
	}

	if ($('input:radio[name=requiresVisa]:checked').length > 0) {
		postData.requiresVisa = $('input:radio[name=requiresVisa]:checked')
				.val();
	}

	if ($('input:radio[name=languageQualificationAvailable]:checked').length > 0) {
		postData.languageQualificationAvailable = $(
				'input:radio[name=languageQualificationAvailable]:checked')
				.val();
	}

	if ($('input:radio[name=passportAvailable]:checked').length > 0) {
		postData.passportAvailable = $(
				'input:radio[name=passportAvailable]:checked').val();
	}

	var gender = $("input[name='genderRadio']:checked").val();
	if (gender) {
		postData.gender = gender;
	}

	// do the post!
	$('#ajaxloader').show();

	$
			.ajax({
				type : 'POST',
				statusCode : {
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
				},
				url : "/pgadmissions/update/editPersonalDetails",
				data : $.param(postData),
				cacheBreaker : new Date().getTime(),
				success : function(data) {
					$('#personalDetailsSection').html(data);

					if (message == 'close') {
						// Close the section only if there are no errors.
						var errorCount = $('#personalDetailsSection div.alert-error').length;
						
						if (errorCount > 0) {
							$('#personalDetails-H2').trigger('click');
							markSectionError('#personalDetailsSection');
						} 
					}
				},
				complete : function() {
					$('#ajaxloader').fadeOut('fast');
				}
			});
}

function deleteQualificationDocumentFile() {
	
	var $container = $('#languageQualificationDocument').closest('div.field');
	var $hidden = $container.find('.file');
	var $uploadedDocuments = $container.find('ul.uploaded-files');

	$uploadedDocuments.find('li:last-child').remove();
	$uploadedDocuments.hide();
	
	//Display uploader back
	$container.find('.fileupload').fileupload('clear').show();

	$hidden.val(''); // clear field value.
	$container.removeClass('uploaded');
}
