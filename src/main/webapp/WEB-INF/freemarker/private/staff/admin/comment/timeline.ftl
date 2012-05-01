 <table>
 	<tr>
 		<th>Author</th>
 		<th>Date</th>
 		<th>Comment</th>
 	</tr>
 <#list comments as comment>
 	<tr>
 		<td>${(comment.user.firstName)!} ${(comment.user.lastName)!}</td>
 		<td>${(comment.createTimestamp?string('dd-MMM-yyyy hh:mm a'))!}</td>
 		<td>${(comment.comment?html)!}</td>
	</tr>	 
</#list>
</table>   