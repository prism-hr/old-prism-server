var loading = false;
var latestConsideredFlagIndex = 0;

$(document).ready(function() {
	var searchPredicatesMap = JSON.parse($("#searchPredicatesMap").val());

	// Modal window functionality.
	setupModalBox();
	checkSwitch();

	$('#search-box').find('.date').each(function() {
		bindDatePicker($(this));
	});

	$.fn.jExpand = function() {
		var element = this;

		$(element).find('.application-details:odd').addClass('odd').addClass('loading');

		$(element).find('.applicationRow').not('.applicationRow.binded').bind('click', function(event) {

			var applicationDetails = $(this).next();

			if (applicationDetails.attr('data-application-status') == 'UNSUBMITTED' || applicationDetails.attr('data-application-status') == 'WITHDRAWN') {
				return;
			}

			// Load data if not already.
			if (applicationDetails.attr('data-loaded') != 'true') {
				applicationDetails.attr('data-loaded', 'true');
				$(element).find('.application-lhs').addClass('loading');

				$.ajax({
					type : 'GET',
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
					url : "/pgadmissions/getApplicationDetails",
					data : {
						applicationId : applicationDetails.attr('data-application-id')
					},
					success : function(data) {

						var applicant = JSON.parse(data.applicant);

						if (data.requiresAttention == 'true') {
							applicationDetails.find('[data-field=message]').text('This application requires your attention');
						}

						applicationDetails.find('[data-field=applicant-name]').text(applicant.name);
						applicationDetails.find('[data-field=submitted-date]').text(data.applicationSubmissionDate);
						applicationDetails.find('[data-field=last-edited-date]').text(data.applicationUpdateDate);

						applicationDetails.find('[data-field=most-recent-qualification]').text(applicant.mostRecentQualification);

						applicationDetails.find('[data-field=most-recent-employment]').text(applicant.mostRecentEmployment);

						applicationDetails.find('[data-field=funding-requirements] b').text("£" + applicant.fundingRequirements);

						applicationDetails.find('[data-field=references-responded] b').text(data.numberOfReferences);

						applicationDetails.find('[data-field=personal-statement-link]').text("Personal Statement");
						applicationDetails.find('[data-field=personal-statement-link]').attr('href', '/pgadmissions/download?documentId=' + data.personalStatementId);

						if (data.cvProvided == 'true') {
							applicationDetails.find('[data-field=cv-statement-link]').text("CV / resume");
							applicationDetails.find('[data-field=cv-statement-link]').attr('href', '/pgadmissions/download?documentId=' + data.cvId);
						}

						applicationDetails.find('[data-field=email]').text(applicant.email);
						applicationDetails.find('[data-field=email]').attr('href', 'mailto:' + applicant.email + "?subject=Question Regarding UCL Prism Application " + data.applicationNumber);
						applicationDetails.find('[data-field=phone-number] span').text(applicant.phoneNumber);
						applicationDetails.find('[data-field=skype] span').text(applicant.skype);

						applicationDetails.find('[data-field=application-status]').text(data.applicationStatus);

					},
					complete : function() {
						$(element).find('.loading').removeClass('loading');
					}
				});
			}
			if ($(event.target).attr('class') == 'btn dropdown-toggle selectpicker btn-default' || $(event.target).attr('class') == 'filter-option pull-left' || $(event.target).attr('class') == 'text' || $(event.target).attr('class') == 'caret' || $(event.target).attr('class') == '' || $(event.target).attr('type') == 'checkbox') {
				// do nothing
			} else {
				$(element).find('.application-details').not(applicationDetails).hide();
				applicationDetails.toggle();
			}

		});
		$(element).find('.applicationRow').addClass('binded');
	};

	populateApplicationList();

	// --------------------------------------------------------------------------------
	// TABLE SORTING
	// --------------------------------------------------------------------------------
	// Add a SPAN tag to table headers for the arrows.
	$('table.data thead th.sortable').prepend('<span />');
	updateSortHeaders();

	// --------------------------------------------------------------------------------
	// SEARCH / FILTERING
	// --------------------------------------------------------------------------------
	$('#search-go').addClass('enabled').click(function() {
		resetPageCount();
		populateApplicationList();
	});

	$('#storeFiltersBtn').click(function() {
		filters = getFilters();

		data = {
			filters : JSON.stringify(filters),
			useDisjunction : $('#useDisjunctionId').is(':checked')
		};

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
			url : "/pgadmissions/applications/saveFilters",
			data : data,
			success : function(data) {
			},
			complete : function() {
			}
		});
		cleanUpFilterIds();
	});

	// ------------------------------------------------------------------------------
	// SELECTION CHANGED FOR SELECT CATEGORY
	// ------------------------------------------------------------------------------
	$('.selectCategory').on("change", function() {
		var selected = $(this).val();
		var predicateSelect = $(this).parent().find('.selectPredicate');
		predicateSelect.empty();

		if (selected != "") {
			var predicates = searchPredicatesMap[selected];
			for (var i = 0; i < predicates.length; i++) {
				predicateSelect.append('<option value="' + predicates[i].name + '">' + predicates[i].displayName + '</option>');
			}
		}
		fieldChange(selected, $(this));
	});

	// ------------------------------------------------------------------------------
	// SELECT ALL/NO APPLICATIONS
	// ------------------------------------------------------------------------------
	$('#select-all').click(function(event) {
		var selectAllValue = this.checked;
		var $appList = $('#appList');
		var list = '';

		$('#applicationListSection input:checkbox').each(function() {
			this.checked = selectAllValue;
			if (selectAllValue) {
				list += $(this).val() + ";";
			}
		});

		$appList.val(list);
	});

	// ------------------------------------------------------------------------------
	// CLEAR ALL FILTERS
	// ------------------------------------------------------------------------------
	$("#search-reset").on('click', function() {
		clearCurrentFilters(true);
		checkSwitch();
	});

	// --------------------------------------------------------------------------------
	// SELECT APPLICATIONS
	// --------------------------------------------------------------------------------
	$(document).on('click', "input[name*='appDownload']", function() {
		var id = $(this).val();
		var currentAppList = $('#appList').val();

		if ($(this).is(':checked')) {
			$('#appList').val(currentAppList + id + ";");
		} else {
			$('#appList').val(currentAppList.replace(id + ";", ''));
		}
	});
	
	$('#search-report').click(downloadReport);
	$('#search-report-html').click({
		outputType : "html"
	}, downloadReport);
	$('#search-report-json').click({
		outputType : "json"
	}, downloadReport);
	$('#search-report-csv').click({
		outputType : "csv",
		reportType : "standard"
	}, downloadReport);
	$('#search-report-summary').click({
		outputType : "csv",
		reportType : "summary"
	}, downloadReport);

	// --------------------------------------------------------------------------------
	// DOWNLOAD SELECTED APPLICATIONS
	// --------------------------------------------------------------------------------
	$('#downloadAll').click(function() {
		var appListValue = '';
		$('input[name=appDownload]').each(function() {
			if (this.checked) {
				appListValue = appListValue + $(this).val() + ";";
			}
		});

		if (appListValue != '') {
			window.open("/pgadmissions/print/all?appList=" + appListValue, '_blank');
		}
	});

	$("#loadMoreApplications").on('click', function() {
		if (loading) {
			return;
		}
		increasePageCount();
		populateApplicationList();
	});

	// Duplicate filters buttons
	$(".add").on('click', function() {
		var existingFilter = $(this).parent();
		var newFilter = $(existingFilter).clone(true);
		newFilter.insertAfter($(this).parent());
		inputBackNormal(newFilter);
		clearFilter(newFilter);
		if (existingFilter.find(".filterInput").val() != "") {
			$('#search-go').click();
		}
		cleanUpFilterIds();
		checkSwitch();
	});

	// Remove current filter
	$(".remove").on('click', function() {
		var existingFilter = $(this).parent();
		if ($("#search-box").find("div.filter").length > 1) {
			var filterValue = existingFilter.find(".filterInput").val();
			existingFilter.remove();
			if (filterValue != "") {
				$('#search-go').click();
			}
		}
		checkSwitch();
	});

	$('#operatorSwitch').on('switch-change', function() {
		setTimeout(function() {
			$('#search-go').click();
		}, 800);
	});
});
/* Check if user submit the application to reveal the share pannel */
function shareApplicationPannel() {
    if( $("#content div.alert-info:contains('Thank you for your application')").length > 0) {
       var application = getURLParameter('application').split('-')[0];
       getAdvertData(application);
    }
}
function getURLParameter(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null;
}
function getAdvertData(programme_code) {
    $.ajax({
        type : 'GET',
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
        url : "/pgadmissions/prospectus/programme/getAdvertData",
        data : {
            programCode : programme_code,
        },
        success : function(data) {
            var map = JSON.parse(data);
            updateAdvertSection(map, programme_code);
        },
        complete : function() {
        }
    });
}
function updateAdvertSection(map, application) {
    var linkToApply = map['linkToApply'];
    var titleSeleted = $('.applicant-name :contains('+application+')').parent().parent().find('td.program-title').text();
    var sharethisvar = '//api.addthis.com/oexchange/0.8/offer?url=' + linkToApply + '&title=' + titleSeleted;

    $("#programAdvertLinkToApply").val(linkToApply);
    $("#modalLinkToApply").val(linkToApply);

    $('#sharethis').prop("href", sharethisvar);
    $('#resourcesModal').modal('show');

    $('.alert.alert-info').clone().prependTo('#resourcesModal .modal-body').find('a').remove();

}

function checkSwitch() {
	if ($('.filter').length > 1) {
		$('#prefilterBox').show();
	} else {
		$('#prefilterBox').hide();
	}
}

// ------------------------------------------------------------------------------
// APPLY/REMOVE DATAPICKER AND SELECTOR
// ------------------------------------------------------------------------------
// input back to normal state
function inputBackNormal(mainInput) {
	var $inputSelected = mainInput.find('.filterInput');

	if ($inputSelected.is('input')) {
		// Clear if input type
		$inputSelected.datepicker("destroy").removeClass('half date hasDatepicker').removeAttr('readonly').val('');
	} else {
		// Replace field if select
		idSwitch = $inputSelected.attr('id');
		$inputSelected.remove();
		$('<input type=\"text\" value=\"\" name=\"searchTerm\" placeholder=\"Filter by...\" class=\"filterInput\" style=\"margin-left: 3px;\" />').insertAfter(mainInput.find('.selectPredicate')).attr('id', idSwitch);
	}
}
// field changer
function fieldChange(selected, id) {
	inputBackNormal(id.parent());
	if (selected == "APPLICATION_STATUS") {
		// Create select
		var myoptions = $("#applicationStatusValues").val();
		var data = myoptions.split(',');
		var selector = $("<select name=\"filterInput\"  style=\"margin-left: 3px;\" class=\"filterInput selector\" />");
		for ( var val in data) {
			$("<option />", {
				value : data[val],
				text : data[val]
			}).appendTo(selector);
		}
		idSwitch = id.parent().find('.filterInput').attr('id');
		$("input#" + idSwitch).remove();

		$(selector).insertAfter(id.parent().find('.selectPredicate')).attr('id', idSwitch);

	} else if (selected == "LAST_EDITED_DATE" || selected == "SUBMISSION_DATE" || selected == "CLOSING_DATE") {
		// Find input and add classes
		id.parent().find('.filterInput').addClass('half date').val('');
		// bind datapicker
		bindDatePicker(id.parent().find('.filterInput'));
	}
}
function resetPageCount() {
	$('#block-index').val("1");
}

function getPageCount() {
	return parseInt($('#block-index').val());
}

function increasePageCount() {
	var blockIndex = parseInt($('#block-index').val());
	blockIndex += 1;
	$('#block-index').val(blockIndex.toString());
}

function populateApplicationList() {

	loading = true;

	$('#ajaxloader').show();

	filters = getFilters();

	options = {
		useDisjunction : $('#useDisjunctionId').is(':checked'),
		filters : JSON.stringify(filters),
		preFilter : $("#preFilter").val(),
		sortCategory : $('#sort-column').val(),
		order : $('#sort-order').val(),
		blockCount : $('#block-index').val(),
		latestConsideredFlagIndex : latestConsideredFlagIndex
	};

	$('#search-box div.alert-error').remove();

	$('#loadMoreApplicationsTable').show();

	$.ajax({
		type : 'GET',
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
			},
		},
		url : "/pgadmissions/applications/section",
		data : options,
		success : function(data) {
			updateSortHeaders();
			if (getPageCount() === 1) {
				$('#applicationListSection').empty();
			}

			$('#applicationListSection').append(data);

			// Select converted to bootstrap dropdown//
			$('select.selectpicker').each(function() {
				if ($(this).is(':visible')) {
					$(this).selectpicker();
				}
				;
			});

			$('#appliList').jExpand();

		},
		complete : function() {
			$('#ajaxloader').fadeOut('fast');
			shareApplicationPannel();
			addToolTips();
			loading = false;
		}
	});
}

function downloadReport(event) {
	filters = getFilters();

	options = {
		filters : JSON.stringify(filters),
		sortCategory : $('#sort-column').val(),
		order : $('#sort-order').val(),
		blockCount : $('#block-index').val(),
		latestConsideredFlagIndex : latestConsideredFlagIndex
		};
		
	var url = "/pgadmissions/applications/report?";
	url += "filters=" + JSON.stringify(filters);
	url += "&sortCategory=" + $('#sort-column').val();
	url += "&order=" + $('#sort-order').val();
	if (event.data && event.data.outputType) {
		url += "&tqx=out:" + event.data.outputType;
	} 
	if (event.data.reportType == 'summary'){
		url += "&reportType=SHORT";
		
	}
	else {
		url += "&reportType=STANDARD";
		
	}


	var win = window.open(url, '_blank');
	win.focus();
}

function sortList(column) {
	oldValue = $('#sort-column').val();
	newValue = column.id;

	if (oldValue == newValue) {
		flipSortOrder();
	} else {
		$('#sort-column').val(newValue);
		$('#sort-order').val("ASCENDING");
	}

	// Highlight the sorting column.
	$('table th').removeClass('sorting asc desc');
	var $header = $('#' + newValue);
	$header.addClass('sorting');
	if ($('#sort-order').val() == "DESCENDING") {
		$header.addClass('desc');
	} else {
		$header.addClass('asc');
	}

	resetPageCount();
	populateApplicationList();
}

function flipSortOrder() {
	oldOrder = $('#sort-order').val();
	if (oldOrder == "ASCENDING") {
		$('#sort-order').val("DESCENDING");
	} else {
		$('#sort-order').val("ASCENDING");
	}
}

function updateSortHeaders() {
	var sortCategory = $('#sort-column').val();
	var sortOrder = $('#sort-order').val();
	$('table th').removeClass('sorting asc desc');
	$('table.data thead th#' + sortCategory).addClass('sorting').addClass(sortOrder == 'ASCENDING' ? 'asc' : 'desc');
}

function getFilters() {
	var filters = new Array();

	$filterbox = $(".filter");

	$.each($filterbox, function() {
		var search_category = $(this).find('.selectCategory').val();
		var search_predicate = $(this).find('.selectPredicate').val();
		var search_term = $(this).find('.filterInput').val();
		if (search_term == undefined) {
			search_term = "";
		}

		if (search_category && search_term.length > 0) {
			filters.push({
				searchCategory : search_category,
				searchPredicate : search_predicate,
				searchTerm : search_term,
			});
		}
	});
	checkSwitch();
	return filters;
}

function getActiveApplicationFilters() {
	// active applications include applications which are NOT approved,
	// rejected or withdrawn
	var activeApplicationFilters = new Array();
	activeApplicationFilters.push({
		searchCategory : "APPLICATION_STATUS",
		searchPredicate : "NOT_CONTAINING",
		searchTerm : "Approved"
	});
	activeApplicationFilters.push({
		searchCategory : "APPLICATION_STATUS",
		searchPredicate : "NOT_CONTAINING",
		searchTerm : "Rejected"
	});
	activeApplicationFilters.push({
		searchCategory : "APPLICATION_STATUS",
		searchPredicate : "NOT_CONTAINING",
		searchTerm : "Withdrawn"
	});

	return activeApplicationFilters;
}

function clearCurrentFilters(shouldApplyChanges) {
	$('#sort-column').val('APPLICATION_DATE');
	$('#sort-order').val('DESCENDING');
	var filters = $("#search-box").find("div.filter");
	for (var i = 1; i < filters.length; i++) {
		$(filters[i]).remove();
	}

	clearFilter(filters[0]);
	if (shouldApplyChanges) {
		$('#search-go').click();
	}
}

function clearFilter(filter) {
	$(filter).find(".selectCategory").val("");
	$(filter).find(".selectPredicate").empty();
	$(filter).find(".filterInput").val('');
	inputBackNormal($(filter));
	checkSwitch();
}

function cleanUpFilterIds() {
	var filters = $(".filter");
	for (var i = 0; i < filters.length; i++) {
		$(filters[i]).attr("id", "filter_" + i);
		$(filters[i]).find(".selectCategory").attr("id", "searchCategory_" + i);
		$(filters[i]).find(".selectPredicate").attr("id", "searchPredicate_" + i);
		$(filters[i]).find(".filterInput").attr("id", "searchTerm_" + i);
	}
}