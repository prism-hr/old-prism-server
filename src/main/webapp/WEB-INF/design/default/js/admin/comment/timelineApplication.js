$(document).ready(function()
{

	// Timeline tab.	
	$('#timelineBtn').click(function()
	{
		// Set the current tab.
		$('#timelineview ul.tabs li').removeClass('current');
		$(this).parent('li').addClass('current');
		
		$('#application').hide();
		$('#timeline').show();
		
		$.get("/pgadmissions/comments/view",
				{
					id:  $('#applicationId').val(),				
					cacheBreaker: new Date().getTime() 
				},
				function(data) {
					$('#timeline').html(data);	
					// Scroll to the tab.
					window.scrollTo(0, $('#timeline').position().top);		
					addToolTips();	
				}
		);
		
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
		
		if ($('#application').html()=="")
		{
			// Only fetch the application form if it hasn't been fetched already.
			$.get("/pgadmissions/application?view=view",
				{
					embeddedApplication: "true",				
					applicationId:  $('#applicationId').val(),				
					cacheBreaker: new Date().getTime() 
				},
				function(data)
				{
					$('#application').html(data);

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
			);
		}

		return false;
	});
	
	$('#application').html("");

	// "Open" the timeline tab by default.
	$('#timelineBtn').trigger('click');
	
});