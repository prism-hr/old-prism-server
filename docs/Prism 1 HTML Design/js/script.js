$(document).ready(function()
{
  
  //------------------------------------------------
	// Form hint tooltips.
  //------------------------------------------------
  $('body span.hint').qtip({
    content: {
       text: function(api) {
         // Retrieve content from custom attribute of the $('.selector') elements.
         return $(this).attr('data-desc');
      } 
    },
    position: {
       my: 'bottom right', // where the tooltip's pointer appears
       at: 'top center',   // where the tooltip is positioned
       viewport: $(window),
       adjust: {
          method: 'flip shift'
       }
    },
    style: 'tooltip-pgr ui-tooltip-shadow'
  });
  
  
  //------------------------------------------------
  // Expandable content boxes.
  //------------------------------------------------
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

  });

  //------------------------------------------------
	// Expanding a FORM from an existing row's arrow button.
  //------------------------------------------------
  $('section.folding a.row-arrow').each(function()
  {
    var $this    = $(this);
    var $form    = $this.closest('table').next('form');
    /*
    $this.bind('click', function() {
      $form.toggle();
      $this.toggleClass('open', $form.is(':visible'));
      return false;
    });
    $this.toggleClass('open', $form.is(':visible'));
    */
  });
  
   //------------------------------------------------
	 // old commenting functionality.
	 //------------------------------------------------
  $('section.folding a.comment-open').each(function()
  {
    var $this = $(this);
    var $target = $this.closest('section.folding').find('div.comment');
    
    // comment box
    $this.bind('click', function()
    {
      $target.show();
      return false;
    });
    $target.find('a.comment-close').bind('click', function()
    {
      $target.hide();
      return false;
    });
    
    // previous comments
    var $previous = $('div.previous', $target);
    var i = 0;
    $('li', $previous).each(function()
    {
      i++;
      var $item = $(this);
      if (i % 2) { $item.addClass('even'); }
      $('span', $item).hide();
      $item.prepend('<a class="more" href="#">read</a>');
      $('a.more', $item).bind('click', function()
      {
        $('span', $item).toggle();
        return false;
      });
    });
    
    $target.hide();
  });
  
  //------------------------------------------------
	// Expanding content on application progress page.
  //------------------------------------------------
	$('#progress span.more').bind('click', function()
  {
    var $this   = $(this);
    var $parent = $this.parent().parent();
    $('div.information', $parent).toggle();
  });
  
  //------------------------------------------------
	// Date pickers and setting their date format.
  //------------------------------------------------
  $('input.date').datepicker({ dateFormat: 'yy/mm/dd', yearRange: "1900:+20" }); });
  
  //------------------------------------------------
	// Sortable tables.
  //------------------------------------------------
  $('table.data').tablesorter();
  $('table.data thead th').wrapInner('<span class="arrow" />');
  
});


//------------------------------------------------
// Displays a GMail-style message.
//------------------------------------------------
function msg(message, type)
{
  var $msg = $('#message-bar');
  if ($msg.length == 0)
  {
    $('body').append('<div id="message-bar" />');
    $msg = $('#message-bar');
  }
  
  $msg.stop(true, true).hide().removeClass().addClass(type).html(message);
  $msg.css({ marginLeft: -($msg.width() / 2) + 'px' });
  $msg.fadeIn(700).delay(3000).fadeOut(700);
}

//------------------------------------------------
// Back to top functionality, project manager style.
//------------------------------------------------
function backToTop()
{
	$.scrollTo('#wrapper', 900);
}
  
