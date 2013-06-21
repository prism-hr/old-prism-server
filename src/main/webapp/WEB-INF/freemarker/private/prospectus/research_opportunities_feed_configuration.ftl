<section class="form-rows">
<h2>Research Opportunities Feed</h2>
  <div>
    <form>
        <div class="alert alert-info">
            <i class="icon-info-sign"></i> 
        </div>

        <div id="researchOpportunityFeedSection">
            <div class="row-group" id="existing-feed-table-row-group">
                <div class="tableContainer table table-condensed">
                    <table id="existing-feed-table" class="table table-striped table-condensed table-hover table-bordered" style="">
                        <colgroup>
                                <col>
                                <col style="width: 30px;">
                                <col style="width: 30px;">
                        </colgroup>
                        <tbody>
                            <tr></tr>
                        </tbody>   
                    </table>
                </div>
            </div>
                  
            <div class="row-group">
                <div class="row">
                    <label for="programmes_lbl" class="plain-label">Programme(s)<em>*</em></label> 
                    <span class="hint" data-desc=""></span>
                    <div class="field">
                        <select multiple="" size="10" id="feed-programmes" name="feed-programmes" class="max"></select>
                    </div>
                </div>
            </div>
            
            <div class="row-group">
                <div class="row">
                    <label for="feed_format_lbl" class="plain-label">Title<em>*</em></label> 
                    <span class="hint" data-desc=""></span>
                    <div class="field">
                        <input class="full" type="text" name="feed-title" id="feed-title" />
                    </div>
                </div>
                
                <div class="row">
                    <label for="feed_format_lbl" class="plain-label">Feed Format<em>*</em></label> 
                    <span class="hint" data-desc="<@spring.message 'prospectus.feedFormat'/>"></span>
                    <div class="field">
                        <select id="feedformat" name="feedformat" class="input-small">
                            <option value="SMALL">Small</option>
                            <option value="LARGE">Large</option>
                        </select>
                    </div>
                </div>
                
                <div class="row">
                    <label for="embed_code_lbl" class="plain-label">Embed Code</label> 
                    <span class="hint" data-desc="<@spring.message 'prospectus.embedCode'/>"></span>
                    <div class="field">
                        <textarea name="feedCode" id="feedCode" class="input-xxlarge" rows="6" cols="150"></textarea>
                    </div>
                    <div class="field">
                        <a href="#previewFeed" id="feed-preview-go" role="button" class="btn" data-toggle="modal">Preview</a>
                    </div>
                </div>
            </div>
            
            <div class="buttons">
                <button class="btn btn-primary" type="button" id="save-feed-go">Submit</button>
            </div>
            
            
            <input id="editingFeedId" name="editingFeedId" type="hidden" value="">
            
        </form>
    </div>
</div>
</section>
<script type="text/javascript" src="<@spring.url '/design/default/js/prospectus/research_opportunities_feed_configuration.js' />"></script>