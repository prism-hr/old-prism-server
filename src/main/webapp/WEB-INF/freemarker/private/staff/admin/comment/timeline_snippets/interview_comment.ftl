<#if comment.decline>
<p>Declined to interview.</p>
<#else>
<p>Willing to supervise?</p>
	<input type="radio" disabled="disabled" <#if comment.willingToSupervise?? && comment.willingToSupervise?string == 'true'>checked="checked"</#if>/><label>Yes</label>
	<input type="radio" disabled="disabled" <#if comment.willingToSupervise?? && comment.willingToSupervise?string == 'false'>checked="checked"</#if>/><label>No</label>								
</p>
<p>Is candidate suitable for UCL?</p>
	<input type="radio" disabled="disabled" <#if comment.suitableCandidate?? && comment.suitableCandidate?string == 'true'>checked="checked"</#if>/><label>Yes</label>
	<input type="radio" disabled="disabled" <#if comment.suitableCandidate?? && comment.suitableCandidate?string == 'false'>checked="checked"</#if>/><label>No</label>								
</p>
</#if> 			