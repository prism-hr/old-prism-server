$(document).ready(function()
{

	$('#timeline').hide();
	$('#opportunity').hide();

	var jumpToTimeline = false; // prevent jumping to the timeline on page load.
	
	// Timeline tab.	
	$('#timelineBtn').click(function()
	{
		// Set the current tab.
		$('#timelineview ul.tabs li').removeClass('current');
		$(this).parent('li').addClass('current');
		
		$('#application').hide();
		if ( $('#timeline').children().length == 0 ) {
			$('#ajaxloader').show();
		}
		$('#timeline').show();
		$('#opportunity').hide();
		
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
						window.scrollTo(0, $('#timeline').offset().top);
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
	
	if($('#application').length > 0){
		// Application tab.
		$('#applicationBtn').click(function()
		{
			// Set the current tab.
			$('#timelineview ul.tabs li').removeClass('current');
			$(this).parent('li').addClass('current');
	
			$('#timeline').hide();
			if ( $('#application').children().length == 0 ) {
				$('#ajaxloader').show();
			}
			$('#application').show();
			
			// Only fetch the application form if it hasn't been fetched already.
			if ($('#application').html()=="")
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
							$('#application').html(data);
	
							window.scrollTo(0, $('#timeline').offset().top);		
	
							// Toggle grey-label class where you find instances of "Not Provided" text.
							$('#application .field').each(function()
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
				window.scrollTo(0, $('#timeline').offset().top);		
			}
			return false;
		});
		$('#application').html("");
		$('#opportunity').hide();
	}

	if($('#isReferee').val() == 'true'){
		$('#applicationBtn').trigger('click');
	}else{
		// "Open" the timeline tab by default.	
		$('#timelineBtn').trigger('click');
	}
	
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
});