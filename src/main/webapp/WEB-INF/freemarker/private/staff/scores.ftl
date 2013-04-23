<#list scores as score>
  <div class="row multi-line score-row">
    <input type="hidden" class="question-type" value = "${score.questionType}"/>
    <input type="hidden" class="question" value = "${score.question}"/>
    <input type="hidden" class="question-required" value = "${customQuestions[score_index].required?string("true", "false")}"/>
    <label class="plain-label question-label" for="question_${score_index}">${score.question} <#if customQuestions[score_index].required> <em>*</em> </#if></label>
    
    <div class="field">
      <#if score.questionType == "TEXT">
      
          <input class="text-input" type="text" value="${score.textResponse!}"/>
          <@spring.bind "comment.scores[${score_index}].textResponse" />
          <#list spring.status.errorMessages as error>
            <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
          </#list>
          
      <#elseif score.questionType == "TEXTAREA">
          
          <textarea class="textarea-input" rows="15" cols="150">${score.textResponse!}</textarea>
          <@spring.bind "comment.scores[${score_index}].textResponse" />
          <#list spring.status.errorMessages as error>
            <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
          </#list>
          
      <#elseif score.questionType == "DATE">
          
          <input class="full date date-input" type="text" value="${(score.dateResponse?string('dd MMM yyyy'))!}" />
          <@spring.bind "comment.scores[${score_index}].dateResponse" />
          <#list spring.status.errorMessages as error>
            <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
          </#list>
          
      </#if>
    </div>
    
  </div>
</#list>
