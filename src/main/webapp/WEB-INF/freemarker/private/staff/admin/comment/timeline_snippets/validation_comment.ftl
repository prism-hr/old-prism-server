<h3>Is the applicant qualified for PhD entry to UCL?</h3>
<p>                          									           				
	<#list validationQuestionOptions as option>
	<label>
		<input type="radio" name="qualifiedForPhd" value="${option}" disabled="disabled" <#if comment.qualifiedForPhd?? && comment.qualifiedForPhd == option>checked="checked"</#if>/>
		${option.displayValue}
	</label>
	</#list>									
</p>

<h3>Does the applicant meeting the minimum required standard of English Language competence?</h3>
<ul>                       							
	<#list validationQuestionOptions as option>
	<li<#if !comment.englishCompentencyOk?? || !comment.englishCompentencyOk == option> class="grey-label"</#if>>
		${option.displayValue}
	</li>
	</#list>
</ul>

<h3>What is the applicant's fee status?</h3>
<p>
	<#list homeOrOverseasOptions as option>
	<label>
		<input type="radio" name="homeOrOverseas" value="${option}" disabled="disabled" <#if comment.homeOrOverseas?? && comment.homeOrOverseas == option>checked="checked"</#if>/>
		${option.displayValue}
	</label>
	</#list>
</p>