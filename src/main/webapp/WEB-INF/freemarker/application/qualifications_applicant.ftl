 <#import "/spring.ftl" as spring />
 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Qualifications
                        </h2>
                        <div>
                            <br/>
                            <table>
							<#list model.applicationForm.qualifications as qualification >
						
 							<tr>
 								<td>
                               Degree: <input type="text"  name="degree" id="degree" value= "${qualification.degree}" />
                               </td>
                              </tr>
                            	<tr><td>Institution:<input type="text"  name="institution" id="institution" value="${qualification.institution}"/></td></tr>
                            	<tr><td>Date:<input type="text" name="date_taken" id="date_taken" value="${qualification.date_taken}" /></td></tr>
                            	<tr><td>Grade: <input type="text"  name="grade" id="grade" value="${qualification.grade}"/></td></tr>
                             	<tr><td>	<input type="hidden" name="qualId" id="qualId" value="${qualification.id}"/></td></tr>
                             	
                            </#list>
                          <tr>
 								<td>
                               Degree: <input type="text"  name="degree" id="degree" value= "" />
                               </td>
                              </tr>
                            	<tr><td>Institution:<input type="text"  name="institution" id="institution" value=""/></td></tr>
                            	<tr><td>Date:<input type="text" name="date_taken" id="date_taken" value="" /></td></tr>
                            	<tr><td>Grade: <input type="text"  name="grade" id="grade" value=""/></td></tr>
                            </table>
                            <br/>
                            <div class="buttons">
                                    <a class="button blue" href="#">Close</a>
                                    <a class="button blue" type="submit" id="qualificationsSaveButton">Save</a>
                            </div>
                        </div>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/qualifications.js'/>"></script>

