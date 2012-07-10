var fetches = 0;

$(document).ready(function()
{

	// Modal window functionality.
	setupModalBox();
	
	populateApplicationList(true);
	
	
	// --------------------------------------------------------------------------------
	// TABLE SORTING
	// --------------------------------------------------------------------------------
	// Add a SPAN tag to table headers for the arrows.
	$('table.data thead th.sortable').prepend('<span />');
	

	// --------------------------------------------------------------------------------
	// SEARCH / FILTERING
	// --------------------------------------------------------------------------------
	$('#search-go').addClass('disabled').click(function()
	{
		if ($('#searchTerm').val().length < 3 || $('#searchCategory').val() == '')
		{
			fixedTip($('#search-go'), 'You must specify your filter.');
			return;
		}
		populateApplicationList();
	});
	
	$('#search-reset').addClass('disabled').click(function()
	{
		populateApplicationList(true);
		$('#search-go, #search-reset').addClass('disabled');
	});
	
	$('#search-box').on('change keypress', '#searchTerm, #searchCategory', function()
	{
		var length = $('#searchTerm').val().length;
		var column = $('#searchCategory').val();
		$('#search-go').toggleClass('disabled', length < 3 || column == '');
		$('#search-reset').toggleClass('disabled', length == 0 && column == '');
		
		if ($('#search-go').not('.disabled'))
		{
			$('#search-go').removeData('qtip');
		}
	});
	

	// ------------------------------------------------------------------------------
	// SELECT ALL/NO APPLICATIONS
	// ------------------------------------------------------------------------------
  $('#select-all').click(function(event)
	{
    var selectAllValue = this.checked;
		var $appList = $('#appList');
		var list = '';
		
		$('#applicationListSection input:checkbox').each(function()
		{
			this.checked = selectAllValue;
			if (selectAllValue)
			{
				list += $(this).val() + ";";
			}
		});
		
		$appList.val(list);
  });
  
	
	// --------------------------------------------------------------------------------
	// SELECT APPLICATIONS
	// --------------------------------------------------------------------------------
	$(document).on('click', "input[name*='appDownload']", function()
	{		
		var id = $(this).val();
		var currentAppList = $('#appList').val();		

		if ($(this).is(':checked'))
		{
			$('#appList').val(currentAppList + id + ";");
		}
		else
		{
			$('#appList').val(currentAppList.replace(id + ";", ''));
		}
	});


	// --------------------------------------------------------------------------------
	// DOWNLOAD SELECTED APPLICATIONS
	// --------------------------------------------------------------------------------
	$('#downloadAll').click(function()
	{
		var appListValue = $('#appList').val();
		if (appListValue != '')
		{
			window.open("/pgadmissions/print/all?appList="+$('#appList').val(), '_blank');
		}
	});

});


function populateApplicationList(reset)
{
	var options = {};
	
	if (reset)
	{
		// Reset search filter.
		$('#searchTerm').val('');
		$('#sort-column').val('APPLICATION_DATE');
		$('#sort-order').val('DESCENDING');
		$('#block-index').val("1");
		$('#searchCategory').val(['']);
	}

	options = {
		searchCategory: $('#searchCategory').val(),
		searchTerm:	    $('#searchTerm').val(),
		sortCategory:   $('#sort-column').val(),
		order:          $('#sort-order').val(),
		blockCount:     $('#block-index').val()
	};
	
	$('#search-box span.invalid').remove();
	
	// We're fetching more applications.
	if (fetches == 0)
	{
		$('div.content-box-inner').append('<div class="ajax" />');
		$('.content-box-inner').append('<div class="fetching">Fetching more applications...</div>');
	}
	fetches++;

	$.ajax({
		 type: 'GET',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  },
			  500: function() {
				  window.location.href = "/pgadmissions/error";
			  },
			  404: function() {
				  window.location.href = "/pgadmissions/404";
			  },
			  400: function() {
				  window.location.href = "/pgadmissions/400";
			  },				  
			  403: function() {
				  window.location.href = "/pgadmissions/404";
			  }
		  },
		  url: "/pgadmissions/applications/section",
		  data: options, 
		  success: function(data)
			{
				$('#applicationListSection').html(data);
			},
			complete: function()
			{
				fetches--;
				if (fetches == 0)
				{
					$('.content-box-inner div.fetching, .content-box-inner div.ajax').remove();
					addToolTips();
				}
			}
	});
}


function sortList(column)
{
	$('#block-index').val("1");
	oldValue = $('#sort-column').val();
	newValue = column.id;
	
	if (oldValue == newValue)
	{
		flipSortOrder();
	}
	else
	{
		$('#sort-column').val(newValue);
		$('#sort-order').val("ASCENDING");
	}
	
	// Highlight the sorting column.
	$('table th').removeClass('sorting asc desc');
	var $header = $('#'+newValue);
	$header.addClass('sorting');
	if ($('#sort-order').val() == "DESCENDING")
	{
		$header.addClass('desc');
	}
	else
	{
		$header.addClass('asc');
	}

	populateApplicationList();
}


function flipSortOrder()
{
	oldOrder = $('#sort-order').val();
	if (oldOrder == "ASCENDING")
	{
		$('#sort-order').val("DESCENDING");
	}
	else
	{
		$('#sort-order').val("ASCENDING");
	}
}


$(function()
{
	$('#applicationListSection').scrollPagination({
		'callback' : function() {
			var blockIndex = parseInt($('#block-index').val());
			blockIndex += 1;
			$('#block-index').val(blockIndex.toString());
			populateApplicationList();
		},
//		'contentPage': '/pgadmissions/applications/pager', // the page where you are searching for results
//		'contentData': {}, // you can pass the children().size() to know where is the pagination
		'scrollTarget': $(window), // who gonna scroll? in this example, the full window
		'heightOffset': 10, // how many pixels before reaching end of the page would loading start? positives numbers only please
	});  
});