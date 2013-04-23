<#list scores as score>
  <div class="row multi-line score-row">
    <input type="hidden" class="question-type" value = "${score.questionType}"/>
    <input type="hidden" class="question" value = "${score.question}"/>
    <input type="hidden" class="question-required" value = "${customQuestions[score_index].required?string("true", "false")}"/>
    <label class="plain-label question-label" for="question_${score_index}">${score.question} <#if customQuestions[score_index].required> <em>*</em> </#if></label>
    
    <#if score.questionType == "TEXT">
      <div class="field">
        <input class="text-input" type="text" value="${score.textResponse!}"/>
        <@spring.bind "comment.scores[${score_index}].textResponse" />
        <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
        </#list>
      </div>
    <#elseif score.questionType == "TEXTAREA">
      <div class="field">
        <textarea class="input-xxlarge textarea-input" rows="15" cols="150">${score.textResponse!}</textarea>

        <@spring.bind "comment.scores[${score_index}].textResponse" />
        <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
        </#list>
      </div>
    </#if>
    
  </div>
</#list>
