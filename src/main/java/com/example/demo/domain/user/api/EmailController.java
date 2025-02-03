package com.example.demo.domain.user.api;


import com.example.demo.domain.user.application.EmailService;
import com.example.demo.domain.user.dto.request.EmailRequest;
import com.example.demo.domain.user.dto.request.VerifyEmailRequest;
import com.example.demo.domain.user.dto.response.EmailResponse;
import com.example.demo.domain.user.dto.response.TemporaryPasswordResponse;
import com.example.demo.global.response.ApiResponse;
import com.example.demo.global.response.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emails")
public class EmailController {

    private final EmailService emailService;

    @Operation(summary = "이메일 인증 요청", description = "메일에 인증 메세지를 요청한다.")
    @PostMapping("")
    private ApiResponse<EmailResponse> sendConfirmCode(@Valid @RequestBody EmailRequest request) {
        EmailResponse response = emailService.sendCodeToEmail(request);
        return ApiUtil.success(response);
    }

    @Operation(summary = "이메일 인증", description = "메일에 전송된 이메일을 인증한다.")
    @PostMapping("/confirm-code")
    private ApiResponse<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        emailService.verifyConfirmCode(request);
        return ApiUtil.successOnly();
    }

    @Operation(summary = "임시 비밀번호 발급", description = "임시 비밀번호를 발급한다.")
    @PostMapping("/temporary-password")
    private ApiResponse<TemporaryPasswordResponse> sendTemporaryPassword(@RequestBody EmailRequest request) {
        TemporaryPasswordResponse response = emailService.sendTemporaryPassword(request);
        return ApiUtil.success(response);
    }
}
