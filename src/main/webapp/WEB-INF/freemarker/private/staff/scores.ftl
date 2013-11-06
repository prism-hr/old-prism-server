<#import "/spring.ftl" as spring />
<#setting locale = "en_US">

<#if alertForScoringQuestions??>
	<div class="alert alert-info">
		<i class="icon-info-sign"></i>
		${alertForScoringQuestions}
	</div>
</#if>

<#list scores as score>
<div class="scoring-questions">
  <div class="row multi-line score-row">
          			
    <#assign originalQuestion = score.originalQuestion>
    <input type="hidden" class="question-type" value = "${score.questionType}"/>
    <input type="hidden" class="question" value = "${score.question}"/>
    <input type="hidden" class="question-required" value = "${originalQuestion.required?string("true", "false")}"/>
    <label class="plain-label question-label" for="question_${score_index}">${score.question}<#if originalQuestion.required><em>*</em></#if></label>
	<#if originalQuestion.tooltip??>
		<span class="hint" data-desc="${originalQuestion.tooltip}"></span>
	</#if>
    <div id="question_container_${score_index}" class="field">
      <#if score.questionType == "TEXT">
      
          <input id="question_${score_index}" class="text-input max" type="text" value="${score.textResponse!}"/>
          
      <#elseif score.questionType == "TEXTAREA">
          <#assign maxLength = originalQuestion.maxLength!50000>
          <textarea id="question_${score_index}" class="textarea-input max scoring-textarea" rows="6" maxlength=${maxLength?c}>${score.textResponse!}</textarea>
          
      <#elseif score.questionType == "DATE">
          <input id="question_${score_index}" class="full date date-input" type="text" value="${(score.dateResponse?string('dd MMM yyyy'))!}" />
          
      <#--
      <#elseif score.questionType == "DATE_RANGE">
          <input id="question_${score_index}" class="full date date-input" type="text" value="${(score.dateResponse?string('dd MMM yyyy'))!}" />
          <input class="full date second-date-input" type="text" value="${(score.secondDateResponse?string('dd MMM yyyy'))!}" />
      -->
          
      <#elseif score.questionType == "DROPDOWN">
      
          <#assign multiple =  originalQuestion.isEnableMultipleSelection()?? && originalQuestion.isEnableMultipleSelection()>
          <#assign selectedOptions = (score.textResponse!"")?split("|") >
          <select id="question_${score_index}" class="dropdown-input max" <#if multiple>multiple</#if>>
            <#if !multiple>
              <option value="">Column...</option>
            </#if>
            <#list originalQuestion.options.option as option>
              <option value="${option}" <#if selectedOptions?seq_contains(option)>selected</#if>>${option}</option> 
            </#list>
          </select>
          
      <#elseif score.questionType == "RATING">
      	  <ul class="rating-list clearfix">
          		<li><i class="icon-thumbs-down"></i></li>
          		<li><i class="icon-star-empty"></i></li>
          		<li><i class="icon-star-empty"></i></li>
          		<li><i class="icon-star-empty"></i></li>
          		<li><i class="icon-star-empty"></i></li>
          		<li><i class="icon-star-empty"></i></li>
          </ul>
          <input id="question_${score_index}" class="rating-input" type="number" value="${score.ratingResponse!}" min="0" max="5" />
          
      </#if>
      
      <#if errorsContainerName??>
	      <@spring.bind "${errorsContainerName}.scores[${score_index}]" />
	      <#list spring.status.errorMessages as error>
	        <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
	      </#list>
      </#if>
    </div>
    
  </div>
</div>
</#list>

<script>registerBindPickers($('.scoring-questions'));</script>