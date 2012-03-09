<#-- Assignments -->
<#import "/spring.ftl" as spring />

<#-- Programme Details Rendering -->


	<h2 class="tick">
		<span class="left"></span><span class="right"></span><span class="status"></span>
		Programme
	</h2>
	
	<div>
    	<form>
            
            <input type="hidden" name="id1" id="id1" value="${model.user.id?string("######")}"/>
            <input type="hidden" id="appId1" name="appId1" value="${model.applicationForm.id?string("######")}"/>
                
			<div>
            	
            	<!-- Programme name (disabled) -->
                <div class="row">
                	<label class="label">Programme</label>
                    <span class="hint" title="Tooltip demonstration."></span>
                    <div class="field">
                    	<input class="full" id="programmeDetailsProgrammeName" name="programmeDetailsProgrammeName" type="text" value="${model.applicationForm.project.program.title}" disabled="disabled" />
                    </div>
				</div>
                  
				<!-- Study option -->
                <div class="row">
                    <label class="label">Study Option</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                		<select class="full" id="programmeDetailsStudyOption" name="programmeDetailsStudyOption">
                		  <#list model.studyOptions as studyOption>
                              <option value="${studyOption}">${studyOption.freeVal}</option>               
                        </#list>
                      	</select>
                    </div>
				</div>

				<!-- Project -->
				<div class="row">
                    <label class="label">Project</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                		<input class="full" id="programmeDetailsProjectName" name="programmeDetailsProjectName" type="text" value="${model.applicationForm.project.title}" disabled="disabled"/>
                    </div>
				</div>
			
			</div>

            <div>
            	
            	<h3>Supervision</h3>
                  
                <!-- supervisor rows -->
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
	                        <th class="align-left"><input class="full" type="text" placeholder="Email address" /></th>
	                        <th><input type="radio" /></th>
	                        <th><input type="checkbox" /></th>
                      	</tr>
                      	<!-- end repeat -->
                    </tbody>
                    
				</table>
                
                <div class="row">
                	<a class="button" href="#">Add supervisor</a>
                </div>
			
			</div>

            <div>
            	<!-- Start date -->
                <div class="row">
                	<label class="label">Start Date</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <input class="full" type="date" id="programmeDetailsStartDate" name="programmeDetailsStartDate" value="${(model.programme.programmeDetailsStartDate?string('yyyy/MM/dd'))!}"/>
                </div>

                <!-- Referrer -->
                <div class="row">
                	<label class="label">Referrer</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                    	<select class="full" id="programmeDetailsReferrer" name="programmeDetailsReferrer">
                    	 <#list model.referrers as referrer>
                              <option value="${referrer}">${referrer.freeVal}</option>               
                        </#list>
                      	</select>
                    </div>
				</div>

			</div>

            <div class="buttons">
            	<a class="button blue" href="#">Close</a>
            	<#if !model.applicationForm.isSubmitted()>
                    <button class="blue" type="button" id="programmeSaveButton">Save</button>
                </#if>    
			</div>

		</form>
	</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/programme.js'/>"></script>