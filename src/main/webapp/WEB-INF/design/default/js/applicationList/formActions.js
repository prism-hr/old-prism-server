var loading = false;

$(document).ready(function() {

    // Modal window functionality.
    setupModalBox();
    
    var hasFilter = $('#hasFilter').val()=="true";
    populateApplicationList(!hasFilter);
    
    // --------------------------------------------------------------------------------
    // TABLE SORTING
    // --------------------------------------------------------------------------------
    // Add a SPAN tag to table headers for the arrows.
    $('table.data thead th.sortable').prepend('<span />');

    // --------------------------------------------------------------------------------
    // SEARCH / FILTERING
    // --------------------------------------------------------------------------------
    var searchButtonsClass = hasFilter ? 'enabled' : 'disabled';
    $('#search-go').addClass(searchButtonsClass).click(function() {
        if ($('#searchTerm').val().length == 0 || $('#searchCategory').val() == '') {
            fixedTip($('#search-go'), 'You must specify your filter.');
            return;
        }
        resetPageCount();
						        populateApplicationList();
    });

    $('#search-reset').addClass(searchButtonsClass).click(function() {
        populateApplicationList(true);
        $('#search-go, #search-reset').addClass('disabled');
    });

    $('#search-box').on('change keypress', '#searchTerm, #searchCategory', function() {
        var length = $('#searchTerm').val().length;
        var column = $('#searchCategory').val();
        $('#search-go').toggleClass('disabled', length == 0 || column == '');
        $('#search-reset').toggleClass('disabled', length == 0 && column == '');

        if ($('#search-go').not('.disabled')) {
            $('#search-go').removeData('qtip');
        }
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
    
    // --------------------------------------------------------------------------------
    // INFINITE SCROLL
    // --------------------------------------------------------------------------------
//    $(window).scroll(function() {
//        if (loading) {
//            return;
//        }
//        
//        if ($(window).scrollTop() == $(document).height() - $(window).height()) {
//            $(window).scrollTop($(window).scrollTop() - 80);
//            increasePageCount();
//            populateApplicationList();
//        }
//    });
    
    $("#loadMoreApplications").live('click', function() {
        if (loading) {
          return;
        }
        increasePageCount();
        populateApplicationList();
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

function decreasePageCount() {
    var blockIndex = parseInt($('#block-index').val());
    blockIndex -= 1;
    $('#block-index').val(blockIndex.toString());
}

function populateApplicationList(reset) {
    
    loading = true;
    
    var options = {};

    if (reset) {
        // Reset search filter.
    	$('#searchTerm').val('');
        $('#sort-column').val('APPLICATION_DATE');
        $('#sort-order').val('DESCENDING');
        $('#block-index').val("1");
        $('#searchCategory').val([ '' ]);
    }

    options = {
        searchCategory : $('#searchCategory').val(),
        searchTerm : $('#searchTerm').val(),
        sortCategory : $('#sort-column').val(),
        order : $('#sort-order').val(),
        blockCount : $('#block-index').val(),
        clear : reset
    };

    $('#search-box div.alert-error').remove();

    $('div.content-box-inner').append('<div class="ajax" />');
    //$('.content-box-inner').append('<div class="fetching">Fetching more applications...</div>');
    
    $('#loadMoreApplicationsTable').show();
    var dataWasEmpty = false;
    
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
            if (reset || getPageCount() === 1) {
                $('#applicationListSection').empty();
            }
            
            if (data.indexOf("applicationRow") !== -1) {
                $('#applicationListSection').append(data);
            } else {
                dataWasEmpty = true;
            }
        },
        complete : function() {
            $('.content-box-inner div.fetching, .content-box-inner div.ajax').remove();
            addToolTips();
            loading = false;
            if (dataWasEmpty) {
                //$('#loadMoreApplicationsTable').hide();
            }
        }
    });
}

function sortList(column) {
    $('#block-index').val("1");
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
