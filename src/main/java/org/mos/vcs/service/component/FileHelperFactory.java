package org.mos.vcs.service.component;

/**
 * @Author: HuuNghia
 * @LastModified: 2024/11/15
 */
public class FileHelperFactory {

    public static FileHelper getFileHelper(String fileType) {
        return switch (fileType) {
            case "application/vnd.ms-excel" -> ExcelHelper.getInstance();
            default -> CsvHelper.getInstance();
        };
    }

}
