<!doctype html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
    <title>${title?html}</title>

    <meta name="description" content="${description?html}"/>
    <meta name="keywords"
          content="opportunities graduates postgraduates scholarships internships jobs courses degrees ucl"/>
    <!-- <meta name="google-site-verification" content="" /> -->
    <meta name="author" content="Wujek Zenon"/>
    <meta property="fb:app_id" content="172294119620634"/>
    <meta property="og:url" content="${ogUrl}"/>
    <meta property="og:title" content="${title?html}"/>
    <meta property="og:description" content="${description?html}"/>
    <meta property="og:image" content="${imageUrl}"/>
    <meta property="og:type" content="website"/>
    <meta property="og:site_name" content="PRiSM"/>
    <meta property="og:locale" content="en_GB"/>
</head>
<body>
<#if advert??>
    <#if advert.relatedInstitutions?has_content>
    <div>
        <p>Related Institutions:</p>
        <ul>
            <#list advert.relatedInstitutions as relatedInstitution>
                <li><a href="${applicationUrl}/#!/?institution=${relatedInstitution.id?c}">${relatedInstitution.title}</a></li>
            </#list>
        </ul>
    </div>
    <#else>
    <div>
        <h3>${advert.title}</h3>
    </div>
    <div>
    	${advert.description}
    </div>
    	<#if advert.parentInstitution??>
    		<p><a href="${applicationUrl}/#!/?institution=${advert.parentInstitution}">Parent Institution</a></p>
    	</#if>
    	<#if advert.parentProgram??>
    		<p><a href="${applicationUrl}/#!/?program=${advert.parentProgram}">Parent Program</a></p>
    	</#if>
    <div>
    </div>
        <#if advert.relatedPrograms?has_content>
        <div>
            <p>Related Programs:</p>
            <ul>
                <#list advert.relatedPrograms as relatedProgram>
                    <li><a href="${applicationUrl}/#!/?program=${relatedProgram.id?c}">${relatedProgram.title}</a></li>
                </#list>
            </ul>
                </div>
        </#if>
        <#if advert.relatedProjects?has_content>
	        <div>
	            <p>Related Projects:</p>
	            <ul>
	                <#list advert.relatedProjects as relatedProject>
	                    <li><a href="${applicationUrl}/#!/?project=${relatedProject.id?c}">${relatedProject.title}</a></li>
	                </#list>
	            </ul>
	        </div>
        </#if>
        <#if advert.relatedUsers?has_content>
	        <div>
	            <p>Related Users:</p>
	            <ul>
	                <#list advert.relatedUsers as relatedUser>
	                    <li>${relatedUser}</li>
	                </#list>
	            </ul>
	        </div>
        </#if>
    </#if>
</#if>
</body>
</html>
