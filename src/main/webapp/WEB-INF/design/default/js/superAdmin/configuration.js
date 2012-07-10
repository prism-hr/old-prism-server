$(document).ready(function()
{
	$('.content-box-inner').append('<div class="ajax" />');
	$.ajax({
		type: 'GET',
		statusCode: {
			401: function()
			{
				window.location.reload();
			}
		},
		url:  "/pgadmissions/configuration/config_section", 
	
		success: function(data)
		{
			$('#configsection').html(data);
			addToolTips();
		},
		complete: function()
		{
			$('div.ajax').remove();
			updateRegistryForm();

			// -----------------------------------------------------------------------------
			// Restrict some text fields to numbers only.
			// -----------------------------------------------------------------------------
			$(document).on('keydown', 'input.numeric', function(event)
			{
				numbersOnly(event);
			});

		}
	});
	
});
	
