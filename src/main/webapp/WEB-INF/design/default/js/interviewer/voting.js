$(function () {
	applyTooltip('.sign-tooltip');
	$(".timeslots-scrollable").mousewheel(function(event, delta) {
		this.scrollLeft -= (delta * 30);
		event.preventDefault();
   });
	
	$('.check-all').change(function () {
		if ($(this)[0].checked) {
			$(this).closest('tr').find('input[type=checkbox].timeslot-to-accept').attr('checked', 'checked');
		}
		else {
			$(this).closest('tr').find('input[type=checkbox].timeslot-to-accept').removeAttr('checked');
		}
	});
	
	$('.timeslot-to-accept').change(function () {
		$(this).closest('tr').find('.check-all').removeAttr('checked');
	});
});