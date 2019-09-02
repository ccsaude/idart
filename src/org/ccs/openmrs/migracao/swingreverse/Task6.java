/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ccs.openmrs.migracao.swingreverse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import model.manager.AdministrationManager;
import model.manager.PatientManager;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.ccs.farmac.JRestoreController;
import org.ccs.farmac.PasswordProtectedZip;
import org.ccs.openmrs.migracao.connection.hibernateConection;
import org.ccs.openmrs.migracao.entidadesHibernate.ExportDispense.PackageDrugInfoExportService;
import org.ccs.openmrs.migracao.entidadesHibernate.importPatient.PatientImportService;
import org.ccs.openmrs.migracao.entidadesHibernate.servicos.PatientIdentifierService;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.AttributeType;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.IdentifierType;
import org.celllife.idart.database.hibernate.SyncTempDispense;
import org.celllife.idart.database.hibernate.User;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.packaging.NewPatientPackaging;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Session;

/**
 *
 * @author colaco
 */
public class Task6 extends SwingWorker<String, Void> {

    //Lista dos possiveis Localizacao do logFile
    final static List<String> logFileLocations = new ArrayList<>();
    final static String logFileName = "EnvioDispensasLogFile.txt";
    private final Random rnd = new Random();
    // Esta classe vai ler e escrever um logFile  com os detalhe das excecpiotns que podem ocorrer
    // durante o processo de uniao de nids. O ficheiro deve ser criado na pasta de instalacao do idart que pode ser
    // C:\\idart ou C:\\Program Files\\idart ou C:\\Program Files (x86)\\idart.
    ReadWriteTextFile rwTextFile = new ReadWriteTextFile();
    String logFile;
    
    Task6() {

//        Alterado para carregar o directorio do Projecto para qualquer Sistema Operacional
//        logFileLocations.add("C:\\iDART_V2017");
//        logFileLocations.add("C:\\Program Files\\idart");
//        logFileLocations.add("C:\\Program Files (x86)\\idart");
//        logFileLocations.add("C:\\idart");
//        logFileLocations.add("C:\\Idart");
        logFileLocations.add(System.getProperty("user.dir"));
        System.out.println(System.getProperty("user.dir"));
    }
    
    @Override
    public String doInBackground() {
        System.err.println("AGUARDE UM MOMENTO ....");
        try {
            
            PasswordProtectedZip pzip = new PasswordProtectedZip();
            JFileChooser jfc = new JFileChooser(System.getProperty("user.home") + File.separator + "Dropbox" + File.separator + "FARMAC" +  File.separator + "DISPENSAS" + File.separator, FileSystemView.getFileSystemView());
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
            
             try {
                pzip.unCompressPasswordProtectedFiles(jfc.getSelectedFile());
            } catch (ZipException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            JRestoreController restore = new JRestoreController();
            restore.executeCommand(iDartProperties.hibernateDatabase, iDartProperties.hibernatePassword, "restore", iDartProperties.hibernateTableExport, jfc.getSelectedFile().getPath().replaceAll(".zip", ""), jfc.getSelectedFile());

             if (new File(jfc.getSelectedFile().getPath().replace(".zip", "")).exists()) {
                        try {
                            FileUtils.deleteDirectory(new File(jfc.getSelectedFile().getPath().replace(".zip", "")));
                        }catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
             }

            
            PackageDrugInfoExportService packageDrugInfoExportService = new PackageDrugInfoExportService();
            PatientImportService patientImportService = new PatientImportService();
            Clinic clinic = AdministrationManager.getMainClinic(patientImportService.patientImportDao().openCurrentSessionwithTransaction());
            IdentifierType identifierType = AdministrationManager.getNationalIdentifierType(patientImportService.patientImportDao().openCurrentSessionwithTransaction());
            AttributeType attributeType = PatientManager.getAttributeTypeObject(patientImportService.patientImportDao().openCurrentSessionwithTransaction(), "ARV Start Date");
            User user = AdministrationManager.getUserByName(patientImportService.patientImportDao().openCurrentSessionwithTransaction(), "Admin");
            List<SyncTempDispense> dispensasFarmacs = patientImportService.findAllExported();
            
            System.err.println("PROCESSANDO ....");
            
            int current = 0;
            int contanonSend = 0;
            int lengthOfTask = dispensasFarmacs.size();
            int pacientesEmTransito = 0;
            //Esvazia o log file
            logFile = getLogFileLocation();
            Session sess = HibernateUtil.getNewSession();
            
            try {
                Thread.sleep(this.rnd.nextInt(50) + 1);
                if (lengthOfTask == 0) {
                    System.err.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    System.err.println("#### Sem Dispensas Listadas para a Migracao ####");
                    return "Done";
                }
                //   Users users = usersService.findById("1");

                for (SyncTempDispense dispensasFarmac : dispensasFarmacs) {
                    ++current;

                    // os erros vao para o logfile
                    try {
                        this.setProgress(100 * current / lengthOfTask);
                        
                        org.celllife.idart.database.hibernate.Patient importedPatient = patientImportService.findByPatientId(dispensasFarmac.getPatientid());
                        
                        if (importedPatient != null) {
                            
                            PatientIdentifierService identifierDao = new PatientIdentifierService();
                            Shell parent = null;
                            NewPatientPackaging newPatientPackaging = new NewPatientPackaging(parent);
                            newPatientPackaging.saveDispenseFarmacQty0(dispensasFarmac, current, clinic, user);
                            
//                            PackageDrugInfoExportService rapidSave = new PackageDrugInfoExportService();
//                            packageDrugInfo.setNotes("Exported");
//                            rapidSave.update(packageDrugInfo);
                            
                            System.err.println("**********************************************************************************************************************************************************************************");
                            System.err.println(" Dispensa do Paciente " + importedPatient.getFirstNames() + " " + importedPatient.getLastname() + " com o nid NID " + importedPatient.getPatientId() + " Enviado para o IDART.");
                            System.err.println("**********************************************************************************************************************************************************************************");
                            
                        }
                        
                    } catch (NullPointerException nl) {
                        System.out.println("Null pointer exception: " + nl.getCause().toString());
                    } catch (Exception e) {
                        // Podem ocorrer diferentes tipos de exceptions, coomo nao podemos prever todas vamos escreve-las
                        //num logfile e continuar com a execucao ciclo   
                        List<String> listNidsProblematicos = new ArrayList<>();
                        listNidsProblematicos.add("---------------------------------------------------------------------- ----------------------------------------------------------------");
                        listNidsProblematicos.add("NID: " + dispensasFarmac.getPatientid());
                        listNidsProblematicos.add("NOME: " + dispensasFarmac.getPatientfirstname());
                        listNidsProblematicos.add("APELIDO: " + dispensasFarmac.getPatientlastname());
                        listNidsProblematicos.add("ERRO: " + e.getMessage());
                        rwTextFile.writeSmallTextFile(listNidsProblematicos, logFile);
                    }
                    
                }
                
            } catch (InterruptedException ie) {
                return "Interrupted";
            }
            System.err.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.err.println("" + (lengthOfTask - pacientesEmTransito) + " Total de dispensas por Enviar ao OpenMRS!!!!!!");
            if (contanonSend != 0) {
                System.err.println("" + contanonSend + " Dispensas nao actualizadas no OpenMRS!!!!!!");
            }
            if ((lengthOfTask - contanonSend) != 0) {
                System.err.println("" + (lengthOfTask - contanonSend - pacientesEmTransito) + " Dispensas Actualizadas no OpenMRS com Sucesso!!!!!!");
            }
            hibernateConection.getInstanceLocal().close();
            current = lengthOfTask * 2;

        } catch (Exception e) {
            System.err.println("ACONTECEU UM ERRO INESPERADO, Ligue o Servidor OpenMRS e Tente Novamente ou Contacte o Administrador \n" + e.getMessage());
        }
        return "Done";
    }
    
    public String getLogFileLocation() {
        
        String fileLocation = "";
        
        for (int i = 0; i < logFileLocations.size(); i++) {
            
            File dir = new File(logFileLocations.get(i));

            //Se o Ficheiro nao e encontrado em nenhuma das locations criar o ficheiro
            // No directorio que existir
            if (dir.exists()) {
                File logFile = new File(logFileLocations.get(i) + "\\" + logFileName);
                if (logFile.exists()) {
                    try {
                        logFile.delete();
                        logFile.createNewFile();
                        fileLocation = logFile.getPath();
                        break;
                    } catch (IOException e) {
                        System.out.println("cannot create log file" + e.getMessage());
                    }
                } //create new file
                else {
                    try {
                        
                        logFile.createNewFile();
                        fileLocation = logFile.getPath();
                        System.out.println(fileLocation + ":  Criado");
                        break;
                        
                    } catch (IOException e) {
                        System.out.println("cannot create log file" + e.getMessage());
                    }
                    
                }
                
            }
            
        }
        
        return fileLocation;
        
    }
    
}
