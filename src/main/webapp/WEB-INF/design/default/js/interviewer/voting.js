$(document).ready(function() {
	var duration = parseInt($("#interview-duration").val());
	$("th.timeslot-header").each(function(){
		var startTime = $(this).find("span.start-time").text();
		var endTime = computeEndTime(startTime, duration);
		$(this).find("span.end-time").html(endTime);
	});
	
	
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
		
		$('#restart-interview').click(function() {
			$('#restart-interview-form').submit();
		});
	});
});

