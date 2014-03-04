$(document).ready(function()
{

	$('#timelineTab').hide();
	$('#opportunityTab').hide();

	var jumpToTimeline = false; // prevent jumping to the timeline on page load.
	
	// Timeline tab.	
	$('#timelineTabBtn').click(function()
	{
		// Set the current tab.
		$('#timelineTabview ul.tabs li').removeClass('current');
		$(this).parent('li').addClass('current');
		
		$('#applicationTab').hide();
		if ( $('#timelineTab').children().length == 0 ) {
			$('#ajaxloader').show();
		}
		$('#timelineTab').show();
		$('#opportunityTab').hide();
		
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
					$('#timelineTab').html(data);	
					// Scroll to the tab.
					if (jumpToTimeline)
					{
						window.scrollTo(0, $('#timelineTab').offset().top);
					}
					else
					{
						jumpToTimeline = true;
					}
					addToolTips();
					toggleScores();
					/* Timeline expandable history*/
				 	exStatus();
				},
				 complete: function() {
					 $('#ajaxloader').fadeOut('fast');
				 }				
		});
		
		
		return false;
	});
	
	if($('#applicationTab').length > 0){
		// Application tab.
		$('#applicationTabBtn').click(function()
		{
			// Set the current tab.
			$('#timelineTabview ul.tabs li').removeClass('current');
			$(this).parent('li').addClass('current');
	
			$('#timelineTab').hide();
			if ( $('#applicationTab').children().length == 0 ) {
				$('#ajaxloader').show();
			}
			$('#applicationTab').show();
			
			// Only fetch the application form if it hasn't been fetched already.
			if ($('#applicationTab').html()=="")
			{
	
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
					  url: "/pgadmissions/application?view=view",
					  data:{
							embeddedApplication: "true",				
							applicationId:  $('#applicationId').val(),				
							cacheBreaker: new Date().getTime() 
						}, 
					  success:	function(data)
						{
							$('#applicationTab').html(data);
	
							window.scrollTo(0, $('#timelineTab').offset().top);		
	
							// Toggle grey-label class where you find instances of "Not Provided" text.
							$('#applicationTab .field').each(function()
							{
								 var strValue = $(this).text();
								 if (strValue.match("Not Provided"))
								 {
									 $(this).toggleClass('grey-label');
								 }
							});
							addToolTips();
						},
					 complete: function() {
						 $('#ajaxloader').fadeOut('fast');
					 }		
				});
			}
			else
			{
				window.scrollTo(0, $('#timelineTab').offset().top);		
			}
			return false;
		});
		$('#applicationTab').html("");
		$('#opportunityTab').hide();
	}

	if($('#isReferee').val() == 'true'){
		$('#applicationTabBtn').trigger('click');
	}else{
		// "Open" the timeline tab by default.	
		$('#timelineTabBtn').trigger('click');
	}
	
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
});