package com.purebred.core.util;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.type.Type;

import java.util.Properties;

public class TableNameSequenceGenerator extends SequenceGenerator {
     public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
        if(params.getProperty(SEQUENCE) == null || params.getProperty(SEQUENCE).length() == 0) {
            String tableName = params.getProperty(PersistentIdentifierGenerator.TABLE);
            if(tableName != null) {
                String seqName = "SEQ_" + tableName;
                params.setProperty(SEQUENCE, seqName);
            }
        }
        super.configure(type, params, dialect);
    }
}
