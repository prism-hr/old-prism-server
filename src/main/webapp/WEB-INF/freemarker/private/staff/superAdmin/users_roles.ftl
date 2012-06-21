<#import "/spring.ftl" as spring />
<table class="data" border="0">
<colgroup>
	<col style="width: 220px;" />
	<col style="width: auto;" />
	<col style="width: 200px;" />
	<col style="width: 100px;" />
</colgroup>
<thead>
	<tr>
		<th scope="col">Email address</th>
		<th scope="col">Name</th>
		<th scope="col">Role(s)</th>
		<th scope="col">Action</th>
	</tr>
</thead>
<tbody>
	<#list usersInRoles as userInRole>
	<tr <#if !userInRole.enabled>class="pending"</#if>>
		<td scope="col">${userInRole.email?html}</td>
		<td scope="col">${userInRole.firstName?html} ${userInRole.lastName?html}</td>
		<td scope="col">${userInRole.getAuthoritiesForProgramAsString(selectedProgram)}</td>
		<td scope="col"><a href="<@spring.url '/manageUsers/edit?programCode=${selectedProgram.code}&user=${encrypter.encrypt(userInRole.id)}'/>">Edit</a> /<a href="#" name="removeuser" id="remove_${encrypter.encrypt(userInRole.id)}">Remove</a></td>
	</tr>
	</#list>			              			
</tbody>
</table>