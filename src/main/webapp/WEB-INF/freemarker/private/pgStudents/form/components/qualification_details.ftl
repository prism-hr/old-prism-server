<#if model.applicationForm.qualifications?has_content>
	<#assign hasQualifications = true>
<#else>
	<#assign hasQualifications = false>
</#if> 
 
 <#import "/spring.ftl" as spring />
			
			<h2 class="empty">
				<span class="left"></span><span class="right"></span><span class="status"></span>
				Qualifications
        	</h2>
            <div>
            
            	<#if hasQualifications>
            
	            	<table class="existing">
		              	
		              	<colgroup>
		                	<col style="width: 30px" />
		                	<col />
		                	<col style="width: 80px" />
		                	<col />
		                	<col />
		                	<col style="width: 30px" />
		                </colgroup>
		              	
		              	<thead>
		                	<tr>
		                  	<th colspan="2">Qualification</th>
		                    <th>Grade</th>
		                    <th>Awarding Body</th>
		                    <th>Date Completed</th>
		                    <th colspan="2">&nbsp;</th>
		                  </tr>
		                </thead>
		                
		                <tbody>
		                
		                	<#list model.applicationForm.qualifications as qualification>
			                	<tr>
				                  	<td><a class="row-arrow" href="#">-</a></td>
				                  	<td>${qualification.type}</td>
				                  	<td>${qualification.grade}</td>
				                  	<td>${qualification.institution}</td>
				                  	<td>${qualification.award_date}</td>
				                  	<td><a class="button-delete" href="#">delete</a></td>
			                  	</tr>
			                  	
			                  	<input type="hidden" name="qualId" id="qualId" value="${qualification.id}"/>
			                  	
							</#list>
										
		                </tbody>
	              	</table>
              
              	</#if>
              
              <form>

	              	<div>
	                  
	                  	<!-- Provider -->
	                	<div class="row">
		                  	<span class="label">Provider</span>
		                    <span class="hint" data-desc="Tooltip demonstration."></span>
		                    <div class="field">
		                    	<input id="q_provider" class="full" type="text" placeholder="e.g. UCL" />
		                    </div>
	                  	</div>
	                  
	                  	<!-- Name (of programme) -->
	                	<div class="row">
		                  	<span class="label">Programme</span>
		                    <span class="hint" data-desc="Tooltip demonstration."></span>
		                    <div class="field">
		                    	<input id="q_name" class="full" type="text" placeholder="e.g. Civil Engineering" />
		                    </div>
	             		</div>
	                  
	                  	<!-- Start date -->
	                	<div class="row">
		                  	<span class="label">Start Date</span>
		                    <span class="hint" data-desc="Tooltip demonstration."></span>
		                    <div class="field">
			                    <input id="q_start_date" class="half date" type="text" value="" />
		                    </div>
	                 	</div>
	                
                  		<!-- Language (in which programme was undertaken) -->
                  		<div class="row">
                    		<span class="label">Language of Study</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                      			<select class="full">
                        			<option>English</option>
                      			</select>
                    		</div>
                  		</div>
                  
                  		<!-- Qualification level -->
	                  	<div class="row">
	                    	<span class="label">Level</span>
	                    	<span class="hint" data-desc="Tooltip demonstration."></span>
	                    	<div class="field">
	                      		<select class="full">
	                        		<option>School</option>
	                      		</select>
	                    	</div>
	                  	</div>

                  		<!-- Qualification type -->
                  		<div class="row">
                    		<span class="label">Type</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                      			<select class="full">
                        			<option>Employer</option>
                      			</select>
                    		</div>
                  		</div>

                  		<!-- Qualification grade -->
                  		<div class="row">
                    		<span class="label">Grade</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                      			<input id="q_grade" class="full" type="text" placeholder="e.g. 2.1, Distinction" />
                    		</div>
                  		</div>

                  		<!-- Qualification score -->
                  		<div class="row">
                    		<span class="label">Score</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                      			<input id="q_score" class="full" type="text" placeholder="e.g. 114" />
                    		</div>
                  		</div>
                  
                  		<!-- Award date -->
                  		<div class="row">
                    		<span class="label">Award Date</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                      			<input id="q_award_date" class="half date" type="text" value="" />
                    		</div>
                  		</div>

                  		<!-- Attachment / supporting document -->
                  		<div class="row">
                    		<span class="label">Supporting Document</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                      			<input id="q_attachment" class="full" type="text" value="" />
                      				<a class="button" href="#">Browse</a>
                      				<a class="button" href="#">Upload</a>
                      				<a class="button" href="#">Add Document</a>
                    		</div>  
                  		</div>
	                
	                </div>

		        	<div class="buttons">
		            	<a class="button" href="#">Cancel</a>
		                <button class="blue" type="submit" value="close">Save and Close</button>
		                <button id="qualificationsSaveButton" class="blue" type="submit" value="add">Save and Add</button>
	                </div>

			  </form>
		</div>
		
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/qualifications.js'/>"></script>
 