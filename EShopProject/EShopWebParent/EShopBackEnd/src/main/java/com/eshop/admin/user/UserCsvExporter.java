package com.eshop.admin.user;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;

import com.eshop.common.entity.User;

import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

public class UserCsvExporter {

	//file download can open with excel
	public void export(List<User> listUsers, HttpServletResponse response ) throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String timeStamp = dateFormat.format(new Date());
		
		String fileName = "users_" + timeStamp + ".csv";
		
		response.setContentType("text/csv");
		
		String headerKeyString = "Content-Disposition";
		String headerValue = "attachment: filename=" + fileName;

		response.setHeader(headerKeyString,headerValue);
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
