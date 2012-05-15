<#import "/spring.ftl" as spring />

<@spring.bind "availableReviewers.*" /> 
<div class="row">
	<label class="label">Reviewers</label>
	<div class="field">
		<p>
			<strong>Available Reviewers</strong>
		</p>
		<select id="reviewers" multiple="multiple"> 
		<#list availableReviewers as reviewer>
			<option value="${reviewer.id?string('#####')}">
				${reviewer.firstName?html} ${reviewer.lastName?html} <#if !reviewer.enabled> - Pending</#if>
			</option> 
		</#list>
		</select>
	</div>
</div>