var loading = false;

$(document)
		.ready(
				function() {
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
						// if ($('#searchTerm').val().length == 0 ||
						// $('#searchCategory').val() == '') {
						// fixedTip($('#search-go'), 'You must specify your
						// filter.');
						// return;
						// }
						resetPageCount();
						populateApplicationList();
					});

					$('#storeFiltersBtn')
							.click(
									function() {
										filters = getFilters();

										data = {
											filters : JSON.stringify(filters),
										};

										$
												.ajax({
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

									});

					// $('#search-box').on('change keypress', '#searchTerm,
					// #searchCategory', function() {
					// var length = $('#searchTerm').val().length;
					// var column = $('#searchCategory').val();
					// $('#search-go').toggleClass('disabled', length == 0 ||
					// column ==
					// '');
					// $('#search-reset').toggleClass('disabled', length == 0 &&
					// column
					// == '');
					//			
					// if ($('#search-go').not('.disabled')) {
					// $('#search-go').removeData('qtip');
					// }
					// });

					// ------------------------------------------------------------------------------
					// SELECTION CHANGED FOR SELECT CATEGORY
					// ------------------------------------------------------------------------------
					$('.selectCategory')
							.live(
									"change",
									function() {
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
									});

					// ------------------------------------------------------------------------------
					// SELECT ALL/NO APPLICATIONS
					// ------------------------------------------------------------------------------
					$('#select-all').click(
							function(event) {
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
					$(document).on(
							'click',
							"input[name*='appDownload']",
							function() {
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
					$('#downloadAll').click(
							function() {
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
					$("#loadActiveApplication")
							.live(
									'click',
									function() {
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
									});

					// To be extended
					// Duplicate filters buttons
					$(".add").live('click', function() {
						var newFilter=$(this).parent().clone();
						newFilter.insertAfter($(this).parent());
						clearFilter(newFilter);
						$('#search-go').click();
					});
					// Remover current filter
					$(".remove").live('click', function() {
						if ($("#search-box").find("div.filter").length > 1) {
							$(this).parent().remove();
							$('#search-go').click();
						}
					});

				});

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
	$(filter).find(".filterInput").val("");
}
