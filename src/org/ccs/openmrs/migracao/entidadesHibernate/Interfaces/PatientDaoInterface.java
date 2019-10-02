/*
 * Decompiled with CFR 0_114.
 */
package org.ccs.openmrs.migracao.entidadesHibernate.Interfaces;

import java.io.Serializable;
import java.util.List;
import org.ccs.openmrs.migracao.swingreverse.SyncDispensasFarmac;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.SyncTempDispense;
import org.celllife.idart.database.hibernate.SyncTempPatient;

public interface PatientDaoInterface<T, Id extends Serializable> {
    public void persist(T var1);

    public void update(T var1);

    public T findById(Id var1);

    public void delete(T var1);

    public List<T> findAll();
    
    public List<SyncTempPatient> findAllImport();
    
    List<SyncTempDispense> findAllExportedFromPatient(String T,Patient p);

    public void deleteAll();
}

