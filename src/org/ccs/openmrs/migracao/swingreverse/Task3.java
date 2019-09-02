/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ccs.openmrs.migracao.swingreverse;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import model.manager.AdministrationManager;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.ccs.farmac.JBackupController;
import org.ccs.farmac.PasswordProtectedZip;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.hibernate.Session;

/**
 *
 * @author colaco
 */
public class Task3 extends SwingWorker<String, Void> {

    private final Random rnd = new Random();

    Task3() {
    }

    public String convertIntegerToMonth(int month) {

        String srtMonth = "";
        switch (month) {
            case 1:
                srtMonth = "Jan";
                break;
            case 2:
                srtMonth = "Feb";
                break;
            case 3:
                srtMonth = "Mar";
                break;
            case 4:
                srtMonth = "Apr";
                break;
            case 5:
                srtMonth = "May";
                break;
            case 6:
                srtMonth = "Jun";
                break;
            case 7:
                srtMonth = "jul";
                break;
            case 8:
                srtMonth = "Aug";
                break;
            case 9:
                srtMonth = "Sep";
                break;
            case 10:
                srtMonth = "Oct";
                break;
            case 11:
                srtMonth = "Nov";
                break;
            case 12:
                srtMonth = "Dec";
                break;
            default:
                break;
        }
        return srtMonth;
    }

    @Override
    public String doInBackground() {
        System.err.println("AGUARDE UM MOMENTO ....");
        try {

            Session sess = HibernateUtil.getNewSession();
            String unidadeSanitaria = AdministrationManager.getMainClinic(sess).getClinicName().replaceAll(" ", "").trim();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            JBackupController backup = new JBackupController();
            // to backup
            PasswordProtectedZip pzip = new PasswordProtectedZip();
            File backupFilePath = new File(System.getProperty("user.home") + File.separator + "Dropbox" + File.separator + "FARMAC" + File.separator + "backup_" + sdf.format(new Date()) + "_" + unidadeSanitaria);

            System.err.println("PROCESSANDO ....");

            try {

                backup.executeCommand(iDartProperties.hibernateDatabase, iDartProperties.hibernatePassword, "backup", iDartProperties.hibernateTableImport, backupFilePath, unidadeSanitaria);
                pzip.compressWithPassword(backupFilePath.getPath());
            } catch (ZipException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (backupFilePath.exists()) {
                try {
                    FileUtils.deleteDirectory(backupFilePath);
                } catch (IOException ex) {
                    Logger.getLogger(JBackupController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            System.err.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        } catch (Exception e) {
            System.err.println("ACONTECEU UM ERRO INESPERADO, Contacte o Administrador \n" + e);
            e.printStackTrace();
        }
        return "Done";
    }
}
