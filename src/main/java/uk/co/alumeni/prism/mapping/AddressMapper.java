package uk.co.alumeni.prism.mapping;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.address.AddressDefinition;

@Service
@Transactional
public class AddressMapper {

    public <T extends AddressDefinition<?>, V extends AddressDefinition<?>> V transform(T source, Class<V> targetClass) {
        V target = BeanUtils.instantiate(targetClass);

        target.setAddressLine1(source.getAddressLine1());
        target.setAddressLine2(source.getAddressLine2());
        target.setAddressTown(source.getAddressTown());
        target.setAddressRegion(source.getAddressRegion());
        target.setAddressCode(source.getAddressCode());

        return target;
    }

}
