$(document).ready(function() {
	
	populateApplicationList(true);

	// --------------------------------------------------------------------------------
	// AJAX ACTIVITY
	// --------------------------------------------------------------------------------
	$('div.content-box-inner').ajaxStart(function()
	{
		$(this).css({ position: 'relative' })
					 .append('<div class="ajax" />');
	})
	.ajaxComplete(function()
	{
		$('div.ajax', this).remove();
	});
	
	
	// --------------------------------------------------------------------------------
	// TABLE SORTING
	// --------------------------------------------------------------------------------
	// Add a SPAN tag to table headers for the arrows.
	$('table.data thead th.sortable').prepend('<span />');
	

	// --------------------------------------------------------------------------------
	// APPLICATION ACTIONS
	// --------------------------------------------------------------------------------
	$(document).on('change', 'select.actionType', function()
	{
		var name = this.name;
		var id = name.substring(5).replace(']', '');

		switch ($(this).val())
		{
			case 'view':
				window.location.href = "/pgadmissions/application?view=view&applicationId=" + id;
				break;
			case 'assignReviewer':
				window.location.href = "/pgadmissions/review/assignReviewers?applicationId=" + id;
				break;
			case 'assignInterviewer':
				window.location.href = "/pgadmissions/interview/assignInterviewers?applicationId=" + id;
				break;
			case 'approve':
				window.location.href = "/pgadmissions/approved/moveToApproved?applicationId=" + id;
				break;
			case 'reject':
				window.location.href = "/pgadmissions/rejectApplication?applicationId=" + id;
				break;
			case 'restartApprovalRequest':
				window.location.href = "/pgadmissions/approval/requestRestart?applicationId=" + id;
				break;
			case 'comment':
				window.location.href = "/pgadmissions/comment?applicationId=" + id;
				break;
			case 'print':
				window.open("/pgadmissions/print?applicationFormId=" + id, "_blank");
				//window.location.href = "/pgadmissions/print?applicationFormId=" + id;
				break;
			case 'reference':
				window.location.href = "/pgadmissions/referee/addReferences?application=" + id;
				break;
			case 'validate':
				window.location.href = "/pgadmissions/progress?application=" + id;
				break;
			case 'review':
				window.location.href = "/pgadmissions/reviewFeedback?applicationId=" + id;
				break;
			case 'interviewFeedback':
				window.location.href = "/pgadmissions/interviewFeedback?applicationId=" + id;
				break;
			case 'restartApproval':
				window.location.href = "/pgadmissions/approval/moveToApproval?applicationId=" + id;
				break;
			case 'progress':
				window.location.href = "/pgadmissions/viewprogress?applicationId=" + id;
				break;
			case 'withdraw':
				if (confirm("Are you sure you want to withdraw the application? You will not be able to submit a withdrawn application."))
				{
					$.ajax({
						type: 'POST',
						 statusCode: {
							  401: function() {
								  window.location.reload();
							  }
						  },
						url:"/pgadmissions/withdraw",
						data:{
							applicationId: id
						}, 
						success:function(data) {
							window.location.href = "/pgadmissions/applications";
						}
					});
				}
		}
	});
	
	
	// --------------------------------------------------------------------------------
	// SEARCH / FILTERING
	// --------------------------------------------------------------------------------
	$('#search-go').click(function()
	{
		$('#search-box span.invalid').remove();
		if ($('#searchTerm').val().length < 3)
		{
			$('#search-box').append('<span class="invalid">Search term must be at least three characters.</span>');
			return;
		}
		else if ($('#searchCategory').val() == '')
		{
			$('#search-box').append('<span class="invalid">Please select a search criterion.</span>');
			return;
		}
		populateApplicationList();
	});
	$('#search-reset').click(function()
	{
		populateApplicationList(true);
	});
	

	// --------------------------------------------------------------------------------
	// SELECT ALL/NO APPLICATIONS
	// --------------------------------------------------------------------------------
	$(document).on('click', "input[name*='appDownload']", function()
	{		
		var id = this.id;
		id = id.replace('appDownload_', '');
	
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
			window.location.href = "/pgadmissions/print/all?appList="+$('#appList').val();
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

	$.ajax({
		 type: 'GET',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  }
		  },
		  url: "/pgadmissions/applications/section",
		  data:options, 
		  success: function(data)
			{
				$('#applicationListSection').html(data);
				addToolTips();
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

$(function(){
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