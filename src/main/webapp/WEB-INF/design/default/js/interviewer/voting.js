$(function () {
	applyTooltip('.sign-tooltip');
	$(".timeslots-scrollable").mousewheel(function(event, delta) {
		this.scrollLeft -= (delta * 30);
		event.preventDefault();
   });
	
	$('.can-make-anytime').click(function() {
		var checkboxes = selectCheckboxes($(this));
		
		checkboxes.removeAttr('disabled').attr('checked', 'checked');
		
		$('#cantMakeIt').val('false');
		$(this).siblings('.cant-make-it').show();
		$(this).siblings('.maybe-can-make-some').hide();
	});
	
	$('.cant-make-it').click(function() {
		var checkboxes = selectCheckboxes($(this));
		
		checkboxes.removeAttr('checked');
		checkboxes.attr('disabled', 'disabled');
		
		$('#cantMakeIt').val('true');
		$(this).hide();
		$(this).siblings('.maybe-can-make-some').show();
	});
	
	$('.maybe-can-make-some').click(function () {
		var checkboxes = selectCheckboxes($(this));
		
		checkboxes.removeAttr('disabled');
		
		$('#cantMakeIt').val('false');
		$(this).hide();
		$(this).siblings('.cant-make-it').show();
	});
	
	function selectCheckboxes (element) {
		return element.closest('tr').find('input[type=checkbox]');
	}
});