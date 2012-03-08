 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Qualification
                        </h2>
                        <div>
                            <br/>
                            <div>
                             
                            <table cellspacing=10>
                                 <tr align=left><th>Type</th><th>Grade</th><th>Institution</th><th>Award Date</th></tr>
                                <#list model.applicationForm.qualifications as qualification>
                                <tr>
                                    <td>${qualification.qualification_type}</td>
                                    <td>${qualification.grade}</td>
                                    <td>${qualification.institution}</td>
                                    <td>${qualification.award_date?string('yyyy/MM/dd')}</td>
                               </tr>
                            </#list>
                            </table>
                             
                            </div>
                            <br/>
                        </div>
