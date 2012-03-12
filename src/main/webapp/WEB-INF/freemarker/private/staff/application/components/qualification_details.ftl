 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Qualification
                        </h2>
                        <div>
                            <br/>
                            <div>
                             
                            <table cellspacing=10>
                                 <tr align=left><th>Type</th><th>Grade</th><th>Institution</th><th>Award Date</th>
                                 <th>Score</th> <th>Level</th> <th>Start Date</th> <th>Language</th> <th>Programme Name</th></tr>
                                <#list model.applicationForm.qualifications as qualification>
                                <tr>
                                    <td>${qualification.qualificationType}</td>
                                    <td>${qualification.qualificationGrade!}</td>
                                    <td>${qualification.qualificationInstitution}</td>
                                    <td>${(qualification.qualificationAwardDate?string('yyyy/MM/dd'))!}</td>
                                    <td>${qualification.qualificationScore!}</td>
                                    <td>${qualification.qualificationLevel}</td>
                                    <td>${(qualification.qualificationStartDate?string('yyyy/MM/dd'))!}</td>
                                    <td>${qualification.qualificationLanguage}</td>
                                    <td>${qualification.qualificationProgramName}</td>
                               </tr>
                            </#list>
                            </table>
                             
                            </div>
                            <br/>
                        </div>
