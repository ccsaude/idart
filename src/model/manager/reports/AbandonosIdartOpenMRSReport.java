/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.manager.reports;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.manager.AdministrationManager;
import model.manager.excel.conversion.exceptions.ReportException;
import static model.manager.reports.AbstractJasperReport.getBeginningOfDay;
import org.ccs.openmrs.migracao.entidades.GlobalProperty;
import org.ccs.openmrs.migracao.entidades.Location;
import org.ccs.openmrs.migracao.entidadesHibernate.ExportDao.GeralRemoteDao;
import org.ccs.openmrs.migracao.entidadesHibernate.servicos.GeralRemoteService;
import org.ccs.openmrs.migracao.entidadesHibernate.servicos.GlobalPropertyService;
import org.ccs.openmrs.migracao.entidadesHibernate.servicos.LocationService;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.Clinic;
import org.eclipse.swt.widgets.Shell;

/**
 *
 * @author colaco
 */
public class AbandonosIdartOpenMRSReport extends AbstractJasperReport {

	private final String clinicName;
	private final Date startDate;
	private final Date endDate;
        

	public AbandonosIdartOpenMRSReport(Shell parent, String clinicName,
			Date theStartDate, Date theEndDate) {
		super(parent);
		this.clinicName = clinicName;
		this.startDate = getBeginningOfDay(theStartDate);
		this.endDate = getEndOfDay(theEndDate);
	}

	@Override
	protected void generateData() throws ReportException {
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		Clinic c = AdministrationManager.getClinic(hSession, clinicName);
                GeralRemoteService geralRemoteService = new GeralRemoteService();
                LocationService locationService = new LocationService();
                GlobalPropertyService globalPropertyService = new GlobalPropertyService();
                GlobalProperty globalProperty = globalPropertyService.findByDefaultName();
                Location location = locationService.findById(globalProperty.getPropertyValue());
        
                List listaAbandonos = geralRemoteService.findAllAbandonos(startDate, endDate,location.getLocationId());
                System.out.println(listaAbandonos.toString().trim().replace('[','{').replace(']','}'));
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("path", getReportPath());
		map.put("clinic", clinicName);
		map.put("clinicid", new Integer(c.getId()));
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("showPatientNames", true);
                map.put("openmrsAbandonos", listaAbandonos.toString().trim().replace('[','{').replace(']','}'));
		map.put("facilityName", LocalObjects.pharmacy.getPharmacyName());
		map.put("pharmacist", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());
		return map;
	}

	@Override
	protected String getReportFileName() {
		return "RelatorioAbandonosIdartvsOpenMRSPersonalidado";
	}

}
