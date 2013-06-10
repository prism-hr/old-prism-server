<#assign notProvided = "Not provided">
     <#if comment?? && comment.scores?size &gt; 0>
     </div>
     <div class="box scores">
        <div class="score-results">
        	<#list comment.scores as score>
	            <div <#if score_index + 1 &gt; 3> class="hide-score" style="display:none;"</#if>>
	                <#if score.questionType == "TEXT" || score.questionType == "TEXTAREA">
	                    <p class="sQuestion">
	                        <span/>
	                        <b>${score.question}:</b>
	                        <#if score.textResponse?has_content>
	                            ${score.textResponse}
	                            <#else>
	                                <i>Not Provided</i>
	                            </#if>
	                    </p>
	
	                <#elseif score.questionType == "DATE">
	                    <p class="sQuestion">
	                        <span/>
	                        <b>${score.question}:</b>
	                        <#if score.dateResponse??>
	                            ${score.dateResponse?string('dd MMM yy')}
	                            <#else>
	                                <i>Not Provided</i>
	                            </#if>
	                    </p>
	
	                <#--
	                <#elseif score.questionType == "DATE_RANGE">
	                    <p class="sQuestion">
	                        <span/>
	                        <b>${score.question}:</b> From ${score.dateResponse} to ${score.secondDateResponse}
	                    </p>
	                    -->
	
	                <#elseif score.questionType == "DROPDOWN">
	                    <p class="sQuestion">
	                        <span/>
	                        <b>${score.question}:</b>
	                        <#if score.textResponse??>
	                            <#list score.textResponse?split("|") as value><#if value_index + 1 &gt; 1>, </#if>${value}</#list>
	                        <#else>
	                            <i>Not Provided</i>
	                        </#if>
	                    </p>
	                    
	
	                <#elseif score.questionType == "RATING">
						<div class="question-rating-wrapper">
							<p class="sQuestion">
		                        <span/>	                        
		                    </p>
		                    <b>${score.question}:</b>
	                        <#if score.ratingResponse??>
	                            <ul class="rating-list clearfix">
	                                <#if score.ratingResponse == 0>
	                                    <li><i class="icon-thumbs-down hover"></i></li>
	                                <#else>
	                                    <#list 1..score.ratingResponse as i>
	                                    <li><i class="icon-star hover"></i></li>
	                                    </#list>
	                                </#if>
	                            </ul>
	                        <#else>
	                            <i>Not Provided</i>
	                        </#if>
						</div>
	                </#if>
	            </div>
	        </#list>
	
	        <div class="hide-score-group" style="display:none;"></div>
	
	        <#if comment.scores?size &gt; 3>
	            <p>
	                <a class="expand more-scores" href="javascript:void(0);">Show more</a>
	            </p>
	        </#if>
         </div>
     </#if>
    
