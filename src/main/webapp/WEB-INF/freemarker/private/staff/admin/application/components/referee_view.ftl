<div class="row-group">
	<#if referee.hasResponded()>
    	<input type="hidden" id="referee_${encRefereeId}_hasResponded" value="responded"/>
    </#if>
    
    <div class="admin_row">
        <label class="admin_header">Reference <#if referee.declined> - Declined</#if></label>
        <div class="field">&nbsp</div>
    </div>

    <!-- First name -->
    <div class="admin_row">
        <span class="admin_row_label">First Name</span>
        <div class="field" id="ref_firstname">${(referee.firstname?html)!"Not Provided"}</div>
    </div>

    <!-- Last name -->
    <div class="admin_row">
        <span class="admin_row_label">Last Name</span>
        <div class="field" id="ref_lastname">${(referee.lastname?html)!"Not Provided"}</div>
    </div>

    <!-- Employer / company name -->
    <div class="admin_row">
        <span class="admin_row_label">Employer</span>
        <div class="field" id="ref_employer">${(referee.jobEmployer?html)!"Not Provided"}</div>
    </div>

    <!-- Position title -->
    <div class="admin_row">
        <span class="admin_row_label">Position</span>
        <div class="field" id="ref_position">${(referee.jobTitle?html)!"Not Provided"}</div>
    </div>

    <!-- Address body -->
    <div class="admin_row">
        <span class="admin_row_label">Address</span>
        <div class="field" id="ref_address_location">${(referee.addressLocation.locationString?html)!"Not Provided"}</div>
    </div>

    <!-- Country -->
    <div class="admin_row">
        <span class="admin_row_label">Country</span>
        <div class="field" id="ref_address_country">${(referee.addressLocation.domicile.name?html)!"Not Provided"}</div>
    </div>
    
    <!-- Email address -->
    <div class="admin_row">
        <span class="admin_row_label">Email</span>
        <div class="field" id="ref_email">${(referee.email?html)!"Not Provided"}</div>
    </div>

    <!-- Telephone -->
    <div class="admin_row">
        <span class="admin_row_label">Telephone</span>
        <div class="field" id="ref_phone">${(referee.phoneNumber?html)!"Not Provided"}</div>
    </div>

    <!-- Skype address -->
    <div class="admin_row">
        <span class="admin_row_label">Skype</span>
        <div class="field" id="ref_messenger">${(referee.messenger?html)!"Not Provided"}</div>
    </div>
</div>
