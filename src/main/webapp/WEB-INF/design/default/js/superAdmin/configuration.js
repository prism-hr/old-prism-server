$(document).ready(function()
{
	$('#ajaxloader').show();
	$.ajax({
		type: 'GET',
		statusCode: {
			401: function()
			{
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
		url:  "/pgadmissions/configuration/config_section", 
	
		success: function(data)
		{
			$('#configsection').html(data);
			addToolTips();
		},
		complete: function()
		{
			$('#ajaxloader').fadeOut('fast');
			if ($('#registryUsers').length > 0) {
				updateRegistryForm();
			}
			/* Tabs */
			generalTabing();
			// -----------------------------------------------------------------------------
			// Restrict some text fields to numbers only.
			// -----------------------------------------------------------------------------
			$(document).on('keydown', 'input.numeric', function(event)
			{
				numbersOnly(event);
			});

		}
	});
	$('.selectpicker').selectpicker();
});

