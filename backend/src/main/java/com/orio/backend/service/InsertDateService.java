package com.orio.backend.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.orio.backend.dto.ResultDto;
import com.orio.backend.dto.TimeDto;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InsertDateService{
    Logger logger = LoggerFactory.getLogger(InsertDateService.class);
    
    public ResultDto insertDate(String filePath, TimeDto timeDto){
        
        // set result
        ResultDto result = new ResultDto();

        // initiallize
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        Workbook workbook = null;

        // Start Date String
        String startDateStr = timeDto.getDay() +" " + timeDto.getStartTime();

        // Date String
        String nowDateStr = timeDto.getDay() +" " + timeDto.getTime();

        // Date Format
        DateFormat dfIn = new SimpleDateFormat("yyyy/MM/dd kk:mm");
        DateFormat dfOut = new SimpleDateFormat("kk:mm");

        try {

            // String to Start Date "yyyy/MM/dd hh:mm"
            Date parsedStartDate = dfIn.parse(startDateStr);

            // String to Date "yyyy/MM/dd hh:mm"
            Date parsedDate = dfIn.parse(nowDateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(parsedDate);

            // day : 1~31
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

            // Start Date to String to "hh:mm"
            String startDate = dfOut.format(parsedStartDate);

            // Date to String to "hh:mm"
            String outputDate = dfOut.format(parsedDate);

            String dTime = null;
            String eOutputDate = null;

            // excel path
            String path = filePath;
            inputStream = new FileInputStream(new File(path));
            workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            sheet.setForceFormulaRecalculation(true);

            // calculate and set value
            while (true) {
                            
                // read excel find coordinate
                // start day -> row :9 col:1(B)
                int rowNumber = dayOfMonth + 9;

                // work start time -> col:E
                CellAddress scellAddress = new CellAddress("E" + rowNumber);
                Row srow = sheet.getRow(scellAddress.getRow());         
                Cell scell = srow.getCell(scellAddress.getColumn());
                CellStyle sdefaultStyle = scell.getCellStyle();

                scell.setCellValue(startDate);
                scell.setCellStyle(sdefaultStyle);

                // work end time -> col:F
                CellAddress cellAddress = new CellAddress("F" + rowNumber);
                Row row = sheet.getRow(cellAddress.getRow());         
                Cell cell = row.getCell(cellAddress.getColumn());
                CellStyle defaultStyle = cell.getCellStyle();

                cell.setCellValue(outputDate);
                cell.setCellStyle(defaultStyle);

                // deduct time -> col:G
                CellAddress dCellAddress = new CellAddress("G" + rowNumber);
                Row dRow = sheet.getRow(dCellAddress.getRow());         
                Cell dCell = dRow.getCell(dCellAddress.getColumn());
                CellStyle dStyle = dCell.getCellStyle();

                dTime = this.calculateDtime(outputDate);
                if (dTime!=null){
                    dCell.setCellValue(dTime);
                    dCell.setCellStyle(dStyle);
                }

                // memo -> col: K
                CellAddress mCellAddress = new CellAddress("K" + rowNumber);
                Row mRow = sheet.getRow(mCellAddress.getRow());         
                Cell mCell = mRow.getCell(mCellAddress.getColumn());
                CellStyle mStyle = mCell.getCellStyle();
                mCell.setCellValue(timeDto.getMemo());
                mCell.setCellStyle(mStyle);

                // write to excel
                outputStream = new FileOutputStream(filePath);
                workbook.write(outputStream);
    
                // evaluate working time
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellAddress eCellAddress = new CellAddress("I" + rowNumber);
                Row eRow = sheet.getRow(eCellAddress.getRow());         
                Cell eCell = eRow.getCell(eCellAddress.getColumn());
                evaluator.evaluateFormulaCell(eCell);
                
                Date eDate = eCell.getDateCellValue();
                eOutputDate = dfOut.format(eDate);
                
                // check minute
                int eMinute = Integer.parseInt(eOutputDate.substring(3,5));
                
                // break at divided by 15
                if (eMinute%15==0){

                    // write  evaluate value to excel
                    outputStream = new FileOutputStream(filePath);
                    workbook.write(outputStream);

                    // close excel
                    workbook.close();
                    outputStream.close();    
                    inputStream.close();
                    break;
                } else {

                    // minus - 1 from oldMinute
                    String oldMinuteStr = outputDate.substring(3,5);
                    int newMinute = Integer.parseInt(oldMinuteStr) - 1;

                    // padding "0" if under 10 
                    if (newMinute<0){
                        int newHour = Integer.parseInt(outputDate.substring(0,2)) - 1;
                        String newH = Integer.toString(newHour);
                        if (newHour >= 0 && newHour < 10){
                            newH = "0" + newH;
                        }
                        outputDate = newH + ":" + "59";
                    }
                    else if (newMinute >= 0 && newMinute < 10){
                        outputDate = outputDate.substring(0,3) + "0" + newMinute;
                    }else {
                        outputDate = outputDate.substring(0,3) + Integer.toString(newMinute);
                    }
                }
                outputStream.close();
            }
            // logger.debug("##work_time : " + outputDate);
            // logger.debug("##deduct_time : " + dTime);
            // logger.debug("##evaluate_time : " + eOutputDate);
            result.setWork_time(outputDate);
            result.setDeduct_time(dTime);
            result.setEvaluate_time(eOutputDate);

        } catch (Exception e) {
            logger.debug(e.getMessage());
        } finally {
            try {
                workbook.close();
                if (inputStream!=null) inputStream.close();
                if (outputStream!=null) outputStream.close();
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
        }
        return result;
    }

    public String modifyTime(String insertTime){

        // get minute


        return null;
    }

    /** calculate deduct time */
    public String calculateDtime(String worktime){

        // work time
        String outputDate = null;

		// time format
		DateFormat dfOut = new SimpleDateFormat("kk:mm");
		DateFormat dfOut2 = new SimpleDateFormat("HH:mm");

		try {
			Date workDate = dfOut.parse(worktime);

			/* time sample */
			Date dDateA = dfOut.parse("00:45");
			Date dDateB = dfOut.parse("01:05");
			Date dDateC = dfOut.parse("01:25");
			Date dDate = dfOut.parse("00:00");

			/* define time */
			// min standard time a
			String mTimeA = "11:45";
			Date mDateA = dfOut.parse(mTimeA);
			// max a
			Date xDateA = new Date(mDateA.getTime() + (45 * 60000));

			// min standard time b
			String mTimeB = "17:20";
			Date mDateB = dfOut.parse(mTimeB);
			// max b
			Date xDateB = new Date(mDateB.getTime() + (20 * 60000));

			// min standard time c
			String mTimeC = "19:40";
			Date mDateC = dfOut.parse(mTimeC);
			// max c
			Date xDateC = new Date(mDateC.getTime() + (20 * 60000));

			/* calculate time */
			// time >= 20:00           => 01:25
			if (workDate.compareTo(xDateC) >= 0){
				dDate = dDateC;
			}
			// 20:00 > time >= 19:40   => 01:05 + (time - 19:40)
			else if (workDate.compareTo(xDateC) < 0 && workDate.compareTo(mDateC) >= 0){
				long d = dDateB.getTime() + (workDate.getTime() - mDateC.getTime());
				dDate = new Date(d);
			}
			// 19:40 > time >= 17:40           => 01:05
			else if (workDate.compareTo(mDateC) < 0 && workDate.compareTo(xDateB) >= 0){
				dDate = dDateB;
			}
			// 17:40 > time >= 17:20   => 00:45 + (time - 17:20)
			else if (workDate.compareTo(xDateB) < 0 && workDate.compareTo(mDateB) >= 0){
				long d = dDateA.getTime() + (workDate.getTime() - mDateB.getTime());
				dDate = new Date(d);
			}
			// 17:20 > time >= 12:30            => 00:45
			else if (workDate.compareTo(mDateB) < 0 && workDate.compareTo(xDateA) >= 0){
				dDate = dDateA;
			}
			// 12:30 > time >= 11:45   => (time - 11:45)
			else if (workDate.compareTo(xDateA) < 0 && workDate.compareTo(mDateA) >= 0){
				long d = dDate.getTime() + (workDate.getTime() - mDateA.getTime());
				dDate = new Date(d);				
			}

			outputDate = dfOut2.format(dDate);
			
		} catch (Exception e) {
            e.printStackTrace();		
		}
        
        // return
        return outputDate;
    }
}