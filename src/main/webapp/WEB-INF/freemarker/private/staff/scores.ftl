<#list scores as score>
  <#if score.questionType == "TEXT">
    <div class="row multi-line score-row">
      <input type="hidden" class="question-type" value = "TEXT"/>
      <input type="hidden" class="question" value = "${score.question}"/>
      <input type="hidden" class="question-required" value = "${customQuestions[score_index].required?string("true", "false")}"/>
      <label class="plain-label question-label" for="question_${score_index}">${score.question} <#if customQuestions[score_index].required> <em>*</em> </#if></label>
      <div class="field">
        <input class="text-input" type="text" value="${score.textResponse!}"/>
        <@spring.bind "comment.scores[${score_index}].textResponse" />
        <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
        </#list>
      </div>
    </div>
  </#if>
</#list>
