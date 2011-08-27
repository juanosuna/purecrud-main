package com.purebred.core.dao;

import com.purebred.core.entity.IdentifiableEntity;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public class GenericEntityDao extends EntityDao<IdentifiableEntity, Serializable> {
}
