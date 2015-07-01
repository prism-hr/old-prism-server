package com.zuehlke.pgadmissions.mappers;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.address.Address;

@Service
@Transactional
public class AddressMapper {

    public <T extends Address, V extends Address> V transform(T source, Class<V> targetClass) {
        V target = BeanUtils.instantiate(targetClass);

        target.setId(source.getId());
        target.setAddressLine1(source.getAddressLine1());
        target.setAddressLine2(source.getAddressLine2());
        target.setAddressTown(source.getAddressTown());
        target.setAddressRegion(source.getAddressRegion());
        target.setAddressCode(source.getAddressCode());

        return target;
    }

}
