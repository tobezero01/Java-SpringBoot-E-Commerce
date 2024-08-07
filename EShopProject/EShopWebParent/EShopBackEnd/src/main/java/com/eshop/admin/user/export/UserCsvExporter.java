package com.eshop.admin.user.export;


import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;

import com.eshop.common.entity.User;

import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

public class UserCsvExporter extends AbstractExporter {

	//file download can open with excel
	public void export(List<User> listUsers, HttpServletResponse response ) throws IOException {
		super.setResponseHeader(response,"text/csv",".csv" );
		ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

		String[] csvHeader = {"User Id", "Email", "First Name", "last Name" , "Roles", "Enabled"};
		String[] fieldMapping = {"id", "email", "firstName", "lastName" , "roles", "enabled"};

		csvBeanWriter.writeHeader(csvHeader);
		for (User user : listUsers) {
			csvBeanWriter.write(user, fieldMapping);
		}
		
		csvBeanWriter.close();
	}
}
