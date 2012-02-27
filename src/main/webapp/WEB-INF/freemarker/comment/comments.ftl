<h2>${model.message}</h2>
<table>
<#list model.comments as comment>
<tr>
	<td>By <b>${comment.user.username}: </b></td>
</tr>
<tr>
	<td>${comment.comment}</td>
</tr>
</#list>