$(document).ready(function()
{
	
	// Expandable content...
	$('section.folding').each(function()
	{
		var $this    = $(this);
		var $header  = $this.children('h2').filter(':first');
		var $content = $this.children('div').filter(':first');
		$header.bind('click', function()
		{
			$(this).toggleClass('open');
			$content.toggle();
		});
		$content.hide();
	});
	
	
	// Sortable tables.
	$('table.data').tablesorter();
	
});