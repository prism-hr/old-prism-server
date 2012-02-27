$(document).ready(function() {
	$('select.actionType').change(function() {
		var name = this.name;
		var id = name.substring(5).replace(']', '');

		if ($(this).val() == 'view') {
			window.location.href = "/pgadmissions/application?id=" + id;
		}
		if ($(this).val() == 'assignReviewer') {
			window.location.href = "/pgadmissions/reviewer/assign?id=" + id;
		}
	});
})