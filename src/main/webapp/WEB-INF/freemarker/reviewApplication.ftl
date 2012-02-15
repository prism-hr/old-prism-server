<html>
<body>
You have been assigned to review application: ${application.id}
<form action= "/pgadmissions/application/submit?id=${application.id}" method="post">
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

</body>
</html>