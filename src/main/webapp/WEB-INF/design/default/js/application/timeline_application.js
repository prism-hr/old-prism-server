$(document).ready(function()
{
	$('#timelineTab').hide();
	$('#opportunityTab').hide();
	
	$('#applicationBtn').click(function(){
		alert("Got Here");
		// Set the current tab.
		$('#timelineTabview ul.tabs li').removeClass('current');
		$(this).parent('li').addClass('current');
		$('#opportunityTab').hide();
		$('#timelineTab').hide();
		$('#applicationTab').show();
		window.scrollTo(0, $('#applicationTab').offset().top - 30);
	});
	
	var jumpToOpportunity = false; // prevent jumping to opportunity on page load.
	
	$('#opportunityTabBtn').click(function(){
		// Set the current tab.
		$('#timelineTabview ul.tabs li').removeClass('current');
		$(this).parent('li').addClass('current');
		$('#applicationTab').hide();
		$('#timelineTab').hide();
		if ($('#opportunityTab').children().length == 0) {
			$('#ajaxloader').show();
		}
		$('#opportunityTab').show();
		
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
					$('#opportunityTab').html(data);	
					// Scroll to the tab.
					if (jumpToOpportunity)
					{
						window.scrollTo(0, $('#opportunityTab').offset().top - 30);
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
	$('#timelineTabBtn').click(function()
	{
		// Set the current tab.
		$('#timelineTabview ul.tabs li').removeClass('current');
		$(this).parent('li').addClass('current');
		$('#applicationTab').hide();
		$('#opportunityTab').hide();
		if ($('#timelineTab').children().length == 0) {
			$('#ajaxloader').show();
		}
		$('#timelineTab').show();
		
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
					$('#timelineTab').html(data);	
					// Scroll to the tab.
					if (jumpToTimeline)
					{
						window.scrollTo(0, $('#timelineTab').offset().top - 30);
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
