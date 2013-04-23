
<#list comment.scores as score>
  <#if score.questionType == "TEXT" || score.questionType == "TEXTAREA">
    <p class="conditional_offer"><span/>
    <b>${score.question} </b>${score.textResponse}</p>
  </#if>
</#list>