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
                              <option value="${studyOption.freeVal}">${studyOption.freeVal}</option>               
                        </#list>
                      	</select>
                      	<input type="hidden" id="programmeDetailsStudyOptionDP" value="${model.programme.programmeDetailsStudyOption!}"/>
                      	<#if model.hasError('programmeDetailsStudyOption')>                            
                                <span style="color:red;"><@spring.message  model.result.getFieldError('programmeDetailsStudyOption').code /></span>                           
                        </#if>
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
	                        <th class="align-left"><input class="full" type="text" placeholder="Email address" name="supervisor_email"/></th>
	                        <th><input type="checkbox" name="supervisor_primary"/></th>
	                        <th><input type="checkbox" /></th>
                      	</tr>
                      	<!-- end repeat -->
                    </tbody>
                    
				</table>
                 <a id="addSuperVisor" class="button" style="width: 110px;">Add Supervisor</a>
			</div>

            <div>
            	<!-- Start date -->
                <div class="row">
                	<label class="label">Start Date</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <input class="full date" type="date" id="programmeDetailsStartDate" name="programmeDetailsStartDate" value="${(model.programme.programmeDetailsStartDate?string('dd-MMM-YYYY'))!}"/>
                    <#if model.hasError('programmeDetailsStartDate')>                            
                          <span style="color:red;"><@spring.message  model.result.getFieldError('programmeDetailsStartDate').code /></span>                           
                    </#if>
                </div>

                <!-- Referrer -->
                <div class="row">
                	<label class="label">Referrer</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <input type="hidden" id="programmeDetailsReferrerDP" value="${model.programme.programmeDetailsReferrer!}"/>
                    <div class="field">
                    	<select class="full" id="programmeDetailsReferrer" name="programmeDetailsReferrer">
                    	 <#list model.referrers as referrer>
                              <option value="${referrer.freeVal}">${referrer.freeVal}</option>               
                        </#list>
                      	</select>
                      	 <#if model.hasError('programmeDetailsReferrer')>                            
                            <span style="color:red;"><@spring.message  model.result.getFieldError('programmeDetailsReferrer').code /></span>                           
                         </#if>
                    </div>
				</div>

			</div>

            <div class="buttons">
            	<#if !model.applicationForm.isSubmitted()>
            	<a class="button" type="button" id="programmeCancelButton" name="programmeCancelButton">Cancel</a>
                    <button class="blue" type="button" id="programmeSaveCloseButton">Save and Close</button>
                    <button class="blue" type="button" id="programmeSaveAddButton">Save and Add</button>
                </#if>    
			</div>

		</form>
	</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/programme.js'/>"></script>