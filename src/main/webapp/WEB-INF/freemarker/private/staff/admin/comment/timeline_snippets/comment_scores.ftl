<#assign notProvided = "Not provided">
    <div class="score-results">
        <#if comment.scores?size &gt; 0>
        	<h4></h4>
        	
        	<#list comment.scores as score>
	            <div <#if score_index + 1 &gt; 3> class="hide-score" style="display:none;"</#if>>
	                <#if score.questionType == "TEXT" || score.questionType == "TEXTAREA">
	                    <p class="sQuestion">
	                        <span/>
	                        <b>${score.question} </b>
	                        <#if score.textResponse??>
	                            ${score.textResponse}
	                            <#else>
	                                <i>${notProvided}</i>
	                            </#if>
	                    </p>
	
	                <#elseif score.questionType == "DATE">
	                    <p class="sQuestion">
	                        <span/>
	                        <b>${score.question}</b>
	                        <#if score.dateResponse??>
	                            ${score.dateResponse}
	                            <#else>
	                                <i>${notProvided}</i>
	                            </#if>
	                    </p>
	
	                <#--
	                <#elseif score.questionType == "DATE_RANGE">
	                    <p class="sQuestion">
	                        <span/>
	                        <b>${score.question} </b>From ${score.dateResponse} to ${score.secondDateResponse}
	                    </p>
	                    -->
	
	                <#elseif score.questionType == "DROPDOWN">
	                    <p class="sQuestion">
	                        <span/>
	                        <b>${score.question} </b>
	                        <#if score.textResponse??>
	                            <#list score.textResponse?split("|") as value><#if value_index + 1 &gt; 1>, </#if>${value}</#list>
	                        <#else>
	                            <i>${notProvided}</i>
	                        </#if>
	                    </p>
	                    
	
	                <#elseif score.questionType == "RATING">

	                    <p class="sQuestion">
	                        <span/>
	                        <b>${score.question} </b>
	                        
	                    </p>
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
                            <i>${notProvided}</i>
                        </#if>
                      
	                </#if>
	            </div>
	        </#list>
	
	        <div class="hide-score-group" style="display:none;"></div>
	
	        <#if comment.scores?size &gt; 3>
	            <p>
	                <a class="expand more-scores" href="javascript:void(0);">Show more</a>
	            </p>
	        </#if>
        </#if>
    </div>
