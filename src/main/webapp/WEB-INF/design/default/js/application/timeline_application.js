$(document).ready(function()
{
	$('#timeline').hide();
	$('#opportunity').hide();
	
	$('#applicationBtn').click(function(){
		// Set the current tab.
		$('#timelineview ul.tabs li').removeClass('current');
		$(this).parent('li').addClass('current');
		$('#opportunity').hide();
		$('#timeline').hide();
		$('#application').show();
		window.scrollTo(0, $('#application').offset().top - 30);
	});
	
	var jumpToOpportunity = false; // prevent jumping to opportunity on page load.
	
	$('#opportunityBtn').click(function(){
		// Set the current tab.
		$('#timelineview ul.tabs li').removeClass('current');
		$(this).parent('li').addClass('current');
		$('#application').hide();
		$('#timeline').hide();
		if ($('#opportunity').children().length == 0) {
			$('#ajaxloader').show();
		}
		$('#opportunity').show();
		
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
			  url: "/pgadmissions/opportunities/related",
			  data:{
					id: $('#applicationId').val(),
					cacheBreaker: new Date().getTime() 
				}, 
			  success:	function(data)
				{
					$('#opportunity').html(data);	
					// Scroll to the tab.
					if (jumpToOpportunity)
					{
						window.scrollTo(0, $('#opportunity').offset().top - 30);
					}
					else
					{
						jumpToOpportunity = true;
					}
					addToolTips();
					toggleScores();	
				}, complete: function() {
                 $('#ajaxloader').fadeOut('fast');
             }	
		});
		
		return false;
	});
	
	var jumpToTimeline = false; // prevent jumping to the timeline on page load.
	
	// Timeline tab.	
	$('#timelineBtn').click(function()
	{
		// Set the current tab.
		$('#timelineview ul.tabs li').removeClass('current');
		$(this).parent('li').addClass('current');
		$('#application').hide();
		$('#opportunity').hide();
		if ($('#timeline').children().length == 0) {
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
					id: $('#applicationId').val(),				
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
				 /* Timeline expandable history*/
				 exStatus();
             }	
		});
		
		return false;
	});
	
});
