$(document).ready(function()
{
	$.ajax({
		 type: 'GET',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  }
		  },
		url:"/pgadmissions/badge",
		data:{
		},
		success:function(data)
		{
			$('#badgeSection').html(data);
			addToolTips();
			bindDatePicker('#batchdeadline');
		}
	});	
	
	$('#cancelBadge').click(function()
	{
		$('#programme').val('');
		$('#project').val('');
		$('#programhome').val('');
		$('#batchdeadline').val('');
		$('#html').val('');
		$('#badge').attr("src", "");
	});

	$(document).on('change', '#programme, #project, #programhome, #batchdeadline', updateBadge);	
});
	
function updateBadge()
{
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
			$('#html').val(data).autosize();
		}
	});	


};