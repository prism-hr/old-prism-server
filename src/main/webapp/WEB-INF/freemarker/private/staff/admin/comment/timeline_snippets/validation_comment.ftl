<h3 class="answer <#if comment.qualifiedForPhd??>${comment.qualifiedForPhd?string?lower_case}</#if>">
	<span data-desc="${comment.englishCompentencyOk?string!"Unsure"}></span> Is the applicant qualified for PhD entry to UCL?
</h3>

<h3 class="answer <#if comment.englishCompentencyOk??>${comment.englishCompentencyOk?string?lower_case}</#if>">
	<span data-desc="${comment.englishCompentencyOk?string!"Unsure"}></span> Does the applicant meeting the minimum required standard of English Language competence?
</h3>

<h3 class="answer <#if comment.homeOrOverseas??>${comment.homeOrOverseas?string?lower_case}</#if>">
	<span data-desc="${comment.englishCompentencyOk?string!"Unsure"}></span> What is the applicant's fee status?
</h3>

<#--
<h3>Is the applicant qualified for PhD entry to UCL?</h3>
<ul>                       							
	<#list validationQuestionOptions as option>
	<li<#if comment.qualifiedForPhd?? && comment.qualifiedForPhd != option> class="grey-label"</#if>>
		${option.displayValue}
	</li>
	</#list>
</ul>

<h3>Does the applicant meeting the minimum required standard of English Language competence?</h3>
<ul>                       							
	<#list validationQuestionOptions as option>
	<li<#if comment.englishCompentencyOk?? && comment.englishCompentencyOk != option> class="grey-label"</#if>>
		${option.displayValue}
	</li>
	</#list>
</ul>

<h3>What is the applicant's fee status?</h3>
<ul>
	<#list homeOrOverseasOptions as option>
	<li<#if comment.homeOrOverseas?? && comment.homeOrOverseas != option> class="grey-label"</#if>>
		${option.displayValue}
	</li>
	</#list>
</ul>
-->