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
		if ( $('#timeline').children().length == 0 ) {
			$('#ajaxloader').show();
		}
		$('#timeline').show();
		
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
					toggleScores();	
				}, complete: function() {
                 $('#ajaxloader').fadeOut('fast');
				 exStatus();
             }	
		});
		
		return false;
	});
	
});
function exStatus() {
	var $expander = $(".excontainer");
	$.each($expander, function() {
		if ($(this).parent().index() > 0) {
			$(this).hide();
			$(this).parent().find('>.box i').removeClass('icon-minus-sign').addClass('icon-plus-sign');
		}
		$(this).parent().find('>.box i').click(function() {
			$(this).parent().parent().find(".excontainer").slideToggle(300);
			if ($(this).attr('class') == 'icon-plus-sign') {
				$(this).removeClass('icon-plus-sign').addClass('icon-minus-sign');
			} else {
				$(this).removeClass('icon-minus-sign').addClass('icon-plus-sign');
			}
		})
	});
}