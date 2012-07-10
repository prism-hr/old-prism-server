<#if comment.decline>
<p class="declined"><span></span>Declined to act as reviewer on this occasion.</p>
<#else>
<h3 class="answer <#if comment.suitableCandidateForUcl?? && comment.suitableCandidateForUcl?string == 'true'>yes<#else>no</#if>">
	<span data-desc="<#if comment.suitableCandidateForUcl?? && comment.suitableCandidateForUcl?string == 'true'>Yes<#else>No</#if>"></span> Is the applicant suitable for postgraduate study at UCL?
</h3>

<h3 class="answer <#if comment.suitableCandidateForProgramme?? && comment.suitableCandidateForProgramme?string == 'true'>yes<#else>no</#if>">
	<span data-desc="<#if comment.suitableCandidateForProgramme?? && comment.suitableCandidateForProgramme?string == 'true'>Yes<#else>No</#if>"></span> Is the applicant suitable for their chosen postgraduate study programme?
</h3>

<h3 class="answer <#if comment.willingToInterview?? && comment.willingToInterview?string == 'true'>yes<#else>no</#if>">
	<span data-desc="<#if comment.willingToInterview?? && comment.willingToInterview?string == 'true'>Yes<#else>No</#if>"></span> Would you like to interview the applicant with a view to working with them? 
</h3>

</#if>