$(document).ready(function()
{

	// Create the feedback button.
	makeFeedbackButton();


  // Select all checkbox implementation
  // Listen for click on toggle checkbox
  $('#select-all').click(function(event) {
    
    var selectAllValue = this.checked;
    
    //Iterate each checkbox
      $(':checkbox').each(function() {
        this.checked = selectAllValue;                        
      });
  });
  
  
  // Toggle grey-label class where you find instances of "Not Provided" text.
  $('.field').each(function(){
     
     var strValue = $(this).text();
    
     if(strValue.match("Not Provided")){
       $(this).toggleClass('grey-label');
       
       var labelValue = $(this).prev().text();
       
       if(labelValue.match("Additional Information")){
         $(this).prev().css("font-weight","bold");
       }
       
     }
     
  });
  
  //$('body.old-ie button').wrap('<span />');
  
    
  fn = window['addToolTips'];
  if(typeof  fn  == 'function'){
    addToolTips();
  }
  
  

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
  if(typeof fn == 'function'){
    bindDatePickers();
  }
  
  
  //Adding style to delete button to make it free from inherited style
  
  var styleMap = {
    'padding':'0',
      'background' : 'none'
  } 
  
  $('.button-delete').parent().css(styleMap);
  
  
  // ------------------------------------------------------------------------------
  // Toolbar button action when jumping on a specific part of the application
  // ------------------------------------------------------------------------------
  $('.tool-button a').click(function()
  {
    var buttonId  = $(this).parent().attr("id");
    var sectionId = "";
    
    switch(buttonId)
    {
      case "tool-programme":
        sectionId = "programme-H2";
        break;
      case "tool-personal":
        sectionId = "personalDetails-H2";
        break;
      case "tool-funding":
        sectionId = "funding-H2";
        break;
      case "tool-employment":
        sectionId = "position-H2";
        break;
      case "tool-address":
        sectionId = "address-H2";
        break;
      case "tool-information":
        sectionId = "additional-H2";
        break;
      case "tool-qualification":
        sectionId = "qualifications-H2";
        break;
      case "tool-documents":
        sectionId = "documents-H2";
        break;
      case "tool-references":
        sectionId = "referee-H2";
        break;
      default:
        return false;
    }
    
    $('section.folding h2').each(function()
    {
      if (sectionId != $(this).attr('id'))
      {
        $(this).removeClass('open');
        $(this).next('div').hide();
      }
      else
      {
        $(this).addClass('open');
        $(this).next('div').show();
      }
    });
    
    $.scrollTo('#'+sectionId, 1000);
    return false;
  });


  // ------------------------------------------------------------------------------
  // Opening and closing each section with a "sliding" animation.
  // ------------------------------------------------------------------------------
  $(document).on('click', 'section.folding h2', function()
  {
    var $header = $(this);
    var $content = $header.next('div');
    
    if ($content.not(':animated'))
    {
      var state = $content.is(':visible');
      if (state)
      {
        $header.removeClass('open');
        $content.slideUp(800);
      }
      else
      {
        $header.addClass('open');
        $content.slideDown(800);
      }
    }
  });
  
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

// ------------------------------------------------------------------------------
// Back to top functionality, project manager style.
// ------------------------------------------------------------------------------
function backToTop()
{
  $.scrollTo('#wrapper', 900);
}


function makeFeedbackButton()
{
	var pathname = window.location.pathname;
	var linkToFeedback = "https://docs.google.com/spreadsheet/viewform?formkey=dDNPWWt4MTJ2TzBTTzQzdUx6MlpvWVE6MQ"
		+"&entry_2="+pathname+"&entry_3="+$("#userRolesDP").val()+"&entry_4="+$("#userFirstNameDP").val()+"&entry_5="+$("#userLastNameDP").val()
		+"&entry_6="+$("#userEmailDP").val();
		
	$("#feedbackButton").attr('href', linkToFeedback);
}
