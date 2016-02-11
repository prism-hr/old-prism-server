<!doctype html>
<html xmlns="http://www.w3.org/1999/xhtml">
[#if metadata??]
<head>
    <meta charset="utf-8">

    [#assign resource = metadata.resource]

        <title>${resource.name?html}</title>
        [#if resource.summary??]
            <meta name="description" content="${resource.summary?html}"/>
        [/#if]
        <!-- <meta name="google-site-verification" content="" /> -->
        <meta name="author" content="${resource.author?html}"/>
        <meta property="fb:app_id" content="172294119620634"/>
        <meta property="og:url" content="${resource.resourceUrl}"/>
        [#if resource.summary??]
            <meta property="og:description" content="${resource.summary?html}"/>
        [/#if]
        <meta property="og:image" content="${resource.thumbnailUrl}"/>
        <meta property="og:type" content="website"/>
        <meta property="og:site_name" content="PRiSM"/>
        <meta property="og:locale" content="en_GB"/>
</head>
<body>
    [#if metadata.relatedInstitutions??]
    <div>
        <p>${metadata.relatedInstitutions.label}:</p>
        <ul>
            [#list metadata.relatedInstitutions.resources as relatedInstitution]
                <li>
                    <a href="${metadata.applicationUrl}/#!/?institution=${relatedInstitution.id?c}">${relatedInstitution.name}</a>
                </li>
            [/#list]
        </ul>
    </div>
    [#else]
    <div>
        <h3>${metadata.name}</h3>
    </div>
    <div>
    ${metadata.description}
    </div>
    [#list metadata.parentResources as parentResource]
        <p><a href="${parentResource.resourceUrl}">${parentResource.name?html}</a></p>
    [/#list]
    <div>

    </div>
        [#if metadata.relatedDepartments??]
        <div>
            <p>${metadata.relatedDepartments.label}:</p>
            <ul>
                [#list metadata.relatedDepartments.resources as relatedDepartment]
                    <li>
                        <a href="${metadata.applicationUrl}/#!/?department=${relatedDepartment.id?c}">${relatedDepartment.name}</a>
                    </li>
                [/#list]
            </ul>
        </div>
        [/#if]

        [#if metadata.relatedPrograms??]
        <div>
            <p>${metadata.relatedPrograms.label}:</p>
            <ul>
                [#list metadata.relatedPrograms.resources as relatedProgram]
                    <li>
                        <a href="${metadata.applicationUrl}/#!/?program=${relatedProgram.id?c}">${relatedProgram.name}</a>
                    </li>
                [/#list]
            </ul>
        </div>
        [/#if]

        [#if metadata.relatedProjects??]
        <div>
            <p>${metadata.relatedProjects.label}:</p>
            <ul>
                [#list metadata.relatedProjects.resources as relatedProject]
                    <li>
                        <a href="${metadata.applicationUrl}/#!/?project=${relatedProject.id?c}">${relatedProject.name}</a>
                    </li>
                [/#list]
            </ul>
        </div>
        [/#if]

        [#if metadata.relatedUsers??]
        <div>
            <p>${metadata.relatedUsers.label}:</p>
            <ul>
                [#list metadata.relatedUsers.users as relatedUser]
                    <li>${relatedUser}</li>
                [/#list]
            </ul>
        </div>
        [/#if]
    [/#if]
</body>
[/#if]
</html>
