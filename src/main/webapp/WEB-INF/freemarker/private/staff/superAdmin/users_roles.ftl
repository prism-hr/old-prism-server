<#import "/spring.ftl" as spring />
<table class="data table table-condensed" border="0">
	
	<tbody>
		<tr>
			<td colspan="4" class="scrollparent">
				<div class="scroll">
					<table class="table-hover table-hover table-striped">
						<colgroup>
							<col style="width: 20px;" />
							<col/>
							<col style="width: 240px;" />
                            <col style="width: 66px;" />
						</colgroup>
						<tbody>
							<#list usersInRoles as userInRole>
							<tr <#if !userInRole.enabled>class="pending"</#if>>
								<td><span class="arrow" <#if !userInRole.enabled> data-desc="Pending"</#if> >&nbsp;</span></td>
								<td scope="col">${userInRole.firstName?html} ${userInRole.lastName?html} (${userInRole.email?html})</td>
								<td scope="col">
									<div class="role-icons <#list userInRole.getAuthoritiesForProgram(selectedProgram) as authority>${authority?lower_case} </#list>">
										<span class="is-admin" data-desc="Administrator"></span>
										<span class="is-approver" data-desc="Approver"></span>
										<span class="is-viewer" data-desc="Viewer"></span>
									</div>
								</td>
								<td scope="col">
									<a class="button-edit" data-desc="Edit" href="<@spring.url '/manageUsers/edit?programCode=${selectedProgram.code}&user=${encrypter.encrypt(userInRole.id)}#editUser'/>">Edit</a>
									<a class="button-delete" data-desc="Remove" href="#" name="removeuser" id="remove_${encrypter.encrypt(userInRole.id)}">Remove</a>
								</td>
							</tr>
							</#list>
						</tbody>
					</table>
				</div>
			</td>
		</tr>
	</tbody>
</table>