 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Qualification
                        </h2>
                        <div>
                            <br/>
                            <div>
                             
                            <table cellspacing=10>
                                 <tr align=left><th>Type</th><th>Value</th><th>Award Date</th></tr>
                                <#list model.applicationForm.qualifications as qualification>
                                <tr>
                                    <td>${qualification.institution}</td>
                                    <td>${qualification.name_of_programme}</td>
                                    <td>${qualification.level}</td>
                                    <td>${qualification.qualification_type}</td>
                                    <td>${qualification.grade}</td>
                                    <td>${qualification.score}</td>
                                    <td>${qualification.start_date?string('yyyy/MM/dd')}</td>
                                    <td>${qualification.language_of_study}</td>
                                    <td>${qualification.award_date?string('yyyy/MM/dd')}</td>
                               </tr>
                            </#list>
                            </table>
                             
                            </div>
                            <br/>
                        </div>
