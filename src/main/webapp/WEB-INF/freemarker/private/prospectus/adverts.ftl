<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/opportunities.css' />"/>
<script type="text/javascript" src="<@spring.url '/design/default/js/prospectus/opportunities.js' />"></script>
<div style="overflow:hidden; position:relative">
 <div id="pholder">
	 <form id="applyForm" action="/pgadmissions/apply/new" method="POST" <#if shouldOpenNewTab??>target="_blank"</#if>>
		<input type="hidden" id="program" name="program" value=""/>
		<input type="hidden" id="advert" name="advert" value=""/>
		<input type="hidden" id="project" name="project" value=""/>
	</form>
	
	<#if Request['prospectus.selectedAdvert']?has_content>
		<input type="hidden" id="prospectusSelectedAdvert" name="prospectusSelectedAdvert" value="${Request['prospectus.selectedAdvert']}"/>
	</#if>
    <header>
      <h1>Research Study Opportunities</h1></header>
    <section id="plist">
    	<ul></ul>
    	<script type="text/javascript" src="http://s7.addthis.com/js/300/addthis_widget.js#pubid=xa-51af252068c85125"></script>
    	
    </section>
    <footer class="clearfix">
    	<div class="left"><a href="www.engineering.ucl.ac.uk" target="_blank"><img src="<@spring.url '/design/default/images/ucl-engineering.jpg'/>" alt="" /></a></div>
    	<div class="right"><a href="http://prism.ucl.ac.uk" target="_blank"><img src="<@spring.url '/design/default/images/prism_small.jpg'/>" alt="" /></a></div>
    </footer>
</div>
</div>