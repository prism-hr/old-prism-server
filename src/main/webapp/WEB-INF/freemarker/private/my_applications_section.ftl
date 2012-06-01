<table class="data" border="0" >
					          	<colgroup>
					            	<col style="width: 24px" />
					            	<col style="width: 20%" />
					            	<col/>
					            	<col/>
					            	<col style="" />
					            	<col style="width: 90px" />
					            	<col style="width: 40px" />
					            </colgroup>
											<thead>
												<tr>
													<th scope="col">&nbsp;</th>
													<th scope="col">Name</th>
													<th scope="col">Programme</th>					
													<th scope="col">Status</th>
													<th scope="col">Actions</th>
													<th scope="col">Submitted</th>					                
													<th class="centre" scope="col">
														<input type="checkbox" name="select-all" id="select-all" />
													</th>
												</tr>
											</thead>
					            <tbody>
					            	<#list applications as application>
							        	<tr id="row_${application.applicationNumber}" name="applicationRow">
							                <td><a class="row-arrow" href="#">&gt;</a></td>
							                <td class="applicant-name">
																${application.applicant.firstName} ${application.applicant.lastName}
																<#if !user.isInRole('APPLICANT')><span class="applicant-id">${application.applicationNumber}</span></#if>
															</td>
							                <td class="program-title">${application.program.code} - ${application.program.title}</td>								                
							               	<td class="status">
																<span class="icon-status ${application.status.displayValue()?lower_case?replace(' ','-')}" data-desc="${application.status.displayValue()}">${application.status.displayValue()}</span>
															</td>
							                <td class="centre">
							                	<select class="actionType" name="app_[${application.applicationNumber}]">
							                		<option>Select...</option>
							                		<option value="view">View</option>
							                		<option value="print">Print</option>
							                	    <#if user.isInRoleInProgram('APPROVER', application.program) && application.isInState('APPROVAL')>
							                	    	<option value="approve">Approve</option>
							                	    	<option value="reject">Reject</option>
      												</#if>
      												<#if  user.hasAdminRightsOnApplication(application) && application.isInState('VALIDATION')> 
									    				<option value="validate">Validate</option>
									      			</#if>
									      			<#if user.hasAdminRightsOnApplication(application) && application.isInState('REVIEW')> 
									    				<option value="validate">Evaluate reviews</option>
									      			</#if>
									      			<#if user.hasAdminRightsOnApplication(application) && application.isInState('INTERVIEW')> 
									    				<option value="validate">Evaluate interview feedback</option>
									      			</#if>
									    			<#if !user.isInRole('APPLICANT') && !user.isRefereeOfApplicationForm(application)>
								    					<option value="comment">Comment</option>								    				
								      				</#if>      												
							                	    <#if (user.isReviewerInLatestReviewRoundOfApplicationForm(application)&& application.isInState('REVIEW') && user.hasRespondedToProvideReviewForApplication(application))>
      													<option value="assignReviewer">Assign Reviewer</option>
        		  									</#if>							                	   
									    			<#if (user.isReviewerInLatestReviewRoundOfApplicationForm(application) && application.isInState('REVIEW') && !user.hasRespondedToProvideReviewForApplicationLatestRound(application))> 
								    					<option value="review">Add Review</option>								    				
								      				</#if>      												
									    			<#if user.isInterviewerOfApplicationForm(application) && application.isInState('INTERVIEW') && !user.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(application)> 
								    					<option value="interviewFeedback">Add Interview Feedback</option>								    				
								      				</#if>      												
								      				<#if (user.isRefereeOfApplicationForm(application) && application.isSubmitted() && !application.isDecided() )>
								    					<option value="reference">Add Reference</option>
								      				</#if>      												
								      				<#if (user.isInRole('APPLICANT') && application.isSubmitted() && !application.isDecided() && !application.isWithdrawn())>
								    					<option value="withdraw">Withdraw</option>
								      				</#if>      												
							                  	</select>

							                </td>
							                <td class="centre">${(application.submittedDate?string("dd MMM yyyy"))!}</td>							  
							                <td class="centre"><input type="checkbox" name="appDownload" id="appDownload_${application.applicationNumber}"/></td>
						              	</tr>
					              	</#list>
					            </tbody>
				          </table>
