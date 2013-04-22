<#list scores as score>
  <#if score.questionType == "TEXT">
    <div class="row multi-line score-row">
      <input type="hidden" class="question-type" value = "TEXT"/>
      <input type="hidden" class="question" value = "${score.question}"/>
      <label class="plain-label question-label" for="question_${score_index}">${score.question} <#if customQuestions[score_index].required> <em>*</em> </#if></label>
      <div class="field">
        <input class="text-input" type="text" value="${score.textResponse!}"/>
      </div>
    </div>
  </#if>
</#list>
