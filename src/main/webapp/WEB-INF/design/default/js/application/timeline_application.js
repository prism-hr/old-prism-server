$(document).ready(function()
{
	$('#applicationBtn').click(function(){
		// Set the current tab.
		$('#timelineview ul.tabs li').removeClass('current');
		$(this).parent('li').addClass('current');
		$('#applicationTab').show();
		$('#timeline').hide();
		window.scrollTo(0, $('#applicationTab').offset().top - 30);
		
	});
	
	
	var jumpToTimeline = false; // prevent jumping to the timeline on page load.
	
	// Timeline tab.	
	$('#timelineBtn').click(function()
	{
		// Set the current tab.
		$('#timelineview ul.tabs li').removeClass('current');
		$(this).parent('li').addClass('current');
		$('#applicationTab').hide();
		$('#timeline').append('<div class="ajax" />').show();
		
		$.ajax({
			 type: 'GET',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  },
				  500: function() {
					  window.location.href = "/pgadmissions/error";
				  },
				  404: function() {
					  window.location.href = "/pgadmissions/404";
				  },
				  400: function() {
					  window.location.href = "/pgadmissions/400";
				  },				  
				  403: function() {
					  window.location.href = "/pgadmissions/404";
				  }
			  },
			  url: "/pgadmissions/comments/view",
			  data:{
					id:  $('#applicationId').val(),				
					cacheBreaker: new Date().getTime() 
				}, 
			  success:	function(data)
				{
					$('#timeline').html(data);	
					// Scroll to the tab.
					if (jumpToTimeline)
					{
						window.scrollTo(0, $('#timeline').offset().top - 30);
					}
					else
					{
						jumpToTimeline = true;
					}
					addToolTips();
					
					$.each($('.hide-score'), function (key, value) {
						$('.hide-score-group').append($(this));
						$(this).show();
					});
					
					$('#more-scores').toggle(function () {
						$('.hide-score-group').slideDown();
						$(this).text('less');
					}, function () {
						$('.hide-score-group').slideUp();
						$(this).text('more');
					});
				}			
		});
		
		return false;
	});
	
});
