/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ccs.openmrs.migracao.swingreverse;

import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import model.manager.AdministrationManager;
import model.manager.PatientManager;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.ccs.farmac.JBackupController;
import org.ccs.farmac.JRestoreController;
import org.ccs.farmac.PasswordProtectedZip;
import org.ccs.openmrs.migracao.connection.hibernateConection;

import org.ccs.openmrs.migracao.entidadesHibernate.importPatient.PatientImportService;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.AttributeType;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.IdentifierType;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.SyncTempPatient;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.hibernate.Session;

/**
 *
 * @author colaco
 */
public class Task4 extends SwingWorker<String, Void> {

    private final Random rnd = new Random();

    Task4() {
    }

    @Override
    public String doInBackground() {
        System.err.println("AGUARDE UM MOMENTO ....");

        PatientImportService patientImportService = new PatientImportService();
        Clinic clinic = AdministrationManager.getMainClinic(patientImportService.patientImportDao().openCurrentSessionwithTransaction());
        IdentifierType identifierType = AdministrationManager.getNationalIdentifierType(patientImportService.patientImportDao().openCurrentSessionwithTransaction());
        AttributeType attributeType = PatientManager.getAttributeTypeObject(patientImportService.patientImportDao().openCurrentSessionwithTransaction(), "ARV Start Date");
        List<SyncTempPatient> patientList = patientImportService.findAllImport();

        int current = 0;
        int lengthOfTask = patientList.size();

        try {

            PasswordProtectedZip pzip = new PasswordProtectedZip();
            JFileChooser jfc = new JFileChooser(System.getProperty("user.home") + File.separator + "Dropbox" + File.separator + "FARMAC" + File.separator+ "REFERENCIAS" + File.separator, FileSystemView.getFileSystemView());
            jfc.setDialogTitle("Seleccione o ficheiro: ");
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.setAcceptAllFileFilterUsed(true);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(".zip", ".7zip");
            jfc.addChoosableFileFilter(filter);
            int returnValue = jfc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                if (jfc.getSelectedFile().isDirectory()) {
                    System.out.println("You selected the directory: " + jfc.getSelectedFile());
                }
            }

            System.err.println("PROCESSANDO ....");

            try {
                pzip.unCompressPasswordProtectedFiles(jfc.getSelectedFile());
            } catch (ZipException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            JRestoreController restore = new JRestoreController();
            restore.executeCommand(iDartProperties.hibernateDatabase, iDartProperties.hibernatePassword, "restore", iDartProperties.hibernateTableImport, jfc.getSelectedFile().getPath().replaceAll(".zip", ""), jfc.getSelectedFile());
            while (current <= lengthOfTask && !this.isCancelled()) {
                try {
                    Thread.sleep(this.rnd.nextInt(50) + 1);
                    if (lengthOfTask == 0) {
                        System.err.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        System.err.println("#### Sem Pacientes Listados para a Migracao ####");
                        return "Done";
                    }

                    if (new File(jfc.getSelectedFile().getPath().replace(".zip", "")).exists()) {
                        try {
                            FileUtils.deleteDirectory(new File(jfc.getSelectedFile().getPath().replace(".zip", "")));

                            // Inicia a Insercao de pacientes no Idart
                            for (SyncTempPatient patientSync : patientList) {
                                ++current;
                                Patient paciente = DadosPacienteFarmac.InserePaciente(patientSync, clinic);
                                if (paciente != null) {
                                    DadosPacienteFarmac.InserePatientIdentifier(paciente, identifierType);
                                    DadosPacienteFarmac.InserePatientAttribute(paciente, patientSync.getDataInicioTarv(), attributeType);
                                }
                            }

                        } catch (IOException ex) {
                            Logger.getLogger(JBackupController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.err.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        System.err.println("" + lengthOfTask + " Pacientes Importados para o IDART com sucesso!!!!!!");
                        //hibernateConection.getInstanceLocal().close();
                        current = lengthOfTask * 2;
                    }
                } catch (Exception e) {
                    System.err.println("ACONTECEU UM ERRO INESPERADO, Contacte o Administrador \n" + e);
                    e.printStackTrace();
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
