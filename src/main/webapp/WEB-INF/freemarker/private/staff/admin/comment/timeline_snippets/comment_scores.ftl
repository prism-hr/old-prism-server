<div class="score-results">
	<h4>Scoring</h4>
	<#list comment.scores as score>
		<div<#if score_index + 1 &gt; 3> class="hide-score" style="display:none;"</#if>>
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
			  	
			  	<ul>
			  		<#list score.textResponse?split("|") as value>
				   		<li>${value}</li>
				  	</#list> 
			  	</ul>
			    
		  	<#elseif score.questionType == "RATING">
				<p class="conditional_offer"><span/>
				<b>${score.question} </b>${score.ratingResponse}</p>
		  	</#if>
		</div>
	</#list>
	
	<div class="hide-score-group" style="display:none;"></div>
	
	<#if comment.scores?size &gt; 3>
		<p>
			<a id="more-scores" href="javascript:void(0);">more</a>
		</p>
	</#if>
</div>