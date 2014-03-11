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
                  <label for="upi" class="plain-label">UCL Staff Identifier (UPI)</label> 
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
              <button class="btn" type="button" id="unlink-upi-go" style="display: none;">Unlink</button>
              <button class="btn btn-primary" type="button" id="save-upi-go" style="display: none;">Submit</button>
          </div>
      </form>
  </div>
  
  <div id="iris-profile-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">X</button>
            <h3 id="myModalLabel">Confirm Identity in IRIS</h3>
        </div>
        <div class="modal-body" id="iris-profile-modal-body">
            <iframe id="iris-profile-modal-iframe"> </iframe>
        </div>
        <div class="modal-footer">
            <button class="btn" data-dismiss="modal" aria-hidden="true">I made a mistake</button>
            <button class="btn btn-primary" id="iris-profile-modal-confirm-btn">I confirm this is me</button>
        </div>
    </div>
</section>