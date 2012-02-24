$(document).ready(function()
{
	
	// Expandable content...
	$('section.folding').each(function()
	{
		var $this    = $(this);
		var $header  = $this.children('h2').filter(':first');
		var $content = $header.next('div');
		$header.bind('click', function()
		{
			$content.toggle();
			$(this).toggleClass('open', $content.is(':visible'));
			return false;
		});
		$content.not('.open').hide();
		$header.toggleClass('open', $content.is(':visible'));
	});

	$('section.folding a.row-arrow').each(function()
	{
		var $this    = $(this);
		var $form    = $this.closest('table').next('form');
		$this.bind('click', function() {
			$form.toggle();
			$this.toggleClass('open', $form.is(':visible'));
			return false;
		});
		$this.toggleClass('open', $form.is(':visible'));
	});
	
	$('#feedback span.more').bind('click', function()
	{
		var $this   = $(this);
		var $parent = $this.parent().parent();
		$('div.information', $parent).toggle();
	});
	
	// Date pickers.
	$('input[type=date]').datepicker();
	
	// Sortable tables.
	$('table.data').tablesorter();
	
});