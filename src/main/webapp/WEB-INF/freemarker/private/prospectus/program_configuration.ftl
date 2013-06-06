<script type="text/javascript" src="<@spring.url '/design/default/js/prospectus/program_configuration.js' />"></script>

<section class="form-rows">
      <h2>Manage Programmes</h2>
      <div id="programAdvertDiv">
          <form>
              <div class="alert alert-info">
                  <i class="icon-info-sign"></i> Manage the adverts and closing dates for your programmes here.
              </div>
              <div class="row-group">
                  <h3>Programme Advert</h3>
                    <div class="row" id="programAdvertProgramDiv">
                      <label for="programAdvertProgramSelect" class="plain-label">Programme<em>*</em></label> <span class="hint" data-desc="<@spring.message 'prospectus.programme'/>"></span>
                      <div class="field">
                          <select id="programAdvertProgramSelect" class="max">
                              <option value="">Select...</option> <#list programmes as programme>
                              <option value="${programme.code}"<#if program?? && programme.code == program.code>selected</#if> > ${programme.title?html}</option> </#list>
                          </select>
                      </div>
                    </div>
                    <div class="row" id="programAdvertDescriptionDiv">
                      <label for="programAdvertDescriptionText" class="plain-label">Description <em>*</em></label> <span class="hint" data-desc="<@spring.message 'prospectus.description'/>"></span>
                      <div class="field">
                          <textarea id="programAdvertDescriptionText" class="max" rows="6" cols="150"></textarea>
                      </div>
                    </div>
                    <div class="row" id="programAdvertStudyDurationDiv">
                      <label for="programAdvertStudyDurationInput" class="plain-label">Duration of Study <em>*</em>
                      </label> <span class="hint" data-desc="<@spring.message 'prospectus.durationOfStudy'/>"></span>
                      <div class="field">
                          <input class="numeric input-small" type="text" size="4" id="programAdvertStudyDurationInput" />
                          <select id="programAdvertStudyDurationUnitSelect" class="input small">
                              <option value="">Select...</option>
                              <option value="Months">Months</option>
                              <option value="Years">Years</option>
                          </select>
                      </div>
                    </div>
                    <div class="row">
                      <label for="programAdvertFundingText" class="plain-label">Funding Information</label> <span class="hint" data-desc="<@spring.message 'prospectus.fundingInformation'/>"></span>
                      <div class="field">
                          <textarea id="programAdvertFundingText" class="max" rows="6" cols="150"></textarea>
                      </div>
                    </div>
              </div>
              <div class="row-group">
                  <h3>Closing Dates</h3>
                  <div class="alert alert-info">
                      <i class="icon-info-sign"></i> Specify closing dates to process applications for your programmes in batch.
                  </div>
                  <div class="tableContainer table table-condensed ">
                    <table id="programAdvertClosingDates" class="table table-striped table-condensed table-hover table-bordered">
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
              <div class="row-group" id="programAdvertClosingDateGroup">
                <h3 id="programAdvertClosingDateHeading">Add Closing Date</h3>
                <input type="hidden" id="programAdvertClosingDateId"/>
                <div class="row" id="programAdvertClosingDateDiv">
                  <label for="closingDate" class="plain-label">Closing Date <em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'prospectus.closingDate'/>"></span>
                  <div class="field">
                    <input type="text" id="programAdvertClosingDateInput" class="full date"/>
                  </div>
                </div>
                <div class="row" id="programAdvertStudyPlacesDiv">
                  <label for="programAdvertStudyPlacesInput" class="plain-label">Study Places</label> 
                  <span class="hint" data-desc="<@spring.message 'prospectus.studyPlaces'/>"></span>
                  <div class="field">
                      <input class="numeric input-small" type="text" size="4" id="programAdvertStudyPlacesInput" />
                  </div>
                </div>
                <div class="field">
                  <a id="addProgramAdvertClosingDate" role="button" class="btn btn-primary">Add</a>
                </div>
              </div>                                        
              <div class="row-group">
                <div class="row" id="programAdvertIsActiveDiv">
                  <label class="plain-label">Are you currently accepting applications? <em>*</em>
                  </label> <span class="hint" data-desc="<@spring.message 'prospectus.acceptingApplications'/>"></span>
                  <div class="field">
                    <input id="programAdvertIsActiveRadioYes" type="radio" name="switch" value="true">
                    Yes
                    </input>
                    <input id="programAdvertIsActiveRadioNo" type="radio" name="switch" value="false">
                    No
                    </input>
                  </div>
                </div>
              </div>
              <div class="row-group">
                  <h3>Advert</h3>
                  <div class="alert alert-info">
                      <i class="icon-info-sign"></i> Embed these resources in emails and on webpages to provide applicants with links to apply for your programme.
                  </div>
                  <div class="row">
                      <label for="programAdvertLinkToApply" class="plain-label">Link to Apply</label> <span class="hint" data-desc="<@spring.message 'prospectus.linkToApply'/>"></span>
                      <div class="field">
                          <input id="programAdvertLinkToApply" name="project" class="input-xxlarge" type="text" role="textbox" aria-haspopup="true" readonly>
                      </div>
                  </div>
                  <div class="row">
                      <label for="programAdvertButtonToApply" class="plain-label">Button to Apply</label> <span class="hint" data-desc="<@spring.message 'prospectus.buttonToApply'/>"></span>
                      <div class="field">
                          <textarea id="programAdvertButtonToApply" class="input-xxlarge" rows="6" cols="150" readonly></textarea>
                      </div>
                  </div>
              </div>
              <div class="buttons">
                  <button class="btn btn-primary" type="button" id="programAdvertSave">Save</button>
              </div>
          </form>
      </div>
  </section>