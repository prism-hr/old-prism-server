<#import "/spring.ftl" as spring />
<table class="data" border="0">
	<colgroup>
		<col style="width: 60px;" />
		<col style="width: auto;" />
		<col style="width: 200px;" />
		<col style="width: 100px;" />
	</colgroup>
	<thead>
		<tr>
			<th scope="col">&nbsp;</th>
			<th scope="col">Name</th>
			<th scope="col">Role(s)</th>
			<th scope="col">Action</th>
		</tr>
	</thead>
	<tbody>
		<#list usersInRoles as userInRole>
		<tr <#if !userInRole.enabled>class="pending"</#if>>
			<td>&nbsp;</td>
			<td scope="col">${userInRole.firstName?html} ${userInRole.lastName?html} (${userInRole.email?html})</td>
			<td scope="col">
				<div class="role-icons <#list userInRole.getAuthoritiesForProgram(selectedProgram) as authority>${authority?lower_case} </#list>">
					<span class="is-admin"></span>
					<span class="is-interviewer"></span>
					<span class="is-reviewer"></span>
					<span class="is-supervisor"></span>
					<span class="is-approver"></span>
				</div>
				<#list userInRole.getAuthoritiesForProgram(selectedProgram) as authority>${authority} - </#list>
			</td>
			<td scope="col">
				<a class="button-edit" data-desc="Edit" href="<@spring.url '/manageUsers/edit?programCode=${selectedProgram.code}&user=${encrypter.encrypt(userInRole.id)}'/>">Edit</a>
				<a class="button-delete" data-desc="Remove" href="#" name="removeuser" id="remove_${encrypter.encrypt(userInRole.id)}">Remove</a>
			</td>
		</tr>
		</#list>			              			
	</tbody>
</table>