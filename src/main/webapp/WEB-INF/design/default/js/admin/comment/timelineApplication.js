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
				}
			);
		}

		return false;
	});
	
	$('#application').html("");

	// "Open" the timeline tab by default.
	$('#timelineBtn').trigger('click');
	
});