$(function () {
	applyTooltip('.sign-tooltip');
	$(".timeslots-wrapper").mousewheel(function(event, delta) {

      this.scrollLeft -= (delta * 30);
    
      event.preventDefault();

   });
});