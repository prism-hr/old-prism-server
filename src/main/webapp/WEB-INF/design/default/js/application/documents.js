$(document).ready(function(){

	$('#documentsCloseButton').click(function(){
		$('#documents-H2').trigger('click');
		return false;
	});
	
	$('#documentsCancelButton').click(function(){
		$("span[class='invalid']").each(function(){
			$(this).html("");
		});
	});
	
	$('a[name="deleteButton"]').click( function(){	
		$(this).parent("form").submit();
	});

	//open/close
	var $header  =$('#documents-H2');
	var $content = $header.next('div');
	$header.bind('click', function()
	{
	  $content.toggle();
	  $(this).toggleClass('open', $content.is(':visible'));
	  return false;
	});
	
	
});
