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
			url:"/pgadmissions/comments/view",
			data:{
				id:  $('#applicationId').val(),				
				cacheBreaker: new Date().getTime() 
			},
			success:function(data) {
				$('#timeline').html(data);
				toggleScores();			
			}
	});
		
	
});