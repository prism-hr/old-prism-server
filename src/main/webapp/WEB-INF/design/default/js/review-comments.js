$(document).ready(function()
{
  
  var $table = $('#review-comments');
  $table.each(function()
  {
    var $this = $(this);
    var $dropdowns = $('select', $this);
    var $comments = $('td.comment', $this);
    
    $dropdowns.bind('change', function(i, obj)
    {
      alert(i);
    });
  });
});