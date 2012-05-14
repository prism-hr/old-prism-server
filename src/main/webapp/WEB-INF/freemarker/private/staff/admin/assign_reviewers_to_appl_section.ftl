<#import "/spring.ftl" as spring />
<section id="assignReviewersToAppSection" class="folding violet">
	<@spring.bind "applicationForm.*" /> 
	<@spring.bind "availableReviewers.*" /> 
	<@spring.bind "applicationReviewers.*" />
	<@spring.bind "programme.*" /> 
	<@spring.bind "unsavedReviewers.*" />
	<div>
		<form>
			<div>
				<!-- Available reviewers in programme -->
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

				<!-- Available Reviewer Buttons -->
				<div class="row reviewer-buttons">
					<div class="field">
						<span>
							<button class="blue" type="submit" id="addReviewerBtn">Add</button>
							<button class="blue" type="submit" id="removeReviewerBtn">Remove</button>
						</span>
					</div>
				</div>

				<!-- Already reviewers of this application -->
				<div class="row">
					<div class="field">
						<p>
							<strong>Selected Reviewers</strong>
						</p>
						<select id="assignedReviewers" multiple="multiple">
						<#list applicationReviewers as reviewer>
							<option disabled="disabled" value="${reviewer.id?string('#####')}">
								${reviewer.firstName?html} ${reviewer.lastName?html} <#if !reviewer.enabled> - Pending</#if>
							</option> 
						</#list> 
						<#list unsavedReviewers as unsaved> 
							<#if applicationReviewers?seq_index_of(unsaved) < 0>
								<option value="${unsaved.id?string('#####')}">
									${unsaved.firstName?html} ${unsaved.lastName?html} <#if !unsaved.enabled> - Pending</#if>
								</option> 
							</#if> 
						</#list>
						</select>
					</div>
				</div>
			</div>
			<div>
				<p>${message!}</p>
				<p>
					<strong>Create New Reviewer</strong>
				</p>
				<!-- Supervisor First Name -->
				<div class="row">
					<label class="label normal">Reviewer First Name<em>*</em></label> 
					<span class="hint" data-desc="Tooltip demonstration."></span>
					<div class="field">
						<input class="full" type="text" name="newReviewerFirstName" id="newReviewerFirstName" />
					</div>
					<@spring.bind "uiReviewer.firstName" /> 
					<#list spring.status.errorMessages as error> 
					  <span class="invalid">${error}</span>
					</#list>
				</div>
				<!-- Supervisor Last Name -->
				<div class="row">
					<label class="label normal">Reviewer Last Name<em>*</em></label> 
					<span class="hint" data-desc="Tooltip demonstration."></span>
					<div class="field">
						<input class="full" type="text" name="newReviewerLastName" id="newReviewerLastName" />
					</div>
					<@spring.bind "uiReviewer.lastName" /> 
					<#list spring.status.errorMessages as error> 
					  <span class="invalid">${error}</span>
					</#list>
				</div>
				<!-- Supervisor Email -->
				<div class="row">
					<label class="label normal">Reviewer Email<em>*</em></label> 
					<span class="hint" data-desc="Tooltip demonstration."></span>
					<div class="field">
						<input class="full" type="text" name="newReviewerEmail" id="newReviewerEmail" />
					</div>
					<@spring.bind "uiReviewer.email" /> 
					<#list spring.status.errorMessages as error> 
					  <span class="invalid">${error}</span>
					</#list>
				</div>
				<div class="row">
					<div class="field">
						<button class="blue" type="submit" id="createReviewer">Create reviewer</button>
						<button class="blue" type="submit" id="moveToReviewBtn">Continue</button>
					</div>
				</div>
			</div>
			<input type="hidden" id="applicationId" name="applicationId"
				value="${applicationForm.id?string(" ######")}"/>
		</form>
	</div>
</section>