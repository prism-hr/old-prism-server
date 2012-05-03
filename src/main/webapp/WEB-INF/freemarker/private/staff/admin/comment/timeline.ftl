 <table>
 	<tr>
 		<th>Author</th>
 		<th>Date</th>
 		<th>Comment</th>
 		<th>Type</th>
 	</tr>
 <#list comments as comment>
 	<tr>
 		<td>${(comment.user.firstName)!} ${(comment.user.lastName)!}</td>
 		<td>${(comment.createdTimestamp?string('dd-MMM-yyyy hh:mm a'))!}</td>
 		<td>${(comment.comment?html)!}</td>
 		<td>${(comment.type?html)!}</td>
 		<#if comment.type?? && comment.type ==  "REVIEW">
 			<td>Supervise: ${(comment.willingToSupervice.displayValue()?html)!}</td>
 			<td>Suitable: ${(comment.suitableCandidate.displayValue()?html)!}</td>
 			<td>Decline: ${(comment.decline.displayValue()?html)!}</td>
 		</#if>
	</tr>	 
</#list>
</table>   