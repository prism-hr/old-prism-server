$(document).ready(function()
{
	$.ajax({
		 type: 'GET',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  },
			  500: function() {
				  window.location.href = "/pgadmissions/error";
			  },
			  404: function() {
				  window.location.href = "/pgadmissions/404";
			  },
			  400: function() {
				  window.location.href = "/pgadmissions/400";
			  },				  
			  403: function() {
				  window.location.href = "/pgadmissions/404";
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
});
	
function updateBadge()
{
	$.ajax({
		 type: 'GET',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  },
			  500: function() {
				  window.location.href = "/pgadmissions/error";
			  },
			  404: function() {
				  window.location.href = "/pgadmissions/404";
			  },
			  400: function() {
				  window.location.href = "/pgadmissions/400";
			  },				  
			  403: function() {
				  window.location.href = "/pgadmissions/404";
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