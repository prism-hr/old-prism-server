$(document).ready(function()
{
  
  //$('body.old-ie button').wrap('<span />');
	
  // Form hint tooltips.
  $('body span.hint').qtip({
    content: {
       text: function(api) {
         // Retrieve content from custom attribute of the $('.selector') elements.
    	   return $(this).attr('data-desc');
    	 
      } 
    },
    position: {
       my: 'bottom right', // Use the corner...
       at: 'top center', // ...and opposite corner
       viewport: $(window),
       adjust: {
          method: 'flip shift'
       }
    },
    style: 'tooltip-pgr ui-tooltip-shadow'
  });
  
  

  // Delete button tooltips.
  $('.button-delete').qtip({
	    content: {
	        text: function(api) {
	          // Retrieve content from custom attribute of the $('.selector') elements.
	          return "Tooltip demonstration.";
	       } 
	     },
	     position: {
	        my: 'bottom right', // Use the corner...
	        at: 'top center', // ...and opposite corner
	        viewport: $(window),
	        adjust: {
	           method: 'flip shift'
	        }
	     },
	     style: 'tooltip-pgr ui-tooltip-shadow'
	   });
  

  $('section.folding a.row-arrow').each(function()
  {
    var $this    = $(this);
    var $form    = $this.closest('table').next('form');
  });
 
  
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
  
  $('#progress span.more').bind('click', function()
  {
    var $this   = $(this);
    var $parent = $this.parent().parent();
    $('div.information', $parent).toggle();
  });
  
  fn = window['bindDatePickers'];
  if(typeof bindDatePickers == 'function'){
	  bindDatePickers();
  }
  
  
  // Sortable tables.
  $('table.data').tablesorter();
  $('table.data thead th').not(':first,:last').wrapInner('<span class="arrow" />');
  
  //Adding style to delete button to make it free from inherited style
  
  var styleMap = {
		'padding':'0',
  		'background' : 'none'
  } 
  
  $('.button-delete').parent().css(styleMap);
  
});



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

// Back to top functionality, project manager style.
function backToTop()
{
	$.scrollTo('#wrapper', 900);
}

