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

public class hibernateConection {
    private static SessionFactory sessionFactoryLocal;
     private static final ThreadLocal<org.hibernate.Session> threadLocal;

    public static org.hibernate.Session getInstanceLocal() {
        org.hibernate.Session session = threadLocal.get();
        session = sessionFactoryLocal.openSession();
        threadLocal.set(session);
        return session;
    }
    static {
        threadLocal = new ThreadLocal();
        try {
            sessionFactoryLocal = new AnnotationConfiguration().configure("org/ccs/openmrs/migracao/connection/hibernate.cfg.xml").buildSessionFactory();
        }
        catch (Throwable e) {
            System.err.println("Failed to create sessionFactory object." + e);
            throw new ExceptionInInitializerError(e);
        }
       
    }
     
}

