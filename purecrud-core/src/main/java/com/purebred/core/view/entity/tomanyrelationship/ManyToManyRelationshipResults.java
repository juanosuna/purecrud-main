package com.purebred.core.view.entity.tomanyrelationship;

import com.purebred.core.dao.EntityDao;
import com.purebred.core.entity.IdentifiableEntity;
import com.purebred.core.util.BeanPropertyType;
import com.purebred.core.util.assertion.Assert;

import java.io.Serializable;

public abstract class ManyToManyRelationshipResults<T, A extends IdentifiableEntity> extends ToManyAggregationRelationshipResults<T> {

    public abstract EntityDao<A, ? extends Serializable> getAssociationDao();

    public void setReferencesToParentAndPersist(T... values) {
        for (T value : values) {
            BeanPropertyType beanPropertyType = BeanPropertyType.getBeanPropertyType(getEntityType(), getParentPropertyId());
            Assert.PROGRAMMING.assertTrue(beanPropertyType.isCollectionType(),
                    "Parent property id (" + getEntityType() + "." + getParentPropertyId() + ") must be a collection type");
            A associationEntity = createAssociationEntity(value);
            if (!getAssociationDao().isPersistent(associationEntity)) {
                getAssociationDao().persist(associationEntity);
            }
            searchImpl(false);
        }
    }

    public void valuesRemoved(T... values) {
        for (T value : values) {
            BeanPropertyType beanPropertyType = BeanPropertyType.getBeanPropertyType(getEntityType(), getParentPropertyId());
            Assert.PROGRAMMING.assertTrue(beanPropertyType.isCollectionType(),
                    "Parent property id (" + getEntityType() + "." + getParentPropertyId() + ") must be a collection type");

            A associationEntity = createAssociationEntity(value);
            getAssociationDao().remove(associationEntity);
        }
        searchImpl(false);
        removeButton.setEnabled(false);
    }

    public abstract A createAssociationEntity(T value);
}
