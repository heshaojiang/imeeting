package com.github.pig.admin.service.impl;

import com.github.pig.common.util.exception.GrgException;
import com.github.pig.common.util.RespCode;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
/**
 * @author fmsheng
 * @description
 * @date 2018/10/23 14:14
 * @modified by
 */
@Service
public class ImportServiceImpl{


    /**
     * 处理上传的文件
     *
     * @param in
     * @param fileName
     * @return
     * @throws Exception
     */
    public List getBankListByExcel(InputStream in, String fileName) throws Exception {
        List list = new ArrayList<>();
        //创建Excel工作薄
        Workbook work = this.getWorkbook(in, fileName);
        if (null == work) {
            throw new GrgException(RespCode.CNSL_UPLOAD_FILE_PARSE_ERROR);
        }
        Sheet sheet = null;
        Row row = null;
        Cell cell = null;

        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            sheet = work.getSheetAt(i);
            if (sheet == null) {
                continue;
            }

            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                row = sheet.getRow(j);
                if (row == null || row.getFirstCellNum() == j) {
                    continue;
                }

                List<Object> li = new ArrayList<>();
                for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
                    cell = row.getCell(y);
                    li.add(cell);
                }
                list.add(li);
            }
        }
        work.close();
        return list;
    }

    /**
     * 判断文件格式
     *
     * @param inStr
     * @param fileName
     * @return
     * @throws Exception
     */
    public Workbook getWorkbook(InputStream inStr, String fileName) throws Exception {
        Workbook workbook = null;
        try
        {
            String fileType = fileName.substring(fileName.lastIndexOf("."));
            if (".xls".equals(fileType)) {
                workbook = new HSSFWorkbook(inStr);
            } else if (".xlsx".equals(fileType)) {
                workbook = new XSSFWorkbook(inStr);
            } else {
                throw new GrgException(RespCode.CNSL_UPLOAD_FILE_FORMAT_ERROR);
            }
        } catch (Exception e){
            throw new GrgException(RespCode.CNSL_UPLOAD_FILE_PARSE_ERROR);
        }
        return workbook;
    }
    public String getFieldString(List<Object> lo,int FieldId){
        String str = null;
        if (lo.size() > FieldId && lo.get(FieldId) != null) {
            Cell cell = (Cell) lo.get(FieldId);
            if (cell != null){
                cell.setCellType(Cell.CELL_TYPE_STRING);
                str = cell.getStringCellValue();
            }

        }
        return str;
    }
}
