 <#import "/spring.ftl" as spring />
 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Qualifications
                        </h2>
                        <div>
                            <br/>
							<#list model.qualifications as qualification >
							<table>
 							<tr><td>
                               Degree: <textarea cols="45" rows="1"  name="degree" id="degree" > ${qualification.degree}</textarea></td></tr>
                            	<tr><td>Institution: <textarea cols="45" rows="1"  name="institution" id="institution">${qualification.institution}</textarea></td></tr>
                            	<tr><td>Date:<textarea cols="45" rows="1" name="date_taken" id="date_taken">${qualification.date_taken}</textarea></td></tr>
                            	<tr><td>Grade: <textarea cols="45" rows="1"  name="grade" id="grade">${qualification.grade}</textarea></td></tr>
                             	<tr><td>	<input type="hidden" name="qualId" id="qualId" value="${qualification.id}"/></td></tr>
                             	</table>
                            </#list>
                            <br/>
                            <div class="buttons">
                                    <a class="button blue" href="#">Close</a>
                                    <a class="button blue" type="submit" id="qualificationsSaveButton">Save</a>
                            </div>
                        </div>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/qualifications.js'/>"></script>

