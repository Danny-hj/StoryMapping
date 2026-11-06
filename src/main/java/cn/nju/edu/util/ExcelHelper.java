package cn.nju.edu.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by away on 2017/11/22.
 */
@Component
public class ExcelHelper {

    private static final Logger log = LoggerFactory.getLogger(ExcelHelper.class);

    public static final String XLS = "xls";
    public static final String XLSX = "xlsx";

    private File file;
    private Workbook workbook;

    public Workbook openFile(File file) {
        try {
            String fileName = file.getName();
            workbook = null;

            //获取excel文件的io流
            InputStream is = new FileInputStream(file);
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if(fileName.endsWith(XLS)){
                //2003
                workbook = new HSSFWorkbook(is);
            }else if(fileName.endsWith(XLSX)){
                //2007
                workbook = new XSSFWorkbook(is);
            }
            this.file = file;
            is.close();
        } catch (IOException e) {
            log.warn("Failed to open workbook {}", file, e);
            return null;
        }
        return workbook;
    }

    /**
     * 读取指定 sheet
     * @param sheetIndex 第几个 sheet
     * @return 读取失败 --> 空的 List
     *         读取成功 --> 包含本 sheet 数据的 List
     */
    public List<List<String>> getSheet(int sheetIndex) {
        List<List<String>> sheetList = new ArrayList<>();
        if (workbook == null) {
            return sheetList;
        }
        Sheet sheetAt = workbook.getSheetAt(sheetIndex);
        int startRow = sheetAt.getFirstRowNum();
        int endRow = sheetAt.getLastRowNum();
        for (int i = startRow; i <= endRow; i++) {
            List<String> rowList = getRow(sheetIndex, i);
            sheetList.add(rowList);
        }
        return sheetList;
    }


    /**
     * 读取指定 sheet 的指定行
     * @param sheetIndex 第几个 sheet
     * @param rowIndex 读取第几行
     * @return 读取失败 --> 空的 List
     *         读取成功 --> 包含本行数据的 List
     */
    public List<String> getRow(int sheetIndex, int rowIndex) {
        List<String> rowList = new ArrayList<>(4);
        if (workbook == null) {
            return rowList;
        }
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        Row row = sheet.getRow(rowIndex);
        int start = row.getFirstCellNum();
        int end = row.getLastCellNum();
        for (int i = start; i < end; i++) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                rowList.add(getStringCellValue(cell));
            }
        }
        return rowList;
    }

    /**
     * 如何使用 参见 main
     * 写入 excel
     * @param content  excel 内容
     * @param dir 生成 excel 的目录
     */
    public void writeExcel(List<String[]> content, String dir) {
        Workbook wb = new XSSFWorkbook();
        try {
            Sheet sheet = wb.createSheet();
            for (int i = 0; i < content.size(); i++) {
                Row row = sheet.createRow(i);
                String[] rowContent = content.get(i);
                if (rowContent == null) {
                    continue;
                }
                for (int j = 0; j < rowContent.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(rowContent[j] == null ? "" : rowContent[j]);
                }
            }
            File output = new File(dir);
            File parent = output.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                log.warn("Failed to create directory {}", parent);
            }
            try (FileOutputStream out = new FileOutputStream(output)) {
                wb.write(out);
            }
        } catch (IOException e) {
            log.warn("Failed to write workbook {}", dir, e);
        } finally {
            try {
                wb.close();
            } catch (IOException e) {
                log.warn("Failed to close workbook", e);
            }
        }
    }

    public boolean close() {
        try {
            workbook.close();
        } catch (IOException e) {
            log.warn("Failed to close workbook", e);
            return false;
        }
        this.file = null;
        this.workbook = null;
        return true;
    }

    private String getStringCellValue(Cell cell) {
        CellType cellType = cell.getCellType();
        if (cellType == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }
        if (cellType == CellType.BLANK || cellType == CellType.ERROR) {
            return null;
        }
        return new DataFormatter().formatCellValue(cell);
    }

    public static void main(String[] args) throws IOException {
        ExcelHelper excelHelper = new ExcelHelper();
        String[] a = new String[3];
        a[0] = "1";
        a[1] = "2";
        a[2] = "4";
        List<String[]> test = new ArrayList<>();
        test.add(a);
        excelHelper.writeExcel(test, "data/export/score.xlsx");
    }
}