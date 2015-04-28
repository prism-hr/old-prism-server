<!doctype html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
    <title>${metadata.title?html}</title>
    <#if metadata.description??>
    	<meta name="description" content="${metadata.description?html}"/>
   	</#if>
    <!-- <meta name="google-site-verification" content="" /> -->
    <meta name="author" content="${metadata.author?html}"/>
    <meta property="fb:app_id" content="172294119620634"/>
    <meta property="og:url" content="${metadata.resourceUrl}"/>
    <#if metadata.description??>
    	<meta property="og:description" content="${metadata.description?html}"/>
   	</#if>
    <meta property="og:image" content="${metadata.thumbnailUrl}"/>
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
    	<#if advert.programId?? || advert.projectId??>
    		<p><a href="${applicationUrl}/#!/?institution=${advert.institutionId?c}">${advert.institutionTitle?html}</a></p>
    	</#if>
    	<#if advert.projectId??>
    		<p><a href="${applicationUrl}/#!/?program=${advert.programId?c}">${advert.programTitle?html}</a></p>
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
