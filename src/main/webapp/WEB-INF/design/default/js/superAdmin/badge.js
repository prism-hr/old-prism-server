$(document).ready(function()
{
	bindDatePicker('#batchdeadline');
	
	$('#cancelBadge').click(function()
	{
		$('#programme').val('');
		$('#project').val('');
		$('#programhome').val('');
		$('#batchdeadline').val('');
		$('#html').val('');
		$('#badge').attr("src", "");
	});
	
 	$('#programme').change(updateBadge);
 	$('#project').change(updateBadge);
 	$('#programhome').change(updateBadge);
 	$('#batchdeadline').change(updateBadge);
 	
});
	
function updateBadge(){
	$.ajax({
		 type: 'GET',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  }
		  },
 		url:"/pgadmissions/badge/html",
		data:{
			program : $('#programme').val(),	
			project: $('#project').val(),
			programhome: $('#programhome').val(),
			batchdeadline: $('#batchdeadline').val(),			
			cacheBreaker: new Date().getTime() 
		},
		success:function(data)
		{
			$('#html').html(data);
			addToolTips();
		}
	});	
	$('#badge').attr("src", "/pgadmissions/badge/html?program=" + $('#programme').val() 
		+ "&project=" +$('#project').val()
		+ "&programhome=" +$('#programhome').val()
		+ "&batchdeadline=" +$('#batchdeadline').val()
		+ "&disable=true");
};				