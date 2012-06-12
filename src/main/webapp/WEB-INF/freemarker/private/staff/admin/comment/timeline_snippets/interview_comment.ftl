<#if comment.decline>
<p>Declined to interview.</p>
<#else>
<h3>Willing to supervise?</h3>
<p>
	<em>
		<#if comment.willingToSupervise?? && comment.willingToSupervise?string == 'true'>Yes</#if>
		<#if comment.willingToSupervise?? && comment.willingToSupervise?string == 'false'>No</#if>
	</em>
<#--
	<label><input type="radio" disabled="disabled" <#if comment.willingToSupervise?? && comment.willingToSupervise?string == 'true'>checked="checked"</#if>/> Yes</label>
	<label><input type="radio" disabled="disabled" <#if comment.willingToSupervise?? && comment.willingToSupervise?string == 'false'>checked="checked"</#if>/> No</label>								
-->
</p>

<h3>Is candidate suitable for UCL?</h3>
<p>
	<em>
		<#if comment.suitableCandidate?? && comment.suitableCandidate?string == 'true'>Yes</#if>
	  <#if comment.suitableCandidate?? && comment.suitableCandidate?string == 'false'>No</#if>								
	</em>
<#--
	<label><input type="radio" disabled="disabled" <#if comment.suitableCandidate?? && comment.suitableCandidate?string == 'true'>checked="checked"</#if>/> Yes</label>
	<label><input type="radio" disabled="disabled" <#if comment.suitableCandidate?? && comment.suitableCandidate?string == 'false'>checked="checked"</#if>/> No</label>								
-->
</p>
</#if> 			