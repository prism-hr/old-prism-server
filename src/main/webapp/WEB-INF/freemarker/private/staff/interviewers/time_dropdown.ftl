 <select name="hours" id="hours">
		<option value="">Hour..</option>
		<#list 1..23 as hour>
			<option value="${hour?string('00')}" <#if interview.timeParts[0]?? && interview.timeParts[0]== hour?string('00')> selected = "selected"</#if>> ${hour?string('00')}</option>          
		</#list>			
		       
</select>	

 <select name="minutes" id="minutes">
		<option value="">Minutes..</option>
		<#list 0..59 as minute>
			<option value="${minute?string('00')}"  <#if interview.timeParts[1]?? && interview.timeParts[1]== minute?string('00')> selected = "selected"</#if>>${minute?string('00')}</option>          
		</#list>        
</select>	

<#--
 <select name="format" id="format">
		<option value="">Format..</option>
		<option value="AM" <#if interview.timeParts[2]?? && interview.timeParts[2]== 'AM'> selected = "selected"</#if>>AM</option>               
		<option value="PM" <#if interview.timeParts[2]?? && interview.timeParts[2]== 'PM'> selected = "selected"</#if>>PM</option>               
</select>
-->