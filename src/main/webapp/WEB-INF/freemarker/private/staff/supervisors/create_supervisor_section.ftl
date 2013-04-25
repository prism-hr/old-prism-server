<#import "/spring.ftl" as spring />
	<h3 id="p_newSupervisor">Create New Supervisor</h3>																			

<div class="row">
	<label id="lbl_newSupervisorFirstName" class="plain-label" for="newSupervisorFirstName">Supervisor First Name<em>*</em></label> 
	<span class="hint" data-desc="<@spring.message 'assignSupervisor.firstName'/>"></span>
		<div class="field">
			<input class="full" type="text" name="newSupervisorFirstName" id="newSupervisorFirstName" value="${(supervisor.firstName?html)!}"/>
			<#if supervisor??>		
    			<@spring.bind "supervisor.firstName" /> 
    	 		<#list spring.status.errorMessages as error> 
                <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
                </#list>
	 		</#if>	
	 </div>
</div>

<div class="row">
	<label id="lbl_newSupervisorLastName" class="plain-label" for="newSupervisorLastName">Supervisor Last Name<em>*</em></label>
	<span class="hint" data-desc="<@spring.message 'assignSupervisor.lastName'/>"></span>
	<div class="field">
		<input class="full" type="text" name="newSupervisorLastName" id="newSupervisorLastName" value="${(supervisor.lastName?html)!}"/>
		<#if supervisor??>	
    		<@spring.bind "supervisor.lastName" /> 
    		<#list spring.status.errorMessages as error> 
            <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
            </#list>
		</#if>		                                      
	</div>
</div>

<div class="row">
	<label id="lbl_newSupervisorEmail" class="plain-label" for="newSupervisorEmail">Email<em>*</em></label>
	<span class="hint" data-desc="<@spring.message 'assignSupervisor.email'/>"></span>
	<div class="field">
		<input class="full" type="email"  name="newSupervisorEmail" id="newSupervisorEmail" value="${(supervisor.email?html)!}"/>			      
		<#if supervisor??>
        	<@spring.bind "supervisor.email" /> 
            <#list spring.status.errorMessages as error> 
            <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
            </#list>
        </#if>                                   
	</div>
	
</div>

<div class="row">
	<div class="field">
		<button class="btn" type="button" id="createSupervisor">Add</button>
	</div>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/autosuggest.js'/>"></script>
<script type="text/javascript">
    $(document).ready(function() {
        autosuggest($("#newSupervisorFirstName"), $("#newSupervisorLastName"), $("#newSupervisorEmail"));
    });
</script>