<#if comment.decline>
<p>Declined to act as interviewer on this occasion.</p>
<#else>
<h3 class="answer <#if comment.willingToSupervise?? && comment.willingToSupervise?string == 'true'>yes<#else>no</#if>">
	<span data-desc="<#if comment.willingToSupervise?? && comment.willingToSupervise?string == 'true'>Yes<#else>No</#if>"></span> Willing to supervise?
</h3>
<h3 class="answer <#if comment.suitableCandidate?? && comment.suitableCandidate?string == 'true'>yes<#else>no</#if>">
	<span data-desc="<#if comment.suitableCandidate?? && comment.suitableCandidate?string == 'true'>Yes<#else>No</#if>"></span> Is candidate suitable for UCL?
</h3>
</#if> 			