<section class="form-rows">
  <h2>Link to UCL IRIS</h2>
  <div>
      <form>
          <div class="alert alert-info" id="iris-account-not-linked-message" style="display:none">
              <i class="icon-info-sign"></i> Link your UCL Prism profile to UCL IRIS. This will display your Research Opportunities Feed on your UCL IRIS profile.
          </div>
          <div class="alert alert-success" id="iris-account-linked-message" style="display:none">
              <i class="icon-ok-sign"></i> Your account is linked to UCL UPI: <span></span>
          </div>
          <div class="row-group">
              <div class="row">
                  <label for="upi" class="plain-label">UCL Staff Indentifier (UPI)</label> 
                  <span class="hint" data-desc="<@spring.message 'prospectus.iris.upi'/>"></span>
                  <div class="field">
                      <input id="upi" name="upi" class="input-small" type="text" />
                  </div>
                  
                  <div class="row">
                      <div class="field">
                          <!--
                          <div class="alert alert-error">
                              <i class="icon-warning-sign"></i>
                          </div>
                          -->
                      </div>
                  </div>
                  
              </div>
          </div>
          <div class="buttons">
              <button class="btn btn-primary" type="button" id="save-upi-go">Submit</button>
          </div>
      </form>
  </div>
</section>