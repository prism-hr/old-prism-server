var loading = false;

$(document).ready(function() {

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
//        if ($('#searchTerm').val().length == 0 || $('#searchCategory').val() == '') {
//            fixedTip($('#search-go'), 'You must specify your filter.');
//            return;
//        }
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
                401 : function() { window.location.reload(); },
                500 : function() { window.location.href = "/pgadmissions/error"; },
                404 : function() { window.location.href = "/pgadmissions/404"; },
                400 : function() { window.location.href = "/pgadmissions/400"; },
                403 : function() { window.location.href = "/pgadmissions/404"; }
            },
            url : "/pgadmissions/applications/saveFilters",
            data : data,
            success : function(data) {
            },
            complete : function() {
            }
        });
        
    });

//    $('#search-box').on('change keypress', '#searchTerm, #searchCategory', function() {
//        var length = $('#searchTerm').val().length;
//        var column = $('#searchCategory').val();
//        $('#search-go').toggleClass('disabled', length == 0 || column == '');
//        $('#search-reset').toggleClass('disabled', length == 0 && column == '');
//
//        if ($('#search-go').not('.disabled')) {
//            $('#search-go').removeData('qtip');
//        }
//    });

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

    // --------------------------------------------------------------------------------
    // DOWNLOAD SELECTED APPLICATIONS
    // --------------------------------------------------------------------------------
    $('#downloadAll').click(function() {
        var appListValue = $('#appList').val();
        if (appListValue != '') {
            window.open("/pgadmissions/print/all?appList=" + $('#appList').val(), '_blank');
        }
    });
    
    $("#loadMoreApplications").live('click', function() {
        if (loading) {
          return;
        }
        increasePageCount();
        populateApplicationList();
    });
	
	// To be extended
	// Duplicate filters buttons
	$(".add").live('click',function () {
		$(this).parent().clone().insertAfter($(this).parent());
	});
	// Remover current filter 
	$(".remove").live('click',function () {
		$(this).parent().remove();
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
            401 : function() { window.location.reload(); },
            500 : function() { window.location.href = "/pgadmissions/error"; },
            404 : function() { window.location.href = "/pgadmissions/404"; },
            400 : function() { window.location.href = "/pgadmissions/400"; },
            403 : function() { window.location.href = "/pgadmissions/404"; }
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
            $('.content-box-inner div.fetching, .content-box-inner div.ajax').remove();
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
    filters = new Array();
    
    $filterbox = $(".filter");
    
    $.each($filterbox, function() {
        searchCategory = $(this).find('.selectCategory').val();
        searchPredicate = $(this).find('.selectPredicate').val();
        searchTerm = $(this).find('.filterInput').val();
        
        if(searchCategory && searchTerm.length > 0){
            filters.push({
                searchCategory : searchCategory,
                searchPredicate : searchPredicate,
                searchTerm : searchTerm
            });
        }
    });

    return filters;
}
