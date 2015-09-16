package com.zuehlke.pgadmissions.mapping;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.address.Address;
import com.zuehlke.pgadmissions.domain.address.AddressDefinition;
import com.zuehlke.pgadmissions.rest.representation.address.AddressRepresentation;

@Service
@Transactional
public class AddressMapper {

    @Inject
    private ImportedEntityMapper importedEntityMapper;

    public <T extends AddressDefinition<?>, V extends AddressDefinition<?>> V transform(T source, Class<V> targetClass) {
        V target = BeanUtils.instantiate(targetClass);

        target.setId(source.getId());
        target.setAddressLine1(source.getAddressLine1());
        target.setAddressLine2(source.getAddressLine2());
        target.setAddressTown(source.getAddressTown());
        target.setAddressRegion(source.getAddressRegion());
        target.setAddressCode(source.getAddressCode());

        return target;
    }

    public AddressRepresentation getAddressApplicationRepresentation(Address address) {
        AddressRepresentation representation = transform(address, AddressRepresentation.class);
        representation.setDomicile(importedEntityMapper.getImportedEntityRepresentation(address.getDomicile()));
        return representation;
    }

}
