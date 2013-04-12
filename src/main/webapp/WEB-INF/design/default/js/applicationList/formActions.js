var loading = false;

$(document).ready(function() {
	var searchPredicatesMap = JSON.parse($(
			"#searchPredicatesMap").val());

	// Modal window functionality.
	setupModalBox();

	populateApplicationList();

	// --------------------------------------------------------------------------------
	// TABLE SORTING
	// --------------------------------------------------------------------------------
	// Add a SPAN tag to table headers for the arrows.
	$('table.data thead th.sortable').prepend('<span />');

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
		};

		$.ajax({
			type : 'POST',
			statusCode : {
				401 : function() {
					window.location
							.reload();
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
	$('.selectCategory').live("change",	function() {
		var selected = $(this).val();
		var predicateSelect = $(this).parent()
				.find('.selectPredicate');
		predicateSelect.empty();
		
		if (selected != "") {
			var predicates = searchPredicatesMap[selected];
			for ( var i = 0; i < predicates.length; i++) {
				predicateSelect
						.append('<option value="'
								+ predicates[i].name
								+ '">'
								+ predicates[i].displayName
								+ '</option>');
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

		$('#applicationListSection input:checkbox')
				.each(function() {
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
	$("#search-reset").live('click', function() {
		clearCurrentFilters(true);
	});

	// --------------------------------------------------------------------------------
	// SELECT APPLICATIONS
	// --------------------------------------------------------------------------------
	$(document).on('click', "input[name*='appDownload']", function() {
		var id = $(this).val();
		var currentAppList = $('#appList').val();

		if ($(this).is(':checked')) {
			$('#appList')
					.val(currentAppList + id + ";");
		} else {
			$('#appList').val(
					currentAppList
							.replace(id + ";", ''));
		}
	});

	// --------------------------------------------------------------------------------
	// DOWNLOAD SELECTED APPLICATIONS
	// --------------------------------------------------------------------------------
	$('#downloadAll').click(function() {
		var appListValue = $('#appList').val();
		if (appListValue != '') {
			window.open(
					"/pgadmissions/print/all?appList="
							+ $('#appList').val(),
					'_blank');
		}
	});

	$("#loadMoreApplications").live('click', function() {
		if (loading) {
			return;
		}
		increasePageCount();
		populateApplicationList();
	});

	// Load active applications
	$("#loadActiveApplication").live('click', function() {
		clearCurrentFilters(false);
		var activeApplications = getActiveApplicationFilters();
		var firstFilter = $(".filter").first();

		for ( var i = 0; i < activeApplications.length; i++) {
			var newFilter = $(firstFilter)
					.clone();
			$(newFilter).insertBefore(
					$(firstFilter));

			var selectCategory=$(newFilter).find(".selectCategory");
			$(selectCategory)
					.val(activeApplications[i].searchCategory);
			$(selectCategory).change();
			
			$(newFilter)
					.find(".selectPredicate")
					.val(activeApplications[i].searchPredicate);
			$(newFilter)
					.find(".filterInput")
					.val(activeApplications[i].searchTerm);
			$('#search-go').click();
		}
		
		$(firstFilter).remove();
		cleanUpFilterIds();
	});

	// To be extended
	// Duplicate filters buttons
	$(".add").live('click', function() {
		var existingFilter=$(this).parent();
		var newFilter=$(existingFilter).clone(true);
		newFilter.insertAfter($(this).parent());
		inputBackNormal(newFilter);
		clearFilter(newFilter);
		if(existingFilter.find(".filterInput").val()!=""){
			$('#search-go').click();
		}
		cleanUpFilterIds();
	});
	
	// Remover current filter
	$(".remove").live('click', function() {
		if ($("#search-box").find("div.filter").length > 1) {
			$(this).parent().remove();
			if(existingFilter.find(".filterInput").val()!=""){
				$('#search-go').click();
			}
		}
	});

});
// ------------------------------------------------------------------------------
// APPLY/REMOVE DATAPICKER AND SELECTOR
// ------------------------------------------------------------------------------
// input back to normal state
function inputBackNormal(mainInput) {
	var $inputSelected = mainInput.find('.filterInput');
	
	if ($inputSelected.is('input')) {
	// Clear if input type
	$inputSelected.datepicker("destroy")
		   .removeClass('half date hasDatepicker')
		   .removeAttr('readonly').val('');
	} else {
	// Replace field if select
		idswich = $inputSelected.attr('id');
		$inputSelected.remove();
		$('<input type=\"text\" value=\"\" name=\"searchTerm\" placeholder=\"Filter by...\" class=\"filterInput\" style=\"margin-left: 3px;\" />').insertAfter(mainInput.find('.selectPredicate')).attr('id',idswich);
	}
}
//field changer
function fieldChange(selected, id) {
	if (selected == "APPLICATION_STATUS") {
		inputBackNormal(id.parent());
		// Create select                                        
		var myoptions = $("#applicationStatusValues").val();                                  
		var data = myoptions.split(',');                                        
		var selector = $("<select name=\"filterInput\"  style=\"margin-left: 3px;\" class=\"filterInput selector\" />");          
		for(var val in data) {
			$("<option />", {value: data[val], text: data[val]}).appendTo(selector);
		}
		idswich = id.parent().find('.filterInput').attr('id');
		$("input#" + idswich).remove();
		
		$(selector).insertAfter(id.parent().find('.selectPredicate')).attr('id',idswich);

		
	} else if (selected == "LAST_EDITED_DATE" || selected == "SUBMISSION_DATE") {
		inputBackNormal(id.parent());
		// Find input and add classes
		id.parent().find('.filterInput').addClass('half date').val('');
		//bind datapicker
		bindDatePicker(id.parent().find('.filterInput'));
	} else {
		inputBackNormal(id.parent());
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

	filters = getFilters();

	options = {
		filters : JSON.stringify(filters),
		sortCategory : $('#sort-column').val(),
		order : $('#sort-order').val(),
		blockCount : $('#block-index').val()
	};

	$('#search-box div.alert-error').remove();

	$('div.content-box-inner').append('<div class="ajax" />');

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
			}
		},
		url : "/pgadmissions/applications/section",
		data : options,
		success : function(data) {
			if (getPageCount() === 1) {
				$('#applicationListSection').empty();
			}

			$('#applicationListSection').append(data);
		},
		complete : function() {
			$('.content-box-inner div.fetching, .content-box-inner div.ajax')
					.remove();
			addToolTips();
			loading = false;
		}
	});
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

function getFilters() {
	var filters = new Array();

	$filterbox = $(".filter");

	$.each($filterbox, function() {
		var search_category = $(this).find('.selectCategory').val();
		var search_predicate = $(this).find('.selectPredicate').val();
		var search_term = $(this).find('.filterInput').val();

		if (search_category && search_term.length > 0) {
			filters.push({
				searchCategory : search_category,
				searchPredicate : search_predicate,
				searchTerm : search_term
			});
		}
	});

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

function clearCurrentFilters(shouldApplyChanges){
	var filters = $("#search-box").find("div.filter");

	for ( var i = 1; i < filters.length; i++) {
		$(filters[i]).remove();
	}

	clearFilter(filters[0]);
	if(shouldApplyChanges){
		$('#search-go').click();
	}
}

function clearFilter(filter){
	$(filter).find(".selectCategory").val("");
	$(filter).find(".selectPredicate").empty();
	$(filter).find(".filterInput").val('');
	inputBackNormal($(filter));
}

function cleanUpFilterIds(){
	var filters=$(".filter");
	for(var i=0;i<filters.length;i++){
		$(filters[i]).attr("id","filter_"+i);
		$(filters[i]).find(".selectCategory").attr("id","searchCategory_"+i);
		$(filters[i]).find(".selectPredicate").attr("id","searchPredicate_"+i);
		$(filters[i]).find(".filterInput").attr("id","searchTerm_"+i);
	}
}

