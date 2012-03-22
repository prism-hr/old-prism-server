 <#import "/spring.ftl" as spring />
 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Qualifications
                        </h2>
                        <div>
                            <br/>
							<#list model.applicationForm.qualifications as qualification >
						
                            <table>
 							<tr>
 								<td>
                             	Type
                               </td>
                            	<td>Grade</td>
                            	<td>Institution</td>
                            	<td>Award Date</td>
 							<tr>
 								<td>
                               	${qualification.type}
                               </td>
                            	<td>${qualification.grade}</td>
                            	<td>${qualification.institution}</td>
                            	<td>${qualification.award_date}</td>
                             	<input type="hidden" name="qualId" id="qualId" value="${qualification.id}"/></tr>
                             </table>	
                            </#list>
                           	Provider <input type="text" id="q_provider" value=""/> <br/>
                            Name <input type="text" id="q_name" value=""/> <br/>
                            Start Date <input type="text"  id="q_start_date" value=""/> <br/>
                            Termination Reason <input type="text" id="q_term_reason" value=""/> <br/>
                            Termination Date <input type="text"  id="q_term_date" value=""/> <br/>
                            Country <input type="text"  id="q_country" value=""/> <br/>
                            Language <input type="text"  id="q_language" value=""/> <br/>
                            Level <input type="text"  id="q_level" value=""/> <br/>
                            Type <input type="text"  id="q_type" value=""/> <br/>
                            Grade <input type="text"  id="q_grade" value=""/> <br/>
                            Score <input type="text"  id="q_score" value=""/> <br/>
                            Award Date <input type="text"  id="q_award_date" value=""/> <br/>
                            Attachment <input type="text"  id="q_attachment" value=""/> <br/>
                            <div class="buttons">
                                    <a class="button blue" href="#">Close</a>
                                    <a class="button blue" type="submit" id="qualificationsSaveButton">Save</a>
                            </div>
                        </div>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/qualifications.js'/>"></script>

