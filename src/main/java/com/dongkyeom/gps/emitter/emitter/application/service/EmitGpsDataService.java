package com.dongkyeom.gps.emitter.emitter.application.service;

import com.dongkyeom.gps.emitter.core.utility.RestClientUtil;
import com.dongkyeom.gps.emitter.emitter.application.usecase.EmitGpsDataUseCase;
import com.dongkyeom.gps.emitter.emitter.domain.GPS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmitGpsDataService implements EmitGpsDataUseCase {

    private final RestClientUtil restClientUtil;

    @Override
    public void execute(MultipartFile file, String destination) {

        List<GPS> gpsDataList = parseExcelFile(file);

        int numberOfThreads = 2000;
        int rowsPerThread = gpsDataList.size() / numberOfThreads;

        for (int t = 0; t < numberOfThreads; t++) {
            int startIndex = t * rowsPerThread;
            int endIndex = (t == numberOfThreads - 1) ? gpsDataList.size() : (t + 1) * rowsPerThread;
            List<GPS> subList = gpsDataList.subList(startIndex, endIndex);

            new Thread(() -> {
                for (GPS gps : subList) {
                    sendGps(gps, destination);
                    try { Thread.sleep(1000); } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();
        }
    }

    /**
     * XLSX 파일을 읽어 GPS 데이터를 파싱합니다.
     *
     * @param file XLSX 파일
     * @return GPS 데이터 리스트
     */
    private List<GPS> parseExcelFile(MultipartFile file) {
        List<GPS> gpsDataList = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // 첫 번째 줄은 헤더이므로 건너뜁니다.
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                String tripId = getCellStringValue(row.getCell(0));
                String agentId = getCellStringValue(row.getCell(1));
                String latitude = getCellStringValue(row.getCell(2));
                String longitude = getCellStringValue(row.getCell(3));
                String timestamp = getCellStringValue(row.getCell(4));

                GPS gpsData = GPS.builder()
                        .tripId(tripId)
                        .agentId(agentId)
                        .latitude(latitude)
                        .longitude(longitude)
                        .timestamp(timestamp)
                        .build();

                gpsDataList.add(gpsData);
            }
        } catch (Exception e) {
            log.error("XLSX 파일 파싱 중 오류 발생", e);
            throw new RuntimeException("XLSX 파일 파싱 실패", e);
        }
        return gpsDataList;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private void sendGps(GPS gps, String destination) {
        String url = "http://" + destination + "/gps";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // JSON 만들기
        String payload = "{"
                + "\"trip_id\":\"" + gps.getTripId() + "\","
                + "\"agent_id\":\"" + gps.getAgentId() + "\","
                + "\"latitude\":\"" + gps.getLatitude() + "\","
                + "\"longitude\":\"" + gps.getLongitude() + "\","
                + "\"timestamp\":\"" + gps.getTimestamp() + "\""
                + "}";

        restClientUtil.sendNonReturnPostMethod(url, headers, payload);
    }
}
