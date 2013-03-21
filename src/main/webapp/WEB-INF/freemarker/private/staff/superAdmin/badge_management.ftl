<section id="badgeSection" >

<#import "/spring.ftl" as spring />

<div class="row-group">
	<div class="row">
		<label class="plain-label">Programme<em>*</em></label>
		<span class="hint" data-desc="<@spring.message 'badge.programme'/>"></span>
		<div class="field">
			<select name="programme" id="programme" class="max">
				<option value="">Please select a program</option>
				<#list programs as program>
					<option value="${program.code}" <#if badge.program?? && badge.program.id == program.id> selected="selected"</#if>>${program.title?html}</option>
				</#list>
			</select>
		</div>
	</div>
	<@spring.bind "badge.program" /> 
        <#list spring.status.errorMessages as error>
        <div class="row">
            <div class="field">
                <span class="invalid">${error}</span>
            </div>
        </div>
    </#list>

	<div class="row">
		<label class="plain-label">Programme Homepage</label>
		<span class="hint" data-desc="<@spring.message 'badge.programmeHomepage'/>"></span>
		<div class="field">
			<input type="text" name="programhome" id="programhome" class="max" value="${(badge.programmeHomepage?html)!}" placeholder="e.g. http://www.ucl.ac.uk" />
		</div>
	</div>
	<@spring.bind "badge.programmeHomepage" /> 
        <#list spring.status.errorMessages as error>
        <div class="row">
            <div class="field">
                <span class="invalid">${error}</span>
            </div>
        </div>
    </#list>		

	<div class="row">
		<label class="plain-label">Project Title</label>
		<span class="hint" data-desc="<@spring.message 'badge.projectTitle'/>"></span>
		<div class="field">
		    <input id="project" name="project" class="full ui-autocomplete-input" type="text" value="${(badge.projectTitle?html)!}" autocomplete="off" role="textbox" aria-autocomplete="list" aria-haspopup="true">
		</div>
	</div>
	<@spring.bind "badge.projectTitle" /> 
        <#list spring.status.errorMessages as error>
        <div class="row">
            <div class="field">
                <span class="invalid">${error}</span>
            </div>
        </div>
    </#list>												
											
	<div class="row">
		<label class="plain-label">Closing Date</label>
		<span class="hint" data-desc="<@spring.message 'badge.closingDate'/>"></span>
		<div class="field">
			<input type="text" name="batchdeadline" id="batchdeadline" class="half date" />
		</div>
	</div>
	<@spring.bind "badge.closingDate" /> 
        <#list spring.status.errorMessages as error>
        <div class="row">
            <div class="field">
                <span class="invalid">${error}</span>
            </div>
        </div>
    </#list>	
</div><!-- .row-group -->


<div class="row-group">
	<label class="plain-label">Badge HTML</label>
	<span class="hint" data-desc="<@spring.message 'badge.html'/>"></span>
	<div class="field">
		<textarea readonly="readonly" id="html" rows="10" cols="70"></textarea>
	</div>
</div>

                                        
<div class="buttons">
    <button class="btn btn-primary" id="badgeSaveButton" type="button">Create</button>
</div>
			
</section>

<script type="text/javascript" src="<@spring.url '/design/default/js/superAdmin/badge_management.js'/>"></script>