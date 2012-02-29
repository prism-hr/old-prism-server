$(document).ready(function() {
	$('select.actionType').change(function() {
		var name = this.name;
		var id = name.substring(5).replace(']', '');

		if ($(this).val() == 'view') {
			window.location.href = "/pgadmissions/application?view=view&id=" + id;
		}else if($(this).val() == 'assignReviewer') {
			window.location.href = "/pgadmissions/reviewer/assign?id=" + id;
		}else if($(this).val() == 'approve') {
			window.location.href = "/pgadmissions/reviewer/assign?id=" + id;
		}else if($(this).val() == 'reject') {
			window.location.href = "/pgadmissions/reviewer/assign?id=" + id;
		}else if($(this).val() == 'comment') {
			window.location.href = "/pgadmissions/comments/showAll?id=" + id;
		}
	});
});