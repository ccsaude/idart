/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 */
package org.ccs.openmrs.migracao.entidadesHibernate.importPatient;

import java.util.List;
import org.ccs.openmrs.migracao.entidadesHibernate.ImportDao.PatientImportDao;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.SyncTempDispense;
import org.celllife.idart.database.hibernate.SyncTempPatient;

public class PatientImportService {

    private static PatientImportDao patientImportDao;

    public PatientImportService() {
        patientImportDao = new PatientImportDao();
    }

    public void persist(Patient entity) {
        patientImportDao.openCurrentSessionwithTransaction();
        patientImportDao.persist(entity);
        patientImportDao.closeCurrentSessionwithTransaction();
    }

    public void update(Patient entity) {
        patientImportDao.openCurrentSessionwithTransaction();
        /*
        Raising Null exception- 04/04/2017 Agnaldo - colaco       */
        //patientImportDao.openCurrentSession();
        patientImportDao.update(entity);
        //patientImportDao.closeCurrentSession();
        patientImportDao.closeCurrentSessionwithTransaction();
    }

    public Patient findById(Integer id) {
        patientImportDao.openCurrentSession();
        Patient patient = patientImportDao.findById(id);
        patientImportDao.closeCurrentSession();
        return patient;
    }

    public Patient findByPatientId(String id) {
        patientImportDao.openCurrentSession();
        Patient patient = patientImportDao.findByPatientId(id);
        patientImportDao.closeCurrentSession();
        return patient;
    }

    public void delete(Integer id) {
        patientImportDao.openCurrentSessionwithTransaction();
        Patient patient = patientImportDao.findById(id);
        patientImportDao.delete(patient);
        patientImportDao.closeCurrentSessionwithTransaction();
    }

    public List<Patient> findAll() {
        patientImportDao.openCurrentSession();
        List<Patient> patients = patientImportDao.findAll();
        patientImportDao.closeCurrentSession();
        return patients;
    }

    public List<SyncTempPatient> findAllImport() {
        patientImportDao.openCurrentSession();
        List<SyncTempPatient> patients = patientImportDao.findAllImport();
        patientImportDao.closeCurrentSession();
        return patients;
    }

    public List<SyncTempDispense> findAllExportedFromPatient(String clinicName,Patient patient) {
        patientImportDao.openCurrentSession();
        List<SyncTempDispense> patients = patientImportDao.findAllExportedFromPatient(clinicName,patient);
        patientImportDao.closeCurrentSession();
        return patients;
    }

    public List<Patient> findAllPatientFromClinic(String clinicName) {
        patientImportDao.openCurrentSession();
        List<Patient> patients = patientImportDao.findAllPatientFromClinic(clinicName);
        patientImportDao.closeCurrentSession();
        return patients;
    }
    
     public Patient findByPatientUuid(String uuid) {
        patientImportDao.openCurrentSession();
        Patient patient = patientImportDao.findByPatientUuid(uuid);
        patientImportDao.closeCurrentSession();
        return patient;
    }
    
    
    public void deleteAll() {
        patientImportDao.openCurrentSessionwithTransaction();
        patientImportDao.deleteAll();
        patientImportDao.closeCurrentSessionwithTransaction();
    }

    public PatientImportDao patientImportDao() {
        return patientImportDao;
    }
}
