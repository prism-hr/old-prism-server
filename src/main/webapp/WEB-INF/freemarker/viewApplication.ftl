<html>
   <body>
		<h2>Application View</h2>	
		<p>Name: ${application.user.firstName} ${application.user.lastName}</p>
		<p>Project Title: ${application.project.title}</p>
		<p>Project Description: ${application.project.description}</p>
		<#if user.isInRole('REVIEWER')>
      		<button onclick="location.href='/pgadmissions/application/review?id=${application.id}'">Review</button>
      	</#if>
      	<#if user.isInRole('APPROVER')>
      	<form id="feedback" method="post" class="feedback" action="/pgadmissions/application/feedback/submit?id=${application.id}">
      	<table>
      	<tr>
				<th>Feedback: </th>
				<td>
					<input type="radio" name="feedback" value="approve" /> Approve<br />
					<input type="radio" name="feedback" value="reject" /> Reject
				</td>
			</tr>
			<tr><td><input name ="submit" type="submit" value="Save" /></td></tr>
		</form>
      	</#if>
	</body>
</html>
