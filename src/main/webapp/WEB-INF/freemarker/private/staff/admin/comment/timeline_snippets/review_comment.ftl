<#if comment.decline>
<p>Declined to review.</p>
<#else>
<p>
	Willing to interview?
	<br />
	<label><input type="radio" disabled="disabled" <#if comment.willingToInterview?? && comment.willingToInterview?string == 'true'>checked="checked"</#if>/> Yes</label>
	<label><input type="radio" disabled="disabled" <#if comment.willingToInterview?? && comment.willingToInterview?string == 'false'>checked="checked"</#if>/> No</label>
</p>
<p>
	Is candidate suitable for UCL?<br />
	<label><input type="radio" disabled="disabled" <#if comment.suitableCandidate?? && comment.suitableCandidate?string == 'true'>checked="checked"</#if>/> Yes</label>
	<label><input type="radio" disabled="disabled" <#if comment.suitableCandidate?? && comment.suitableCandidate?string == 'false'>checked="checked"</#if>/> No</label>								
</p>
</#if>