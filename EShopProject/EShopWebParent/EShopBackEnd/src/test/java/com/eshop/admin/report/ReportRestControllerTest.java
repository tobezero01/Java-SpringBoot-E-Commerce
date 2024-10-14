package com.eshop.admin.report;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReportRestControllerTest {
    @Autowired private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "kanna.allada@gmail.com", password = "allada2020", authorities = "Admin")
    public void testGetReportDateLast7Days() throws Exception {
        String requestURL = "/reports/sales_by_date/last_7_days";

        mockMvc.perform(get(requestURL)).andExpect(status().isOk()).andDo(print());

    }
}
