package com.dongkyeom.gps.emitter.emitter.presentation.controller.command;

import com.dongkyeom.gps.emitter.core.dto.ResponseDto;
import com.dongkyeom.gps.emitter.emitter.presentation.dto.request.EmitGpsRequestDto;
import com.dongkyeom.gps.emitter.emitter.application.usecase.EmitGpsDataUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gps")
public class EmitterCommandV1Controller {

    private final EmitGpsDataUseCase emitGpsDataUseCase;

    /**
     * GPS 데이터 파일을 읽고, 입력받은 경로로 데이터를 전송한다.
     * 목표 endpoints는 Trajectory-Processor 서버에 구현되어있는 API 와 관련이 있다.
     * @param file 엑셀 데이터(1행 무시, 2행부터 데이터 시작. tripId, agentId, latitude, longitude 순서)
     * @param requestDto GPS 데이터 전송을 위한 목적지 IP 주소 및 포트
     * 목적지로 최종 전송되는 payload 의 Json 형식은 아래와 같다.
     *                   {
     *                      "trip_id":  "tripId",
     *                      "agent_id":  "agentId",
     *                      "latitude":  "latitude",
     *                      "longitude":  "longitude",
     *                      "timestamp":  "timestamp"
     *                   }
     */
    @PostMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseDto<Void> emitGpsData(
            @RequestPart(value = "file") MultipartFile file,
            @RequestPart(value = "body") EmitGpsRequestDto requestDto
    ) {
        emitGpsDataUseCase.execute(
                file,
                requestDto.destination()
        );
        return ResponseDto.ok(null);
    }

}
