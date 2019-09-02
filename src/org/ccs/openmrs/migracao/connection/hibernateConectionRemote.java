/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.cfg.AnnotationConfiguration
 *  org.hibernate.cfg.Configuration
 *  org.hibernate.classic.Session
 */
package org.ccs.openmrs.migracao.connection;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class hibernateConectionRemote {
    private static SessionFactory sessionFactoryRemote;
    private static final ThreadLocal<org.hibernate.Session> threadRemote;


    public static org.hibernate.Session getInstanceRemote() {
        org.hibernate.Session session = threadRemote.get();
        session = sessionFactoryRemote.openSession();
        threadRemote.set(session);
        return session;
    }

     static {
        threadRemote = new ThreadLocal();
        try {
            sessionFactoryRemote = new AnnotationConfiguration().configure("org/ccs/openmrs/migracao/connection/hibernateRemote.cfg.xml").buildSessionFactory();
        }
        catch (Throwable e) {
            System.err.println("Failed to create sessionFactory object." + e);
            throw new ExceptionInInitializerError(e);
        }
    }
}

