<html>
   <body>
		<h2>Application View</h2>	
		<p>Title: ${application.title}</p>
		<p>Gender: ${application.gender}</p>
		<p>Date of birth: ${application.dob}</p>
		<p>Country of birth: ${application.cob}</p></p>
		<p>Nationality: ${application.nat}
		<p>Description of research: ${application.descriptionOfResearch}</p>
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
