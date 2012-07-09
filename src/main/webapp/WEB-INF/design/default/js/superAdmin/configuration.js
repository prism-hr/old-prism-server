$(document).ready(function()
{
	$('#configsection').append('<div class="ajax" />');
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
			$('#configsection div.ajax').remove();
			updateRegistryForm();

		}
	});
	
});
	
