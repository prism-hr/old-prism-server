package uk.co.alumeni.prism.mapping;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.domain.Domicile;
import uk.co.alumeni.prism.domain.address.Address;
import uk.co.alumeni.prism.domain.address.AddressCoordinates;
import uk.co.alumeni.prism.domain.address.AddressDefinition;
import uk.co.alumeni.prism.rest.representation.address.AddressCoordinatesRepresentation;
import uk.co.alumeni.prism.rest.representation.address.AddressRepresentation;

import javax.transaction.Transactional;

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

    public AddressRepresentation getAddressRepresentation(Address address) {
        AddressRepresentation representation = transform(address, AddressRepresentation.class);

        Domicile domicile = address.getDomicile();
        representation.setDomicile(domicile == null ? null : domicile.getId());
        representation.setGoogleId(address.getGoogleId());

        AddressCoordinates addressCoordinates = address.getAddressCoordinates();
        if (addressCoordinates != null) {
            representation.setCoordinates(new AddressCoordinatesRepresentation().withLatitude(addressCoordinates.getLatitude()) //
                    .withLongitude(addressCoordinates.getLongitude()));
        }

        return representation;
    }

}
