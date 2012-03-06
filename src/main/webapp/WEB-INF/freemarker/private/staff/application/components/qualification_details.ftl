 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Qualifications
                        </h2>
                        <div>
                            <br/>
                            <div>
                            <#list model.applicationForm.qualifications as qualification >
                            	${qualification.degree}
                            	${qualification.institution}
                            	${qualification.date_taken}
                            	${qualification.grade}
                            </#list>
                            </div>
                        </div>