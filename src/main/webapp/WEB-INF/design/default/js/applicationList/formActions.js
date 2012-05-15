$(document).ready(function() {
	$('select.actionType').change(function() {
		var name = this.name;
		var id = name.substring(5).replace(']', '');

		if ($(this).val() == 'view') {
			window.location.href = "/pgadmissions/application?view=view&applicationId=" + id;
		}else if($(this).val() == 'assignReviewer') {
			window.location.href = "/pgadmissions/assignReviewers?applicationId=" + id;
		}else if($(this).val() == 'assignInterviewer') {
			window.location.href = "/pgadmissions/interview/assignInterviewers?applicationId=" + id;
		}else if($(this).val() == 'approve') {
			window.location.href = "/pgadmissions/reviewer/assign?id=" + id;
		}else if($(this).val() == 'reject') {
			window.location.href = "/pgadmissions/rejectApplication?applicationId=" + id;
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
		}else if($(this).val() == 'assignStagesDuration') {
			window.location.href = "/pgadmissions/assignStagesDuration";
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


	$('#manageUsersButton').click(function(){
		window.location.href = "/pgadmissions/manageUsers/showPage";
	});

	$("input[name*='appDownload']").click(function(){
		var id = this.id;
		id = id.replace('appDownload_', '');
		var currentAppList = $('#appList').val();
		if ($('#appDownload_'+id).attr('checked')){
			$('#appList').val(currentAppList+id+";");
		} else {
			$('#appList').val(currentAppList.replace(id+";", ''));
		}
		var appListValue = $('#appList').val();
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