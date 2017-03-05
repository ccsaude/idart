/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  org.hibernate.Query
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 */
package org.ccs.openmrs.migracao.entidadesHibernate.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;
import org.ccs.openmrs.migracao.connection.hibernateConection;
import org.ccs.openmrs.migracao.entidades.PatientIdentifier;
import org.ccs.openmrs.migracao.entidadesHibernate.Interfaces.PatientIdentifierDaoInterface;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PatientIdentifierDao
implements PatientIdentifierDaoInterface<PatientIdentifier, String> {
    public Session currentSession;
    public Transaction currentTransaction;

    public Session openCurrentSession() {
        this.currentSession = hibernateConection.getInstanceRemote();
        return this.currentSession;
    }

    public Session openCurrentSessionwithTransaction() {
        this.currentSession = hibernateConection.getInstanceRemote();
        this.currentTransaction = this.currentSession.beginTransaction();
        return this.currentSession;
    }

    public void closeCurrentSession() {
        this.currentSession.close();
    }

    public void closeCurrentSessionwithTransaction() {
        this.currentTransaction.commit();
        this.currentSession.close();
    }

    public Session getCurrentSession() {
        return this.currentSession;
    }

    public void setCurrentSession(Session currentSession) {
        this.currentSession = currentSession;
    }

    public Transaction getCurrentTransaction() {
        return this.currentTransaction;
    }

    public void setCurrentTransaction(Transaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

    @Override
    public void persist(PatientIdentifier entity) {
        this.getCurrentSession().save((Object)entity);
    }

    @Override
    public void update(PatientIdentifier entity) {
        this.getCurrentSession().update((Object)entity);
    }

    @Override
    public PatientIdentifier findById(String id) {
        PatientIdentifier patientIdentifier = (PatientIdentifier)this.getCurrentSession().createQuery("from PatientIdentifier p where p.identifier = '" + id+"'").uniqueResult();
        return patientIdentifier;
    }

    @Override
    public List<PatientIdentifier> findAllByNidLikeAndNameLikeAndSurnameLike(String nid,String name,String surname) {
    List<PatientIdentifier> patientIdentifiers = this.getCurrentSession().createQuery("from PatientIdentifier pa where pa.patientIdentifierId in (select p.patientIdentifierId " +
                                                                                                                                                       "from PatientIdentifier p,PersonName pn "+
                                                                                                                                                       "where p.identifier like '%" +nid+"%' "+
                                                                                                                                                       "AND pn.givenName like '%"+name+"%' "+
                                                                                                                                                       "AND pn.familyName like '%"+surname+"%' "+
                                                                                                                                                       "AND p.patientId = pn.personId)").list();
        return patientIdentifiers;
    }
    
     @Override
    public List<PatientIdentifier> findByNidAndNameAndSurname(String nid,String name,String surname) {
         List<PatientIdentifier> patientIdentifiers = this.getCurrentSession().createQuery("from PatientIdentifier pa where pa.patientIdentifierId in (select p.patientIdentifierId " +
                                                                                                                                                       "from PatientIdentifier p,PersonName pn "+
                                                                                                                                                       "where p.identifier = '" +nid+"' "+
                                                                                                                                                       "AND pn.givenName like '%"+name+"%' "+
                                                                                                                                                       "AND pn.familyName like '%"+surname+"%' "+
                                                                                                                                                       "AND p.patientId = pn.personId)").list();
        return patientIdentifiers;
    }
    
    @Override
      public List<PatientIdentifier> findByAllIdentifierLike(String id) {
        List<PatientIdentifier>  patientIdentifiers = this.getCurrentSession().createQuery("from PatientIdentifier p where p.identifier like '%"+id+"%'").list();
        return patientIdentifiers;
    }
    
    @Override
    public PatientIdentifier findByPatientId(String id) {
        PatientIdentifier patientIdentifier = null;
      
        patientIdentifier = (PatientIdentifier)this.getCurrentSession().createQuery("from PatientIdentifier p where p.patientId = " + Integer.parseInt(id) + " AND p.identifierType = 2").uniqueResult();
      
        if (patientIdentifier == null) {
            patientIdentifier = (PatientIdentifier)this.getCurrentSession().createQuery("from PatientIdentifier p where p.patientId = " + Integer.parseInt(id) + " AND p.identifierType = 9 ").uniqueResult();
        }
        if (patientIdentifier == null) {
            patientIdentifier = (PatientIdentifier)this.getCurrentSession().createQuery("from PatientIdentifier p where p.patientId = " + Integer.parseInt(id) + " AND p.identifierType = 6 ").uniqueResult();
        }
        if (patientIdentifier == null) {
            patientIdentifier = (PatientIdentifier)this.getCurrentSession().createQuery("from PatientIdentifier p where p.patientId = " + Integer.parseInt(id) + " AND p.identifierType = 11 ").uniqueResult();
        }
        return patientIdentifier;
    }

    @Override
    public void delete(PatientIdentifier entity) {
        this.getCurrentSession().delete((Object)entity);
    }

    @Override
    public List<PatientIdentifier> findAll() {
        List patientIdentifiers = this.getCurrentSession().createQuery("from PatientIdentifier").list();
        return patientIdentifiers;
    }

    @Override
    public void deleteAll() {
        List<PatientIdentifier> entityList = this.findAll();
        for (PatientIdentifier entity : entityList) {
            this.delete(entity);
        }
    }
}

