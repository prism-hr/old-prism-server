<#import "/spring.ftl" as spring />
<html>
<body>
<form action= "/pgadmissions/comments/submit?id=${model.application.id}" method="post">
<table>
<tr>
<td> Comment: </td>
<td><textarea id="comment" name="comment" cols="45" rows="7"> </textarea></td>
</tr>
<tr>
	<td><input name ="commit" type="submit" value="Submit Comment" /></td>
</tr>
</table>
</form>
<p><a href="<@spring.url '/j_spring_security_logout'/>">Log out</a></p>
</body>

</html>