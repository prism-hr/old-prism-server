 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Qualifications
                        </h2>
                        <div>
                            <br/>
                           
							<#list model.user.qualifications as qualification >
							<table>
							<tr><td>
                            	Degree: <input type="text" value="${qualification.degree}" name="degree" id="degree"/> </td></tr>
                            	<tr><td>Institution: <input type="text" value="${qualification.institution}" name="institution" id="institution"/></td></tr>
                            	<tr><td>Date: <input type="text" value="${qualification.date_taken}" name="date" id="date"/></td></tr>
                            	<tr><td>Grade: <input type="text" value="${qualification.grade}" name="grade" id="grade"/></td></tr>
                            	</table>
                            </#list>
                            <br/>
                            <div class="buttons">
                                    <a class="button blue" href="#">Close</a>
                                    <a class="button blue" type="submit" id="qualificationsSaveButton">Save</a>
                            </div>
                        </div>