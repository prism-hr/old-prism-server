<#import "/spring.ftl" as spring />
										


<div class="row-group">
	<div class="row">
		<label class="plain-label">Programme<em>*</em></label>
		<span class="hint" data-desc="<@spring.message 'badge.programme'/>"></span>
		<div class="field">
			<select name="programme" id="programme" class="max">
				<option value="">Please select a program</option>
				<#list programs as program>
					<option value="${program.code}">${program.title?html}</option>
				</#list>
			</select>
		</div>
	</div>

	<div class="row">
		<label class="plain-label">Programme Homepage</label>
		<span class="hint" data-desc="<@spring.message 'badge.programmeHomepage'/>"></span>
		<div class="field">
			<input type="text" name="programhome" id="programhome" class="max" placeholder="e.g. http://www.ucl.ac.uk" />
		</div>
	</div>		

	<div class="row">
		<label class="plain-label">Project Title</label>
		<span class="hint" data-desc="<@spring.message 'badge.projectTitle'/>"></span>
		<div class="field">
			<input type="text" name="project" id="project" class="max" />
		</div>
	</div>												
											
	<div class="row">
		<label class="plain-label">Closing Date</label>
		<span class="hint" data-desc="<@spring.message 'badge.closingDate'/>"></span>
		<div class="field">
			<input type="text" name="batchdeadline" id="batchdeadline" class="half date" />
		</div>
	</div>
	
</div><!-- .row-group -->

<div class="row-group">
	<label class="plain-label">Badge HTML</label>
	<span class="hint" data-desc="<@spring.message 'badge.html'/>"></span>
	<div class="field">
		<textarea readonly="readonly" id="html" rows="15" cols="70"></textarea>
	</div>
</div>

								