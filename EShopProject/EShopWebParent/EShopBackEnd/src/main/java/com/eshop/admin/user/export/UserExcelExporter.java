package com.eshop.admin.user.export;


import com.eshop.common.entity.User;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.util.List;

public class UserExcelExporter extends AbstractExporter {

	//file download can open with excel

	private XSSFWorkbook workbook ;
	private XSSFSheet sheet ;

	public UserExcelExporter() {
		workbook = new XSSFWorkbook();
	}

	private void writeHeaderLine() {
		sheet = workbook.createSheet("Users");
		XSSFRow row = sheet.createRow(0);

		XSSFCellStyle cellStyle = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();

		font.setBold(true);
		font.setFontHeight(14);
		cellStyle.setFont(font);

		createCell(row, 0,"User Id" , cellStyle);
		createCell(row, 1,"Email" , cellStyle);
		createCell(row, 2,"First Name" , cellStyle);
		createCell(row, 3,"Last Name" , cellStyle);
		createCell(row, 4,"Roles" , cellStyle);
		createCell(row, 5,"Enabled" , cellStyle);

	}

	private void createCell(XSSFRow row, int columIndex , Object value, CellStyle cellStyle) {
		XSSFCell cell = row.createCell(columIndex);
		sheet.autoSizeColumn(columIndex);
		if(value instanceof Integer) {
			cell.setCellValue((Integer)value);
		}else if(value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		}else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(cellStyle);
	}

	private void writeDataLine(List<User> listUsers) {
		int rowIndex = 1;

		XSSFCellStyle cellStyle = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(12);
		cellStyle.setFont(font);

		for (User user : listUsers) {
			XSSFRow row = sheet.createRow(rowIndex++);
			int indexColumn = 0;
			createCell(row, indexColumn++, user.getId(), cellStyle);
			createCell(row, indexColumn++, user.getEmail(), cellStyle);
			createCell(row, indexColumn++, user.getFirstName(), cellStyle);
			createCell(row, indexColumn++, user.getLastName(), cellStyle);
			createCell(row, indexColumn++, user.getRoles().toString(), cellStyle);
			createCell(row, indexColumn, user.isEnabled(), cellStyle);
		}
	}
	public void export(List<User> listUsers, HttpServletResponse response ) throws IOException {
		super.setResponseHeader(response, "application/octet-stream", ".xlsx", "users_");

		writeHeaderLine();
		writeDataLine(listUsers);

		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
	}

}
