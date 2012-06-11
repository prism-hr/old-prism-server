<p>Is the applicant qualified for PhD entry to UCL?</p>
<p>                          									           				
	<#list validationQuestionOptions as option>
	<input type="radio" name="qualifiedForPhd" value="${option}" disabled="disabled" <#if comment.qualifiedForPhd?? && comment.qualifiedForPhd == option>checked="checked"</#if>/><label> ${option.displayValue}</label>
	</#list>									
</p>

<p>Does the applicant meeting the minimum required standard of English Language competence?</p>
<p>                       							
									            				
	<#list validationQuestionOptions as option>
	<label><input type="radio" name="englishCompentencyOk" value="${option}"  disabled="disabled" <#if comment.englishCompentencyOk?? &&  comment.englishCompentencyOk == option>checked="checked"</#if>/> ${option.displayValue}</label>
	</#list>
	
</p>

<p>What is the applicant's fee status?</p>
<p>                          															  				
	<#list homeOrOverseasOptions as option>
	<label><input type="radio" name="homeOrOverseas" value="${option}" disabled="disabled" <#if comment.homeOrOverseas?? && comment.homeOrOverseas == option>checked="checked"</#if>/> ${option.displayValue}</label>
	</#list>

</p>