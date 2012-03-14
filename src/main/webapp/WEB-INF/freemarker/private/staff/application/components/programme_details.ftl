<#if model.applicationForm.project?has_content>
	<#assign hasProject = true>
<#else>
	<#assign hasProject = false>
</#if>

<#if model.programme?has_content>
	<#assign hasProgramme = true>
<#else>
	<#assign hasProgramme = false>
</#if>

<#-- Assignments -->
<#import "/spring.ftl" as spring />

<#-- Programme Details Rendering -->


	<h2 class="tick">
		<span class="left"></span><span class="right"></span><span class="status"></span>
		Programme
	</h2>
	
	<div>
    	<form>
                
			<div>
            	
            	<!-- Programme name (disabled) -->
                <div class="row">
                	<label class="label">Programme</label>
                    <span class="hint" title="Tooltip demonstration."></span>
                    <div class="field">
                    	${model.applicationForm.project.program.title}
                    </div>
				</div>
                  
				<!-- Study option -->
                <div class="row">
                    <label class="label">Study Option</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                    	${model.programme.programmeDetailsStudyOption!}
                    </div>
				</div>

				<!-- Project -->
				<div class="row">
                    <label class="label">Project</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                		${model.applicationForm.project.title}
                    </div>
				</div>
			
			</div>

            <div>
            	
            	<h3>Supervision</h3>
                  
                <!-- supervisor rows -->
                <!-- Add freemarker list to pull the rows for Supervisors -->
                <table class="multiples">
                	<colgroup>
                    	<col />
                      	<col style="width: 80px;" />
                      	<col style="width: 80px;" />
                    </colgroup>
                    
                    <thead>
                    	<tr>
	                        <th class="align-left">Supervisor</th>
	                        <th>Primary</th>
	                        <th>Aware</th>
                    	</tr>
                    </thead>
                    
                    <tbody>
						<!-- repeat these rows for every existing supervisor. -->
                      	<tr>
	                        <th class="align-left">
	                        	<!-- Add freemarker expression to pull the content for Supervisor email -->
							</th>
	                        <th>
	                        	<!-- Add freemarker expression to pull the content for value property -->
	                        	<input type="checkbox" name="supervisor_primary"/ value="" disabled="disabled">
	                        </th>
	                        <th>
	                        	<!-- Add freemarker expression to pull the content for value property -->
	                        	<input type="checkbox" value="" disabled="disabled"/>
	                        </th>
                      	</tr>
                      	<!-- end repeat -->
                    </tbody>
                    
				</table>
			</div>

            <div>
            	<!-- Start date -->
                <div class="row">
                	<label class="label">Start Date</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    ${(model.programme.programmeDetailsStartDate?string('dd-MMM-YYYY'))!}
                </div>

                <!-- Referrer -->
                <div class="row">
                	<label class="label">Referrer</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
						${model.programme.programmeDetailsReferrer!}                    
					</div>
				</div>

			</div>

            <div class="buttons">
				<button class="blue" type="button">Close</button>
			</div>

		</form>
	</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/programme.js'/>"></script><section class="folding violet">