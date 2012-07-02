 <select name="hours" id="hours">
		<option value="">Hour..</option>
		<#list 1..23 as hour>
			<option value="${hour?string('00')}" <#if interview.timeHours?? && interview.timeHours == hour?string('00')> selected = "selected"</#if>> ${hour?string('00')}</option>          
		</#list>			
		       
</select>	
		<@spring.bind "interview.timeHours" /> 
		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
 <select name="minutes" id="minutes">
		<option value="">Minutes..</option>
		<#list 0..59 as minute>
			<option value="${minute?string('00')}"  <#if interview.timeMinutes?? && interview.timeMinutes == minute?string('00')> selected = "selected"</#if>>${minute?string('00')}</option>          
		</#list>        
</select>	
		<@spring.bind "interview.timeMinutes" /> 
		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>