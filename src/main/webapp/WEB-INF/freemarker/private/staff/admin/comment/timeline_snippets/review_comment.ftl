<#if comment.decline>
  <p class="declined"><span></span>Declined to act as reviewer on this occasion.</p>
<#else>
  <h3 class="answer <#if comment.suitableCandidateForUcl?? && comment.suitableCandidateForUcl?string == 'true'>yes<#else>no</#if>">
  	<span data-desc="<#if comment.suitableCandidateForUcl?? && comment.suitableCandidateForUcl?string == 'true'>Yes<#else>No</#if>"></span>Is the applicant suitable for postgraduate study at UCL?
  </h3>
  
  <h3 class="answer <#if comment.suitableCandidateForProgramme?? && comment.suitableCandidateForProgramme?string == 'true'>yes<#else>no</#if>">
  	<span data-desc="<#if comment.suitableCandidateForProgramme?? && comment.suitableCandidateForProgramme?string == 'true'>Yes<#else>No</#if>"></span>Is the applicant suitable for their chosen postgraduate study programme?
  </h3>
  
  <h3 class="answer <#if comment.willingToInterview?? && comment.willingToInterview?string == 'true'>yes<#else>no</#if>">
  	<span data-desc="<#if comment.willingToInterview?? && comment.willingToInterview?string == 'true'>Yes<#else>No</#if>"></span>Would you like to interview the applicant? 
  </h3>
  
  <h3 class="answer <#if comment.willingToWorkWithApplicant?? && comment.willingToWorkWithApplicant?string == 'true'>yes<#else>no</#if>">
      <span data-desc="<#if comment.willingToWorkWithApplicant?? && comment.willingToWorkWithApplicant?string == 'true'>Yes<#else>No</#if>"></span>Would you like to supervise this applicant? 
  </h3>

  <div class="question-rating-wrapper">
    <p class="sQuestion">
      <span/>                         
    </p>
    <b>Applicant rating:</b>
    <#if comment.applicantRating??>
      <ul class="rating-list clearfix">
        <#if comment.applicantRating == 0>
          <li><i class="icon-thumbs-down hover"></i></li>
        <#else>
          <#list 1..comment.applicantRating as i>
            <li><i class="icon-star hover"></i></li>
          </#list>
        </#if>
      </ul>
    <#else>
        <i>Not Provided</i>
    </#if>
  </div>
  
</#if>

<#include "comment_scores.ftl"/>
