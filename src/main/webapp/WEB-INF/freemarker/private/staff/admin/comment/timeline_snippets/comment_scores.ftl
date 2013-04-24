<#assign notProvided = "Not provided">
<#list comment.scores as score>
  <#if score.questionType == "TEXT" || score.questionType == "TEXTAREA">
    <p class="conditional_offer"><span/>
      <b>${score.question}</b>
      <#if score.textResponse??>
        ${score.textResponse}
      <#else>
        <i>${notProvided}</i>
      </#if>
    </p>
    
  <#elseif score.questionType == "DATE">
    <p class="conditional_offer"><span/>
      <b>${score.question}</b>
      <#if score.dateResponse??>
        ${score.dateResponse}
      <#else>
        <i>${notProvided}</i>
      </#if>
    </p>
  
  <#--
  <#elseif score.questionType == "DATE_RANGE">
   <p class="conditional_offer"><span/>
    <b>${score.question} </b>From ${score.dateResponse} to ${score.secondDateResponse}</p>
  -->  
    
  <#elseif score.questionType == "DROPDOWN">
    <p class="conditional_offer"><span/>
      <b>${score.question} </b>
      <#if score.textResponse??>
        <#list score.textResponse?split("|") as value>
          <p>${value}</p>
        </#list>
      <#else>
        <i>${notProvided}</i>
      </#if>
    </p>
    
  <#elseif score.questionType == "RATING">
    <p class="conditional_offer"><span/>
    <b>${score.question} </b>
      <#if score.ratingResponse??>
        ${score.ratingResponse}
      <#else>
        <i>${notProvided}</i>
      </#if>
    </p>
    
  </#if>
</#list>