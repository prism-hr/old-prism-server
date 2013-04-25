<#setting locale = "en_US">
<script type="text/javascript" src="<@spring.url '/design/default/js/scores.js' />"></script>

<#list scores as score>
<div class="scoring-questions">
  <div class="row multi-line score-row">
    <#assign originalQuestion = score.originalQuestion>
    <input type="hidden" class="question-type" value = "${score.questionType}"/>
    <input type="hidden" class="question" value = "${score.question}"/>
    <input type="hidden" class="question-required" value = "${originalQuestion.required?string("true", "false")}"/>
    <label class="plain-label question-label" for="question_${score_index}">${score.question} <#if originalQuestion.required> <em>*</em> </#if></label>

    <div class="field">
      <#if score.questionType == "TEXT">
      
          <input class="text-input max" type="text" value="${score.textResponse!}"/>
          
      <#elseif score.questionType == "TEXTAREA">
          
          <textarea class="textarea-input max" rows="15" cols="150">${score.textResponse!}</textarea>
          
      <#elseif score.questionType == "DATE">
          <input class="full date date-input" type="text" value="${(score.dateResponse?string('dd MMM yyyy'))!}" />
          
      <#--
      <#elseif score.questionType == "DATE_RANGE">
          <input class="full date date-input" type="text" value="${(score.dateResponse?string('dd MMM yyyy'))!}" />
          <input class="full date second-date-input" type="text" value="${(score.secondDateResponse?string('dd MMM yyyy'))!}" />
      -->
          
      <#elseif score.questionType == "DROPDOWN">
      
          <#assign multiple =  originalQuestion.isEnableMultipleSelection()?? && originalQuestion.isEnableMultipleSelection()>
          <#assign selectedOptions = (score.textResponse!"")?split("|") >
          <select class="dropdown-input max" <#if multiple>multiple</#if>>
            <#if !multiple>
              <option value="">Column...</option>
            </#if>
            <#list originalQuestion.options.option as option>
              <option value="${option}" <#if selectedOptions?seq_contains(option)>selected</#if>>${option}</option> 
            </#list>
          </select>
          
      <#elseif score.questionType == "RATING">
      
          <input class="rating-input" type="number" value="${score.ratingResponse!}" min="0" max="5" />
          
      </#if>
      
      <@spring.bind "${errorsContainerName}.scores[${score_index}]" />
      <#list spring.status.errorMessages as error>
        <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
      </#list>
    </div>
    
  </div>
</div>
</#list>

<script>registerBindPickers($('.scoring-questions'));</script>