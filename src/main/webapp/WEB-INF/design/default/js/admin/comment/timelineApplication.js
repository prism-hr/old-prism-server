$(document).ready(function()
{


	var jumpToTimeline = false; // prevent jumping to the timeline on page load.
	
	// Timeline tab.	
	$('#timelineBtn').click(function()
	{
		// Set the current tab.
		$('#timelineview ul.tabs li').removeClass('current');
		$(this).parent('li').addClass('current');
		
		$('#application').hide();
		$('#timeline').show();
		
		$.ajax({
			 type: 'GET',
			 statusCode: {
				  401: function() {
					  window.location.reload();
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
				},			
		});
		
		
		return false;
	});
	
	// Application tab.
	$('#applicationBtn').click(function()
	{
		// Set the current tab.
		$('#timelineview ul.tabs li').removeClass('current');
		$(this).parent('li').addClass('current');

		$('#timeline').html("").hide();
		$('#application').show();
		
		// Only fetch the application form if it hasn't been fetched already.
		if ($('#application').html()=="")
		{

			$.ajax({
				 type: 'GET',
				 statusCode: {
					  401: function() {
						  window.location.reload();
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
								 var labelValue = $(this).prev().text();
								 if (labelValue.match("Additional Information"))
								 {
									 $(this).prev().css("font-weight","bold");
								 }
							 }
						});

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

	// "Open" the timeline tab by default.
	$('#timelineBtn').trigger('click');
	
});