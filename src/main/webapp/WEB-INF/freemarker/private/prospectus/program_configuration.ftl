<script type="text/javascript" src="<@spring.url '/design/default/js/prospectus/program_configuration.js' />"></script>

<section class="form-rows">
      <h2>Manage Research Programmes</h2>
      <div>
          <form>
              <div class="alert alert-info">
                  <i class="icon-info-sign"></i> A specific guidance note for the context of use goes here.
              </div>
              <div class="row-group">
                  <h3>Programme Advert</h3>
                    <div class="row" id="program">
                      <label for="programme" class="plain-label">Programme<em>*</em></label> <span class="hint" data-desc="<@spring.message 'prospectus.programme'/>"></span>
                      <div class="field">
                          <select name="programme" id="programme" class="max">
                              <option value="">Select...</option> <#list programmes as programme>
                              <option value="${programme.code}"<#if program?? && programme.code == program.code>selected</#if> > ${programme.title?html}</option> </#list>
                          </select>
                      </div>
                    </div>
                    <div class="row" id="description">
                      <label for="programmeDescription" class="plain-label">Description <em>*</em></label> <span class="hint" data-desc="<@spring.message 'prospectus.description'/>"></span>
                      <div class="field">
                          <textarea id="programmeDescription" class="max" rows="6" cols="150"></textarea>
                      </div>
                    </div>
                    <div class="row" id="durationOfStudyInMonth">
                      <label for="programmeDurationOfStudy" class="plain-label">Duration of Study <em>*</em>
                      </label> <span class="hint" data-desc="<@spring.message 'prospectus.durationOfStudy'/>"></span>
                      <div class="field">
                          <input class="numeric input-small" type="text" size="4" id="programmeDurationOfStudy" /> <select id="timeUnit" class="input small">
                              <option value="">Select...</option>
                              <option value="Months">Months</option>
                              <option value="Years">Years</option>
                          </select>
                      </div>
                    </div>
                    <div class="row">
                      <label for="programmeFundingInformation" class="plain-label">Funding Information</label> <span class="hint" data-desc="<@spring.message 'prospectus.fundingInformation'/>"></span>
                      <div class="field">
                          <textarea id="programmeFundingInformation" class="max" rows="6" cols="150"></textarea>
                      </div>
                    </div>
              </div>
              <div class="row-group">
                  <h3>Closing Dates</h3>
                  <div class="alert alert-info">
                      <i class="icon-info-sign"></i> A specific guidance note for the context of use goes here.
                  </div>
                  <div class="tableContainer table table-condensed ">
                    <table id="closingDates" class="table table-striped table-condensed table-hover table-bordered">
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
              <div class="row-group">
                <h3>Add Closing Date</h3>
                <input type="hidden" id="closingDateId"/>
                <div class="row" id="closingDateRow">
                  <label for="closingDate" class="plain-label">Closing Date <em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'prospectus.closingDate'/>"></span>
                  <div class="field">
                    <input type="text" id="closingDate" class="full date"/>
                  </div>
                </div>
                <div class="row" id="studyPlacesRow">
                  <label for="studyPlaces" class="plain-label">Study Places</label> 
                  <span class="hint" data-desc="<@spring.message 'prospectus.studyPlaces'/>"></span>
                  <div class="field">
                      <input class="numeric input-small" type="text" size="4" id="studyPlaces" />
                  </div>
                </div>
                <div class="field">
                  <a id="addClosingDate" role="button" class="btn btn-primary">Add</a>
                </div>
              </div>                                        
              <div class="row-group">
                <div class="row" id="isCurrentlyAcceptingApplications">
                  <label class="plain-label" for="currentlyAcceptingApplication">Are you currently accepting applications? <em>*</em>
                  </label> <span class="hint" data-desc="<@spring.message 'prospectus.acceptingApplications'/>"></span>
                  <div class="field">
                    <input id="currentlyAcceptingApplicationYes" type="radio" name="switch" value="true">
                    Yes
                    </input>
                    <input id="currentlyAcceptingApplicationNo" type="radio" name="switch" value="false">
                    No
                    </input>
                  </div>
                </div>
              </div>
              <div class="row-group">
                  <h3>Advert</h3>
                  <div class="alert alert-info">
                      <i class="icon-info-sign"></i> A specific guidance note for the context of use goes here.
                  </div>
                  <div class="row">
                      <label for="linkToApply" class="plain-label">Link to Apply</label> <span class="hint" data-desc="<@spring.message 'prospectus.linkToApply'/>"></span>
                      <div class="field">
                          <input id="linkToApply" name="project" class="input-xxlarge" type="text" role="textbox" aria-haspopup="true" readonly>
                      </div>
                  </div>
                  <div class="row">
                      <label for="buttonToApply" class="plain-label">Button to Apply</label> <span class="hint" data-desc="<@spring.message 'prospectus.buttonToApply'/>"></span>
                      <div class="field">
                          <textarea id="buttonToApply" class="input-xxlarge" rows="6" cols="150" readonly></textarea>
                      </div>
                  </div>
              </div>
              <div class="buttons">
                  <button class="btn btn-primary" type="button" id="save-go">Save</button>
              </div>
          </form>
      </div>
  </section>