<script type="text/javascript" src="<@spring.url '/design/default/js/prospectus/project_configuration.js' />"></script>

<section class="form-rows">
    <h2>Manage Projects</h2>
    <div id="projectAdvertDiv">
        <form>
            <div class="alert alert-info">
                <i class="icon-info-sign"></i> Manage the adverts and closing dates for your projects here.
            </div>
            
            <div id="projectAdvertsDiv" class="row-group" style="display:none">
              <div class="tableContainer table table-condensed ">
                <table id="projectAdvertsTable" class="table table-striped table-condensed table-hover table-bordered">
                  <colgroup>
                    <col />
                    <col style="width: 30px;" />
                    <col style="width: 30px;" />
                  </colgroup>
                  <tbody>
                  
                  </tbody>
                </table>
              </div>
            </div>
            
            <input type="hidden" id="projectId"/>
            
            <div class="row-group">
                <h3>Project Advert</h3>
                <div class="row" id="projectAdvertProgramDiv">
                  <label for="projectAdvertProgramSelect" class="plain-label">Programme<em>*</em></label> <span class="hint" data-desc="<@spring.message 'prospectus.programme'/>"></span>
                  <div class="field">
                      <select id="projectAdvertProgramSelect" class="max">
                          <option value="">Select...</option> <#list programmes as programme>
                          <option value="${programme.code}"<#if program?? && programme.code == program.code>selected</#if> > ${programme.title?html}</option> </#list>
                      </select>
                  </div>
                </div>
                <div class="row" id="projectAdvertTitleDiv">
                    <label for="projectAdvertTitleInput" class="plain-label">Title <em>*</em></label>
                    <span class="hint" data-desc=""></span>
                    <div class="field">
                        <input class="numeric max" type="text" id="projectAdvertTitleInput" />
                    </div>
                </div>
                <div class="row" id="projectAdvertDescriptionDiv">
                    <label for="projectAdvertDescriptionText" class="plain-label">Description <em>*</em></label> <span class="hint" data-desc="<@spring.message 'prospectus.description'/>"></span>
                    <div class="field">
                        <textarea id="projectAdvertDescriptionText" class="max" rows="6" cols="150"></textarea>
                    </div>
                </div>
                <div class="row" id="projectAdvertStudyDurationDiv">
                    <label for="projectDuration" class="plain-label">Duration of Study <em>*</em>
                    </label> <span class="hint" data-desc="<@spring.message 'prospectus.durationOfStudy'/>"></span>
                    <div class="field">
                        <input class="numeric input-small" type="text" size="4" id="projectAdvertStudyDurationInput" />
                        <select id="projectAdvertStudyDurationUnitSelect" class="input small">
                            <option value="">Select...</option>
                            <option value="Months">Months</option>
                            <option value="Years">Years</option>
                        </select>
                    </div>
                </div>
                <div class="row" id="projectAdvertFundingDiv">
                    <label for="projectAdvertFundingText" class="plain-label">Funding Information</label> <span class="hint" data-desc="<@spring.message 'prospectus.fundingInformation'/>"></span>
                    <div class="field">
                        <textarea id="projectAdvertFundingText" class="max" rows="6" cols="150"></textarea>
                    </div>
                </div>
            </div>
            
            <div class="row-group">
              <h3>Closing Date</h3>
              <div class="row" id="projectAdvertHasClosingDateDiv">
                <label class="plain-label">Would you like to set a closing date? <em>*</em>
                </label> <span class="hint" data-desc="<@spring.message 'prospectus.acceptingApplications'/>"></span>
                <div class="field">
                  <input id="projectAdvertHasClosingDateRadioYes" type="radio" name="projectAdvertHasClosingDateRadio" value="true">
                    Yes
                  </input>
                  <input id="projectAdvertHasClosingDateRadioNo" type="radio" name="projectAdvertHasClosingDateRadio" value="false">
                    No
                  </input>
                </div>
              </div>
              
              <div class="row" id="projectAdvertClosingDateDiv">
                <label for="closingDate" class="plain-label">Closing Date <em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'prospectus.closingDate'/>"></span>
                <div class="field">
                  <input type="text" id="projectAdvertClosingDateInput" class="full date"/>
                </div>
              </div>
            </div>     
                                               
            <div class="row-group">
              <div class="row" id="projectAdvertIsActiveDiv">
                <label class="plain-label">Are you currently accepting applications? <em>*</em>
                </label> <span class="hint" data-desc="<@spring.message 'prospectus.acceptingApplications'/>"></span>
                <div class="field">
                  <input id="projectAdvertIsActiveRadioYes" type="radio" name="projectAdvertIsActiveRadio" value="true">
                  Yes
                  </input>
                  <input id="projectAdvertIsActiveRadioNo" type="radio" name="projectAdvertIsActiveRadio" value="false">
                  No
                  </input>
                </div>
              </div>
              
              <div class="field">
                <a id="addProjectAdvert" role="button" class="btn btn-primary">Add</a>
              </div>
            </div>
            
            <div class="row-group">
                <h3>Advert</h3>
                <div class="alert alert-info">
                    <i class="icon-info-sign"></i> Embed these resources in emails and on webpages to provide applicants with links to apply for your project.
                </div>
                <div class="row">
                    <label for="projectAdvertLinkToApply" class="plain-label">Link to Apply</label> <span class="hint" data-desc="<@spring.message 'prospectus.linkToApply'/>"></span>
                    <div class="field">
                        <input id="projectAdvertLinkToApply" name="project" class="input-xxlarge" type="text" role="textbox" aria-haspopup="true" readonly>
                    </div>
                </div>
                <div class="row">
                    <label for="projectAdvertButtonToApply" class="plain-label">Button to Apply</label> <span class="hint" data-desc="<@spring.message 'prospectus.buttonToApply'/>"></span>
                    <div class="field">
                        <textarea id="projectAdvertButtonToApply" class="input-xxlarge" rows="6" cols="150" readonly></textarea>
                    </div>
                </div>
            </div>
            <div class="buttons">
                <button class="btn" type="button" id="projectsClear">Clear</button>
                <button class="btn btn-primary" type="button" id="projectsSave">Save</button>
            </div>
        </form>
    </div>
</section>

    