package com.kvant.service;

import com.kvant.entity.Lead;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelService {

    public byte[] generateLeadExcel(Lead lead) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Заявка");
            Row header = sheet.createRow(0);
            String[] columns = {"Дата", "Имя", "Телефон", "Email", "Услуга", "Комментарий"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            Row data = sheet.createRow(1);
            data.createCell(0).setCellValue(lead.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            data.createCell(1).setCellValue(lead.getFirstName() + (lead.getLastName() != null ? " " + lead.getLastName() : ""));
            data.createCell(2).setCellValue(lead.getPhone());
            data.createCell(3).setCellValue(lead.getEmail());
            data.createCell(4).setCellValue(lead.getServiceType());
            data.createCell(5).setCellValue(lead.getComment() != null ? lead.getComment() : "");

            for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    public byte[] generateAllLeadsExcel(List<Lead> leads) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Заявки");
            Row header = sheet.createRow(0);
            String[] columns = {"ID", "Дата", "Имя", "Фамилия", "Телефон", "Email", "Услуга", "Комментарий", "Статус", "UTM Source", "UTM Campaign"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowNum = 1;
            for (Lead lead : leads) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(lead.getId());
                row.createCell(1).setCellValue(lead.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                row.createCell(2).setCellValue(lead.getFirstName() != null ? lead.getFirstName() : "");
                row.createCell(3).setCellValue(lead.getLastName() != null ? lead.getLastName() : "");
                row.createCell(4).setCellValue(lead.getPhone());
                row.createCell(5).setCellValue(lead.getEmail());
                row.createCell(6).setCellValue(lead.getServiceType() != null ? lead.getServiceType() : "");
                row.createCell(7).setCellValue(lead.getComment() != null ? lead.getComment() : "");
                row.createCell(8).setCellValue(lead.getStatus() != null ? lead.getStatus() : "");
                row.createCell(9).setCellValue(lead.getUtmSource() != null ? lead.getUtmSource() : "");
                row.createCell(10).setCellValue(lead.getUtmCampaign() != null ? lead.getUtmCampaign() : "");
            }

            for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }
}