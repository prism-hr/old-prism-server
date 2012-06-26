<#if comment.decline>
<p>Declined to act as interviewer on this occasion.</p>
<#else>
<h3 class="answer <#if comment.suitableCandidateForUcl?? && comment.suitableCandidateForUcl?string == 'true'>yes<#else>no</#if>">
	<span data-desc="<#if comment.suitableCandidateForUcl?? && comment.suitableCandidateForUcl?string == 'true'>Yes<#else>No</#if>"></span> Is the applicant suitable for postgraduate study at UCL?
</h3>
<h3 class="answer <#if comment.suitableCandidateForProgramme?? && comment.suitableCandidateForProgramme?string == 'true'>yes<#else>no</#if>">
	<span data-desc="<#if comment.suitableCandidateForProgramme?? && comment.suitableCandidateForProgramme?string == 'true'>Yes<#else>No</#if>"></span> Is the applicant suitable for their chosen postgraduate study programme?
</h3>
<h3 class="answer <#if comment.willingToSupervise?? && comment.willingToSupervise?string == 'true'>yes<#else>no</#if>">
	<span data-desc="<#if comment.willingToSupervise?? && comment.willingToSupervise?string == 'true'>Yes<#else>No</#if>"></span> Would you like to admit the applicant as their supervisor/programme director?
</h3>
</#if> 			