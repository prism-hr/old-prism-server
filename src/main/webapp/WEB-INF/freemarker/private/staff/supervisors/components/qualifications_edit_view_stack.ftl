<#list applicationForm.qualifications as qualification>
    
    <#assign encQualificationId = encrypter.encrypt(qualification.id) />

    <div class="row-group" id="qualification_${encQualificationId}" style="display:none">

        <!-- Header -->
        <div class="admin_row">
            <label class="admin_header">Qualification</label>
            <div class="field">&nbsp</div>
        </div>

        <!-- Provider -->
        <div class="admin_row">
            <span class="admin_row_label">Institution Country</span>
            <div class="field" id="qualificationInstitutionCountry">${(qualification.institutionCountry.name?html)!"Not Provided"}</div>
        </div>

        <div class="admin_row">
            <span class="admin_row_label">Institution / Provider Name</span>
            <div class="field" id="qualificationInstitution">${(qualification.qualificationInstitution?html)!"Not Provided"}</div>
        </div>

        <div class="admin_row">
            <span class="admin_row_label">Other Institution / Provider Name</span>
            <div class="field" id="qualificationInstitution">${(qualification.otherQualificationInstitution?html)!"Not Provided"}</div>
        </div>

        <!-- Type -->
        <div class="admin_row">
            <span class="admin_row_label">Qualification Type</span>
            <div class="field" id="qualificationType">${(qualification.qualificationType.name?html)!"Not Provided"}</div>
        </div>

        <!-- Title -->
        <div class="admin_row">
            <span class="admin_row_label">Title</span>
            <div class="field" id="qualificationSubject">${(qualification.qualificationTitle?html)!"Not Provided"}</div>
        </div>
        
        <!-- Subject -->
        <div class="admin_row">
            <span class="admin_row_label">Subject</span>
            <div class="field" id="qualificationSubject">${(qualification.qualificationSubject?html)!"Not Provided"}</div>
        </div>

        <!-- Language (in which programme was undertaken) -->
        <div class="admin_row">
            <span class="admin_row_label">Language of Study</span>
            <div class="field" id="qualificationLanguage">${(qualification.qualificationLanguage?html)!"Not Provided"}</div>
        </div>

        <!-- Start date -->
        <div class="admin_row">
            <span class="admin_row_label">Start Date</span>
            <div class="field" id="qualificationStartDate">${(qualification.qualificationStartDate?string('dd MMM yyyy'))!"Not Provided"}</div>
        </div>

        <!-- Has qualification been awarded? -->
        <div class="admin_row">
            <span class="admin_row_label">Has this qualification been awarded?</span>
            <div class="field" id="qualificationCompleted">${(qualification.completed?string("Yes", "No"))!"Not Provided"}</div>
        </div>

        <!-- Qualification grade -->
        <div class="admin_row">
            <span class="admin_row_label">Grade / Result /GPA</span>
            <div class="field" id="qualificationGrade">${(qualification.qualificationGrade?html)!"Not Provided"}</div>
        </div>

        <!-- Award date -->
        <div class="admin_row">
            <span class="admin_row_label">Award Date</span>
            <div class="field" id="qualificationAwardDate">${(qualification.qualificationAwardDate?string('dd MMM yyyy'))!"Not Provided"}</div>
        </div>

        <!-- Attachment / supporting document  -->
        <div class="admin_row">
            <span class="admin_row_label">Transcript (PDF)</span>
            <div class="field" id="referenceDocument">
                <#if qualification.proofOfAward??> <a href="<@spring.url '/download?documentId=${(encrypter.encrypt(qualification.proofOfAward.id))!}'/>" target="_blank">${(qualification.proofOfAward.fileName)!}</a> <#else> Not Provided </#if>
            </div>

        </div>
    </div>
</#list>