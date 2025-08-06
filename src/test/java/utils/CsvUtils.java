package utils;

import com.opencsv.CSVReader;
import config.TestConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvUtils {
    public static List<String[]> readCsv(String resourcePath) {
        List<String[]> rows = new ArrayList<>();
        InputStreamReader isr = null;
        try {
            java.io.InputStream is = CsvUtils.class.getClassLoader().getResourceAsStream(resourcePath);
            if (is == null) {
                log.error("{} not found on classpath.", resourcePath);
                throw new RuntimeException(resourcePath + " not found on classpath");
            }
            isr = new InputStreamReader(is);
            CSVReader reader = new CSVReader(isr);
            String[] line;
            while ((line = reader.readNext()) != null) {
                rows.add(line);
            }
            reader.close();
        } catch (Exception e) {
            log.error("Error reading CSV {}: {}", resourcePath, e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try { if (isr != null) isr.close(); } catch (Exception e) { log.warn("Exception while closing InputStreamReader: {}", e.getMessage(), e); }
        }
        return rows;
    }

    public static List<String[]> readEnvCsv(String fileName) {
        String env = TestConfig.getProperty("env");
        String resourcePath;
        if (fileName.startsWith("testdata/")) {
            resourcePath = fileName;
        } else if (fileName.contains("/")) {
            resourcePath = fileName;
        } else {
            resourcePath = "testdata/" + env + "/" + fileName;
        }
        return readCsv(resourcePath);
    }
}
