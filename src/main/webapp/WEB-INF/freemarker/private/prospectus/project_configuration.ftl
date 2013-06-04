<section class="form-rows">
    <h2>Manage Projects</h2>
    <div>
        <form>
            <div class="alert alert-info">
                <i class="icon-info-sign"></i> Manage the adverts and closing dates for your projects here.
            </div>
            <div class="row-group">
                <h3>Project Advert</h3>
                <div class="row" id="projectProgramDiv">
                    <label for="programme" class="plain-label">Programme<em>*</em></label> <span class="hint" data-desc="<@spring.message 'prospectus.programme'/>"></span>
                    <div class="field">
                        <select name="programme" id="programme" class="max">
                            <option value="">Select...</option> <#list programmes as programme>
                            <option value="${programme.code}"<#if program?? && programme.code == program.code>selected</#if> > ${programme.title?html}</option> </#list>
                        </select>
                    </div>
                </div>
                <div class="row" id="projectTitleDiv">
                    <label for="projectTitle" class="plain-label">Title <em>*</em></label>
                    <span class="hint" data-desc=""></span>
                    <div class="field">
                        <input class="numeric max" type="text" id="projectTitle" />
                    </div>
                </div>
                <div class="row" id="projectDescriptionDiv">
                    <label for="programmeDescription" class="plain-label">Description <em>*</em></label> <span class="hint" data-desc="<@spring.message 'prospectus.description'/>"></span>
                    <div class="field">
                        <textarea id="programmeDescription" class="max" rows="6" cols="150"></textarea>
                    </div>
                </div>
                <div class="row" id="projectDurationDiv">
                    <label for="projectDuration" class="plain-label">Duration of Study <em>*</em>
                    </label> <span class="hint" data-desc="<@spring.message 'prospectus.durationOfStudy'/>"></span>
                    <div class="field">
                        <input class="numeric input-small" type="text" size="4" id="projectDuration" />
                        <select id="timeUnit" class="input small">
                            <option value="">Select...</option>
                            <option value="Months">Months</option>
                            <option value="Years">Years</option>
                        </select>
                    </div>
                </div>
                <div class="row" id="projectFundingDiv">
                    <label for="programmeFundingInformation" class="plain-label">Funding Information</label> <span class="hint" data-desc="<@spring.message 'prospectus.fundingInformation'/>"></span>
                    <div class="field">
                        <textarea id="programmeFundingInformation" class="max" rows="6" cols="150"></textarea>
                    </div>
                </div>
            </div>
            <div class="row-group">
                <h3>Closing Date</h3>
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
                    <i class="icon-info-sign"></i> Embed these resources in emails and on webpages to provide applicants with links to apply for your project.
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

    