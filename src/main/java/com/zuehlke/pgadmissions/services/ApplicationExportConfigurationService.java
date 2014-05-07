package com.zuehlke.pgadmissions.services;

import static org.apache.commons.lang.BooleanUtils.isTrue;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dto.ApplicationExportConfigurationDTO;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Service
@Transactional
public class ApplicationExportConfigurationService {

    public int getBatchSize() {
        return getApplicationExportConfiguration().getBatchSize();
    }

    public void disablePorticoInterface() {
        ApplicationExportConfigurationDTO configuration = getApplicationExportConfiguration();
        configuration.setEnabled(false);
        updateApplicationExportConfiguration(configuration);
    }


    public boolean isPorticoInterfaceEnabled() {
        return isTrue(getApplicationExportConfiguration().isEnabled());
    }

    public ApplicationExportConfigurationDTO getApplicationExportConfiguration() {
        // TODO Auto-generated method stub
        return null;
    }

    public void updateApplicationExportConfiguration(ApplicationExportConfigurationDTO configuration) {
        // TODO Auto-generated method stub

    }

    public boolean userTurnedOnThrottle(boolean newValueSetByTheUser) {
        return !getApplicationExportConfiguration().isEnabled() && newValueSetByTheUser;
    }

}
