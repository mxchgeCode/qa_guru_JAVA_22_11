import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import json.Meetings;


public class ReadingFilesTest {
    private ClassLoader cl = ReadingFilesTest.class.getClassLoader();

    @Test
    void readingPdfFromZip() throws Exception {
        try (InputStream zipFile = cl.getResourceAsStream("archive_for_test.zip");
             ZipInputStream filesFromZip = new ZipInputStream(zipFile)) {
            ZipEntry entry;
            while ((entry = filesFromZip.getNextEntry()) != null) {
                if (entry.getName().contains(".pdf")) {
                    PDF pdf = new PDF(filesFromZip);
                    Assertions.assertTrue(pdf.text.startsWith("PDF для теста"));
                    break;
                }
            }
        }
    }

    @Test
    void readingXlsxFromZip() throws Exception {
        try (InputStream zipFile = cl.getResourceAsStream("archive_for_test.zip");
             ZipInputStream filesFromZip = new ZipInputStream(zipFile)) {
            ZipEntry entry;
            while ((entry = filesFromZip.getNextEntry()) != null) {
                if (entry.getName().contains(".xlsx")) {
                    XLS xls = new XLS(filesFromZip);
                    Assertions.assertEquals(xls.excel.getSheetAt(0).getRow(3).getCell(0)
                            .getStringCellValue(), "Понедельник");
                    break;
                }
            }
        }
    }

    @Test
    void readingCSVFromZip() throws Exception {
        try (InputStream zipFile = cl.getResourceAsStream("archive_for_test.zip");
             ZipInputStream filesFromZip = new ZipInputStream(zipFile)) {
            ZipEntry entry;
            while ((entry = filesFromZip.getNextEntry()) != null) {
                if (entry.getName().contains(".csv")) {
                    CSVReader csvReader = new CSVReader(new InputStreamReader(filesFromZip));
                    List<String[]> csvContent = csvReader.readAll();
                    Assertions.assertArrayEquals(new String[]{"Вторник", "2"}, csvContent.get(1));
                    break;
                }
            }

        }
    }

    @Test
    void parseJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream is = cl.getResourceAsStream("meetings.json");
             InputStreamReader isr = new InputStreamReader(is)) {
            Meetings meetings = objectMapper.readValue(isr, Meetings.class);
            Assertions.assertEquals("Monday", meetings.getDay());
            Assertions.assertEquals(List.of("13:00", "15:00"), meetings.getTime());


        }
    }
}