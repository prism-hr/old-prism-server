<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/opportunities.css' />"/>
<#if !feedId??>
	<#assign feedId = "standalone-opportunity-list">
</#if>
<div style="overflow:hidden; position:relative">
  <div id="placeholder-${feedId}" class="placeholder">
  
    <form id="applyForm" action="/pgadmissions/apply/new" method="POST" style="display:none;" <#if shouldOpenNewTab??>target="_blank"</#if>>
      <input type="hidden" id="advert" name="advert" value=""/>
      <#if feedKey??>
      	<input type="hidden" id="feedKey-${feedId}" name="feedKey-${feedId}" value="${(feedKey)!}"/>
      </#if>
      <#if feedKeyValue??>
      	<input type="hidden" id="feedKeyValue-${feedId}" name="feedKeyValue-${feedId}" value="${(feedKeyValue)!}"/>
      </#if>
    </form>
  	
    <header>
	    <#if feedId == "standalone-opportunity-list">
	      <a href="/pgadmissions/createOpportunity" <#if shouldOpenNewTab??>target="_blank"</#if> class="btn btn-success newOpportunity">Advertise</a>
	    </#if>
      <h1>
        <#if feedTitle??>
          ${feedTitle}
        <#else>
          Opportunities
        </#if>
      </h1>
    </header>
    
    <section id="list-${feedId}" class="placeholder-list">
    	<ul></ul>
    </section>
      
    <footer class="clearfix">
    	<div class="left"><a href="http://www.engineering.ucl.ac.uk" target="_blank"><img src="<@spring.url '/design/default/images/ucl-engineering.jpg'/>" alt="" /></a></div>
    	<div class="right"><a href="http://prism.ucl.ac.uk" target="_blank"><img src="<@spring.url '/design/default/images/prism_small.jpg'/>" alt="" /></a></div>
    </footer>
    
  </div>
</div>