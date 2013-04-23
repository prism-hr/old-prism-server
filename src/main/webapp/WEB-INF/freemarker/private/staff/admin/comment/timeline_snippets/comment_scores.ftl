
<#list comment.scores as score>
  <#if score.questionType == "TEXT" || score.questionType == "TEXTAREA">
    <p class="conditional_offer"><span/>
    <b>${score.question} </b>${score.textResponse}</p>
    
  <#elseif score.questionType == "DATE">
    <p class="conditional_offer"><span/>
    <b>${score.question} </b>${score.dateResponse}</p>
  
  <#elseif score.questionType == "DATE_RANGE">
   <p class="conditional_offer"><span/>
    <b>${score.question} </b>From ${score.dateResponse} to ${score.secondDateResponse}</p>
    
  <#elseif score.questionType == "DROPDOWN">
    <p class="conditional_offer"><span/>
      <b>${score.question} </b>
      
      <#list score.textResponse?split("|") as value>
       <p>${value}</p>
      </#list> 
    </p>
    
  <#elseif score.questionType == "RATING">
    <p class="conditional_offer"><span/>
    <b>${score.question} </b>${score.ratingResponse}</p>
    
  </#if>
</#list>