<section class="folding violet">
<h2 class="tick open">
         <span class="left"></span><span class="right"></span><span class="status"></span>
                Programme
</h2>
                        <div>
                        
                        <div class="row">
                            <label class="label"><strong>Programme</strong></label>
                                <div class="field">
                                    ${model.programme.programmeDetailsProgrammeName!}
                                </div>
                        </div>
                        
                        <p></p>
                        
                        <div class="row">
                            <label class="label"><strong>Study Option</strong></label>
                                <div class="field">
                                    ${model.programme.programmeDetailsStudyOption!}
                                </div>
                        </div>
                        
                        <p></p>
                        
                        <div class="row">
                            <label class="label"><strong>Project</strong></label>
                                <div class="field">
                                    ${model.programme.programmeDetailsProjectName!}
                                </div>
                        </div>
                        
                        <p></p>
                         <div class="row">
                            <label class="label"><strong>Start Date</strong></label>
                                <div class="field">
                                    ${(model.programme.programmeDetailsStartDate?string('yyyy/MM/dd'))!}
                                </div>
                        </div>
                        
                        <p></p>
                        
                        <div class="row">
                            <label class="label"><strong>Referrer</strong></label>
                                <div class="field">
                                    ${model.programme.programmeDetailsReferrer!}
                                </div>
                        </div>
                        
                        <p></p>
                        
                        </div>
                      </section>