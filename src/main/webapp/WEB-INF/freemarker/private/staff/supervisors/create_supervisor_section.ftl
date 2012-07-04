<#import "/spring.ftl" as spring />
<p>
	<strong>Create New Supervisor</strong>											
</p>									

<div class="row">
	<label class="plain-label">Supervisor First Name<em>*</em></label> 
	<span class="hint" data-desc="<@spring.message 'assignSupervisor.firstName'/>"></span>
		<div class="field">
			<input class="full" type="text" name="newSupervisorFirstName" id="newSupervisorFirstName" value="${(supervisor.firstName?html)!}"/>		
			<@spring.bind "supervisor.firstName" /> 
	 		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>	
	 </div>
</div>

<div class="row">
	<label class="plain-label">Supervisor Last Name<em>*</em></label>
	<span class="hint" data-desc="<@spring.message 'assignSupervisor.lastName'/>"></span>
	<div class="field">
		<input class="full" type="text" name="newSupervisorLastName" id="newSupervisorLastName" value="${(supervisor.lastName?html)!}"/>	
		<@spring.bind "supervisor.lastName" /> 
		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>		                                      
	</div>
	
</div>

<div class="row">
	<label class="plain-label">Email<em>*</em></label>
	<span class="hint" data-desc="<@spring.message 'assignSupervisor.email'/>"></span>
	<div class="field">
		<input class="full" type="text"  name="newSupervisorEmail" id="newSupervisorEmail" value="${(supervisor.email?html)!}"/>			      
		<@spring.bind "supervisor.email" /> 
	<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>                                   
	</div>
	
</div>

<div class="row">
	<div class="field">
		<button class="blue" type="button" id="createSupervisor">Add</button>
	</div>
</div>