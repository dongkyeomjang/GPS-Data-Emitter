package com.dongkyeom.gps.emitter.emitter.application.usecase;

import com.dongkyeom.gps.emitter.core.annotation.bean.UseCase;
import org.springframework.web.multipart.MultipartFile;

@UseCase
public interface EmitGpsDataUseCase {

    /**
     * GPS 데이터 파일을 읽고, 입력받은 경로로 데이터를 전송한다.
     * 목표 endpoints는 Trajectory-Processor 서버에 구현되어있는 API 와 관련이 있다.
     * @param file 엑셀 데이터(1행 무시, 2행부터 데이터 시작. tripId, agentId, latitude, longitude 순서)
     * @param destination GPS 데이터 전송을 위한 목적지 IP 주소 및 포트
     */
    void execute(MultipartFile file, String destination);
}
