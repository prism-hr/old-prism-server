$(document).ready(function() {
	loadProgrammes();
	
	loadExistingFeeds();
	
	$('#new-feed-go').hide();
	
	$("#save-feed-go").on("click", function() {
		var url = '/pgadmissions/prospectus/researchOpportunitiesFeed';
		if ($('#editingFeedId').val() == "") {
			onFeedSaveOrUpdate(url, 'POST', function(feedId) {
				editFeed(feedId);
			});
		} else {
			url = url + "/" + $('#editingFeedId').val();
			onFeedSaveOrUpdate(url, 'PUT', function() {
			});
		}
	});
	
	$('#existing-feed-table').on('click', 'button.button-delete', function() {
		var feedId = $(this).closest('tr').data("feedid");
		if (feedId !== undefined) {
			deleteFeed(feedId);
		}
	});
	
	$('#existing-feed-table').on('click', '.button-edit,.button-show', function() {
		var feedId = $(this).closest('tr').data("feedid");
		if (feedId !== undefined) {
			editFeed(feedId);
		}
		if (feedId < 0) {
			$('#feed-programmes').attr("readonly", "readonly").attr("disabled", "disabled");
			$('#feed-title').attr("readonly", "readonly").attr("disabled", "disabled");
			$('#feedformat').attr("readonly", "readonly").attr("disabled", "disabled");
		} else {
			$('#feed-programmes').removeAttr("readonly", "readonly").removeAttr("disabled", "disabled");
			$('#feed-title').removeAttr("readonly", "readonly").removeAttr("disabled", "disabled");
			$('#feedformat').removeAttr("readonly", "readonly").removeAttr("disabled", "disabled");
		}
	});
	
	$('#new-feed-go').on('click', function() {
		clearForm();
		$('#new-feed-go').hide();
		$('#save-feed-go').show();
		//$('#feed-programmes', '#feed-title', '#feedformat').removeAttr("readonly", "readonly").removeAttr("disabled", "disabled");
	});
});
function shortTable () {
	$existingtable = $('#existing-feed-table table');
	$existingtable.find('tr:nth-child(1) td:first-child').css('font-weight', 'bold');
	$existingtable.find('tr:nth-child(2) td:first-child').css('font-weight', 'bold');
	$existingtable.find('tr:nth-child(1) td:nth-child(3)').html('<i class="button-delete grey disable">Remove</i>');
	$existingtable.find('tr:nth-child(2) td:nth-child(3)').html('<i class="button-delete grey disable">Remove</i>');
	$('.button-delete.disable').click(function(event) {
     event.preventDefault(); 
    });
}
function loadExistingFeeds() {
	$('#existing-feed-table-row-group').hide();
	$('#existing-feed-table tbody tbody > tr').remove();
	$.ajax({
		type: 'GET',
		dataType: "json",
		statusCode: {
			401: function() { window.location.reload(); },
			500: function() { window.location.href = "/pgadmissions/error"; },
			404: function() { window.location.href = "/pgadmissions/404"; },
			400: function() { window.location.href = "/pgadmissions/400"; },                  
			403: function() { window.location.href = "/pgadmissions/404"; }
		},
		url: "/pgadmissions/prospectus/researchOpportunitiesFeed",
		success: function(data) {
			$.each(data, function(index, item) {
				$("#existing-feed-table-row-group").show();
				var editable = item.id >= 0;
				var viewButton;
				var deleteButton;
				if (editable) {
					viewButton = '<button class="button-edit" type="button" data-desc="Edit">Edit</button>'; 
					deleteButton = '<button class="button-delete" type="button" data-desc="Remove">Remove</button>';
				} else {
					viewButton = '<button class="button-show" type="button" data-desc="Edit">Edit</button>'; 
					deleteButton = '';
				}
				$('#existing-feed-table tbody tbody').append(
					'<tr data-feedid="' + item.id + '">' 
					+ '<td>' + item.title + '</td>'
					+ '<td>' + viewButton + '</td>'
					+ '<td>' + deleteButton + '</td>'
					+ '</tr>');
			});
			shortTable();
		}
	});
}
function loadProgrammes() {
	$.ajax({
		type: 'GET',
		dataType: "json",
		statusCode: {
			401: function() { window.location.reload(); },
			500: function() { window.location.href = "/pgadmissions/error"; },
			404: function() { window.location.href = "/pgadmissions/404"; },
			400: function() { window.location.href = "/pgadmissions/400"; },                  
			403: function() { window.location.href = "/pgadmissions/404"; }
		},
		url: "/pgadmissions/prospectus/researchOpportunitiesFeed/programmes",
		success: function(data) {
			$.each(data, function(index, item) {
				$('#feed-programmes').append(new Option(item.title, item.id));
			});
		}
	});
}

function clearForm() {
	$("#researchOpportunityFeedSection").find("div.alert-error").remove();
	$('#feed-programmes').find("option").attr("selected", false);
	$('#feed-title').val("");
	$('#feedformat').find("option").attr("selected", false);
	$('#feedformat > option:first').attr("selected", true);
	$('#editingFeedId').val("");
	$("#feedCode").val("");
	$("#save-feed-go").text("Create");
}

function onFeedSaveOrUpdate(url, method, returnFunction) {
	$("#researchOpportunityFeedSection").find("div.alert-error").remove();
	
	var selectedPrograms = new Array();
	var feedSize = $('#feedformat').val();
	
	$('#feed-programmes :selected').each(function(i, selected) { 
		  selectedPrograms.push(parseInt($(selected).val()));
	});
	
	$.ajax({
		type: method,
		dataType: "json",
		contentType: "application/json",
		statusCode: {
			401: function() { window.location.reload(); },
			500: function() { window.location.href = "/pgadmissions/error"; },
			404: function() { window.location.href = "/pgadmissions/404"; },
			400: function() { window.location.href = "/pgadmissions/400"; },                  
			403: function() { window.location.href = "/pgadmissions/404"; }
		},
		url: url,
		data: JSON.stringify( { 'selectedPrograms': selectedPrograms, 'feedSize': feedSize, 'feedTitle': $('#feed-title').val()} ),
		success: function(data) {
			if (!data.success) {
				if (data.selectedPrograms != null) {
                    $('#feed-programmes').parent().append('<div class="alert alert-error"> <i class="icon-warning-sign"></i> ' + data.selectedPrograms + '</div>');
                }
                if (data.feedSize != null) {
                    $('#feedformat').parent().append('<div class="alert alert-error"> <i class="icon-warning-sign"></i> ' + data.feedSize + '</div>');
                }
                if (data.feedTitle != null) {
                    $('#feed-title').parent().append('<div class="alert alert-error"> <i class="icon-warning-sign"></i> ' + data.feedTitle + '</div>');
                }
				$('#researchOpportunityFeedSection .alert.alert-info').removeClass('alert-info').addClass('alert-success').html('<i class="icon-ok-sign"></i> Your feed is been seved');
			} else {
				loadExistingFeeds();
				$("#feedCode").val(data.iframeCode);
				returnFunction(data.id);
				$('#researchOpportunityFeedSection .alert.alert-info').removeClass('alert-info').addClass('alert-success').html('<i class="icon-ok-sign"></i> Your feed is been updated');
			}
		}
	});
}

function deleteFeed(feedId) {
	$.ajax({
		type: 'DELETE',
		dataType: "json",
		contentType: "application/json",
		statusCode: {
			401: function() { window.location.reload(); },
			500: function() { window.location.href = "/pgadmissions/error"; },
			404: function() { window.location.href = "/pgadmissions/404"; },
			400: function() { window.location.href = "/pgadmissions/400"; },                  
			403: function() { window.location.href = "/pgadmissions/404"; }
		},
		url: "/pgadmissions/prospectus/researchOpportunitiesFeed/" + feedId,
		success: function(data) {
			clearForm();
			loadExistingFeeds();
		}
	});
}
$(document).on('change', '#feedformat', function() {
	var content = $('#feedCode').val();
	if (content) {
		if ($(this).val() == 'SMALL') {
			content = content.replace('430','210');
			$('#feedCode').val(content);
		} else {
			content = content.replace('210','430');
			$('#feedCode').val(content);
		}
	}
});
function editFeed(feedId) {
	$("#researchOpportunityFeedSection").find("div.alert-error").remove();
	$('#new-feed-go').show();
	$.ajax({
		type: 'GET',
		dataType: "json",
		contentType: "application/json",
		statusCode: {
			401: function() { window.location.reload(); },
			500: function() { window.location.href = "/pgadmissions/error"; },
			404: function() { window.location.href = "/pgadmissions/404"; },
			400: function() { window.location.href = "/pgadmissions/400"; },                  
			403: function() { window.location.href = "/pgadmissions/404"; }
		},
		url: "/pgadmissions/prospectus/researchOpportunitiesFeed/" + feedId,
		success: function(data) {
			clearForm();
			$.each(data, function(index, item) {
				$('#feed-title').val(item.title);
				$("#feed-programmes > option").each(function() {
					if (_.indexOf(item.selectedPrograms, parseInt($(this).val())) > -1) {
						$(this).attr("selected", true);
					}
				});
				$('#feedformat > option[value="' + item.feedSize + '"]').attr("selected", "selected");
				$('#editingFeedId').val(item.id);
				$("#feedCode").val(item.iframeCode);
				$("#save-feed-go").text("Update");
				if(item.id >= 0) { // editable
					$("#save-feed-go").show();
				} else {
					$("#save-feed-go").hide();
				}
			});
		}
	});
}