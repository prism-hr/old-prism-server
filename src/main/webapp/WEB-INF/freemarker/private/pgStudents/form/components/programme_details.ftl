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
                    	<input class="full" type="text" value="Programme of interest" disabled="disabled" />
                    </div>
				</div>
                  
				<!-- Study option -->
                <div class="row">
                    <label class="label">Study Option</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                		<select class="full">
	                    	<option>e.g. full time, part-time, distance</option>
                      	</select>
                    </div>
				</div>

				<!-- Project -->
				<div class="row">
                    <label class="label">Project</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                		<input class="full" type="text" placeholder="Project of interest" />
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
                    <input class="half date" type="text" value="" />
                </div>

                <!-- Referrer -->
                <div class="row">
                	<label class="label">Referrer</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                    	<select class="full">
	                    	<option>UCL graduate study website</option>
                      	</select>
                    </div>
				</div>

			</div>

            <div class="buttons">
            	<a class="button blue" href="#">Close</a>
                <button class="blue" type="submit">Save</button>
			</div>

		</form>
	</div>
