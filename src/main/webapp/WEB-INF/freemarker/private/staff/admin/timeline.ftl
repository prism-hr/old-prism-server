 <#list comments as comment>
 	<h1>COMMENT</h1>
	<p>${(comment.comment?html)!}</p>
	<p>${(comment.user.firstName)!} ${(comment.user.lastName)!}</p>	 
</#list>   