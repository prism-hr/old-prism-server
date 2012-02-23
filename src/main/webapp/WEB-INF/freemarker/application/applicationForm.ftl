<div>
	<h2>Apply Now</h2>
	<form id="applicationForm" method="post" class="application" action="/pgadmissions/apply">
			<p>Project: ${application.project.title}</p>
			<p>Application Number: <input type="text" value="${application.id}"  readonly="readonly" name="id"/></p>
			<input type="submit" value="Submit" id="submit" name="submit"/>
	</form>
	
</div>