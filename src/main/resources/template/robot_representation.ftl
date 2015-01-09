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
    <#if advert.relatedInstitutions?size > 0>
    <div>
        <p>Related Institutions:</p>
        <li>
            <#list advert.relatedInstitutions as relatedInstitution>
                <ul><a href="${applicationUrl}/!#/?institution=${relatedInstitution.id}">${relatedInstitution.title}</a>
                </ul>
            </#list>
        </li>
    </div
    <#else>
    <div>
        <h3>${advert.title}</h3>
    </div>
    <div>
    ${advert.description}
    </div>
        <#if advert.relatedPrograms?size > 0>
        <div>
            <p>Related Programs:</p>
            <li>
                <#list advert.relatedPrograms as relatedProgram>
                    <ul><a href="${applicationUrl}/!#/?program=${relatedProgram.id}">${relatedProgram.title}</a></ul>
                </#list>
            </li>
                </div
        </#if>
        <#if advert.relatedProjects?size > 0>
        <div>
            <p>Related Projects:</p>
            <li>
                <#list advert.relatedProjects as relatedProject>
                    <ul><a href="${applicationUrl}/!#/?project=${relatedProject.id}">${relatedProject.title}</a></ul>
                </#list>
            </li>
                </div
        </#if>
        <#if advert.relatedUsers?size > 0>
        <div>
            <p>Related Users:</p>
            <li>
                <#list advert.relatedUsers as relatedUser>
                    <ul>${relatedUser}</ul>
                </#list>
            </li>
        </div
        </#if>
    </#if>
</#if>
</body>
</html>
