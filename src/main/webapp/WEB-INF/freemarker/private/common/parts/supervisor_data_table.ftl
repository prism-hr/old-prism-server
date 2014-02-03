<#import "/spring.ftl" as spring />

				<table class="data-table">
				
					<thead>
						<tr>
							<th>Name</th>
							<th>Email</th>
							<th>Primary</th>
							<th>Aware</th>
							<th>Action</th>
							<th></th>
						</tr>
					</thead>
				
					<tfoot></tfoot>
					
					<tbody></tbody>
					
				</table>

				<div class="row">
					<label class="plain-label">Supervision</label>
					<span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.supervisor'/>"></span>
				</div>
					<!-- supervisor rows -->
                <div class="row">
		        	<label class="plain-label">Supervisor First Name<em>*</em></label>
		            <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.firstname'/>"></span>
		            	<div class="field">
		                	<input class="full" type="text" placeholder="First Name" id="supervisorFirstname" name="supervisorFirstname"/>
		                </div>
				</div>						
				<div class="row">
					<label class="plain-label">Supervisor Last Name<em>*</em></label>
		            <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.lastname'/>"></span>
		            <div class="field"> 
		            	<input class="full" type="text" placeholder="Last Name" id="supervisorLastname" name="supervisorLastname"/>
		            </div>
		        </div>
					
				<div class="row">
		        	<label class="plain-label">Supervisor Email<em>*</em></label>
		            <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.email'/>"></span>
		            <div class="field">
		             	<input class="full" type="email" placeholder="Email address" id="supervisorEmail" name="supervisorEmail"/>
		            </div>
		        </div>
				
		        <div class="row">
		        	<label class="plain-label">Is supervisor aware of your application?</label>
		            <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.awareOfApplication'/>"></span>
		            <input type="checkbox" name="awareSupervisorCB" id="awareSupervisorCB"/>
		            <input type="hidden" name="awareSupervisor" id="awareSupervisor"/>
				</div>			
				
				<button class="btn btn-primary" id="addSupervisor" type="button" name="add">Submit</button>


