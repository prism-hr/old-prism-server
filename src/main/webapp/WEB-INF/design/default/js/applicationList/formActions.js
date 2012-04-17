$(document).ready(function() {
	$('select.actionType').change(function() {
		var name = this.name;
		var id = name.substring(5).replace(']', '');

		if ($(this).val() == 'view') {
			window.location.href = "/pgadmissions/application?view=view&applicationId=" + id;
		}else if($(this).val() == 'assignReviewer') {
			window.location.href = "/pgadmissions/reviewer/assign?id=" + id;
		}else if($(this).val() == 'approve') {
			window.location.href = "/pgadmissions/reviewer/assign?id=" + id;
		}else if($(this).val() == 'reject') {
			window.location.href = "/pgadmissions/reviewer/assign?id=" + id;
		}else if($(this).val() == 'comment') {
			window.location.href = "/pgadmissions/comments/showAll?id=" + id;
		}else if($(this).val() == 'print') {
			window.location.href = "/pgadmissions/print?applicationFormId=" + id;
		}else if($(this).val() == 'reference') {
			window.location.href = "/pgadmissions/referee/addReferences?applicationFormId=" + id;
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