package com.eshop.admin.user.export;

import com.eshop.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AbstractExporter {
    public void setResponseHeader(HttpServletResponse response, String contentType , String extension ) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timeStamp = dateFormat.format(new Date());

        String fileName = "users_" + timeStamp + extension;

        response.setContentType(contentType);

        String headerKeyString = "Content-Disposition";
        String headerValue = "attachment: filename=" + fileName;

        response.setHeader(headerKeyString, headerValue);

    }
}
