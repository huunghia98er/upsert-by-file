package org.mos.vcs.service.component;

import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mos.vcs.dto.UserDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author: HuuNghia
 * @LastModified: 2024/12/03
 */

@Slf4j
public class ExcelHelper implements FileHelper {

    private ExcelHelper() {
    }

    public static ExcelHelper getInstance() {
        return ExcelHelperHolder.INSTANCE;
    }

    @Override
    public List<UserDto> read(InputStream inputStream) throws IOException, CsvException {
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(new XSSFWorkbook(inputStream), 100);

        Sheet sheet = sxssfWorkbook.getSheetAt(0);

        for (Row row : sheet) {
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING:
                        log.info(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        log.info(String.valueOf(cell.getNumericCellValue()));
                        break;
                    case _NONE:
                        log.info("None");
                        break;
                    case FORMULA:
                        log.info(cell.getCellFormula());
                        break;
                    case BLANK:
                        log.info("Blank");
                        break;
                    case BOOLEAN:
                        log.info(String.valueOf(cell.getBooleanCellValue()));
                        break;
                    case ERROR:
                        log.info(String.valueOf(cell.getErrorCellValue()));
                        break;
                    default:
                        log.info("Unknown cell type");
                        break;
                }
            }
        }
        return List.of();
    }

    @Override
    public void write(List<String[]> list, String filePath) throws IOException {

    }

    private static class ExcelHelperHolder {
        private static final ExcelHelper INSTANCE = new ExcelHelper();
    }
}
