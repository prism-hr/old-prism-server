<section class="form-rows">
<h2>Research Opportunities Feed</h2>
  <div>
    <form>
        <div id="researchOpportunityFeedSection">
            <div class="row-group" id="existing-feed-table-row-group">
                <div class="tableContainer table table-condensed">
                    <table id="existing-feed-table" class="table table-condensed table-bordered" style="">
                        <colgroup>
                                <col>
                                <col style="width: 36px;">
                                <col style="width: 36px;">
                        </colgroup>
                        <tbody>
                            <tr>
                                <td colspan="3" class="scrollparent">
                                    <div class="scroll">
                                        <table class="table-hover table-hover table-striped" style="">
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
                                </td>
                            </tr>
                        </tbody>   
                    </table>
                </div>
            </div>

            <div class="alert alert-info">
                <i class="icon-info-sign"></i> Select the programmes that you wish to advertise opportunities for.
            </div>
                  
            <div class="row-group">
                <div class="row">
                    <label for="programmes_lbl" class="plain-label">Programme(s)<em>*</em></label> 
                    <span class="hint" data-desc="Select the relevant programmes."></span>
                    <div class="field">
                        <select multiple="" size="10" id="feed-programmes" name="feed-programmes" class="max"></select>
                    </div>
                </div>
            </div>
            
            <div class="row-group">
                <div class="row">
                    <label for="feed_format_lbl" class="plain-label">Title<em>*</em></label> 
                    <span class="hint" data-desc="Give your feed a title. This will help you to find it again when you want to edit it."></span>
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
                        <textarea name="feedCode" id="feedCode" class="input-xxlarge" rows="6" cols="150" readonly="readonly"></textarea>
                    </div>
                </div>
            </div>
            
            <div class="buttons">
                <button class="btn btn-success" type="button" id="new-feed-go">Add New</button>
                <button class="btn btn-primary" type="button" id="save-feed-go">Create</button>
            </div>
            
            
            <input id="editingFeedId" name="editingFeedId" type="hidden" value="">
            
        </form>
    </div>
</div>
</section>
<script type="text/javascript" src="<@spring.url '/design/default/js/prospectus/research_opportunities_feed_configuration.js' />"></script>