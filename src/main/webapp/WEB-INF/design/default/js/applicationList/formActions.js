$(document).ready(function() {
	
	populateApplicationList(true);
	
	$(document).on('change', 'select.actionType', function() {
		var name = this.name;
		var id = name.substring(5).replace(']', '');

		if ($(this).val() == 'view') {
			window.location.href = "/pgadmissions/application?view=view&applicationId=" + id;
		}else if($(this).val() == 'assignReviewer') {
			window.location.href = "/pgadmissions/review/assignReviewers?applicationId=" + id;
		}else if($(this).val() == 'assignInterviewer') {
			window.location.href = "/pgadmissions/interview/assignInterviewers?applicationId=" + id;
		}else if($(this).val() == 'approve') {
			window.location.href = "/pgadmissions/approved/moveToApproved?applicationId=" + id;
		}else if($(this).val() == 'reject') {
			window.location.href = "/pgadmissions/rejectApplication?applicationId=" + id;
		}else if($(this).val() == 'restartApprovalRequest') {
			window.location.href = "/pgadmissions/approval/requestRestart?applicationId=" + id;
		}else if($(this).val() == 'comment') {
			window.location.href = "/pgadmissions/comment?applicationId=" + id;
		}else if($(this).val() == 'print') {
			window.location.href = "/pgadmissions/print?applicationFormId=" + id;
		}else if($(this).val() == 'reference') {
			window.location.href = "/pgadmissions/referee/addReferences?application=" + id;
		}else if($(this).val() == 'validate') {
			window.location.href = "/pgadmissions/progress?application=" + id;
		}else if($(this).val() == 'review') {
			window.location.href = "/pgadmissions/reviewFeedback?applicationId=" + id;
		}else if($(this).val() == 'interviewFeedback') {
			window.location.href = "/pgadmissions/interviewFeedback?applicationId=" + id;
		}else if($(this).val() == 'restartApproval') {
			window.location.href = "/pgadmissions/approval/moveToApproval?applicationId=" + id;
		}
		else if($(this).val() == 'withdraw') {
				if(confirm("Are you sure you want to withdraw the application? You will not be able to submit a withdrawn application."))
				{
					$.post("/pgadmissions/withdraw",
					{
						applicationId: id
					}, 
					function(data) {
						window.location.href = "/pgadmissions/applications";
					}
				);
				}
			}
	});
	
	
	/* Search functionality. */
	$('#search-go').click(function() { populateApplicationList(); });
	$('#search-reset').click(function() { populateApplicationList(true); });
	/*
	$('#searchBtn').click(function(){
		populateApplicationList();
	});
	$('#searchCategory').change(function() { populateApplicationList(); });
	*/
	

	$('#manageUsersButton').click(function(){
		window.location.href = "/pgadmissions/manageUsers/showPage";
	});
	
	$('#configuration').click(function(){
		window.location.href = "/pgadmissions/configuration";
	});

	$(document).on('click', "input[name*='appDownload']", function(){		
		var id = this.id;
		id = id.replace('appDownload_', '');
	
		var currentAppList = $('#appList').val();		
		if ($(this).attr('checked')){
			$('#appList').val(currentAppList + id + ";");
		} else {
			$('#appList').val(currentAppList.replace(id  +";", ''));
		}
	
		
	});

	$('#downloadAll').click(function(){
		var appListValue = $('#appList').val();
		if (appListValue==''){
			alert("At least one application must be selected for download!");
		} else {
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
	}

	options = {
		searchCategory: $('#searchCategory').val(),
		searchTerm:	    $('#searchTerm').val(),
		sortCategory:   $('#sort-column').val(),
		order:          $('#sort-order').val(),
		blockCount:     $('#block-index').val()
	};
	
	$('#search-box span.invalid').remove();

	if (!reset)
	{
		// Check for search term.
		if (options.searchTerm.length < 3)
		{
			$('#search-box').append('<span class="invalid">Search term must be at least three characters.</span>');
			return;
		}
	
		// Check for selected criteria.
		if (options.sortCategory == '')
		{
			$('#search-box').append('<span class="invalid">Please select a search criterion.</span>');
			return;
		}
	}
	
	$.get("/pgadmissions/applications/section",
		options,
		function(data)
		{
			$('#applicationListSection').html(data);
			addToolTips();
		}
	);
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
	if ($('#sort-order') == "DESCENDING")
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