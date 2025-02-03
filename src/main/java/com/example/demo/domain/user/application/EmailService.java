package com.example.demo.domain.user.application;

import com.example.demo.domain.user.dao.ConfirmCodeRepository;
import com.example.demo.domain.user.dao.UserRepository;
import com.example.demo.domain.user.domain.ConfirmCode;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.dto.request.EmailRequest;
import com.example.demo.domain.user.dto.request.VerifyEmailRequest;
import com.example.demo.domain.user.dto.response.EmailResponse;
import com.example.demo.domain.user.dto.response.TemporaryPasswordResponse;
import com.example.demo.domain.user.exception.ConfirmCodeNotFoundException;
import com.example.demo.domain.user.exception.NotMatchConfirmCodeException;
import com.example.demo.domain.user.exception.SendCodeFailureException;
import com.example.demo.domain.user.exception.UserNotFoundException;
import jakarta.mail.MessagingException;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final AsyncEmailSenderService asyncEmailSenderService;
    private final int CONFIRM_CODE_LENGTH = 6;
    private final ConfirmCodeRepository confirmCodeRepository;
    private final UserRepository userRepository;

    public EmailResponse sendCodeToEmail(EmailRequest request){
        String email = request.getEmail();
        String title = "아워메뉴 이메일 인증 번호";
        String generatedRandomCode = generateRandomCode(CONFIRM_CODE_LENGTH);

        String content = "<html>"
                + "<body>"
                + "<h1>아워메뉴 인증 코드: " + generatedRandomCode + "</h1>"
                + "</body>"
                + "</html>";

        try {
            asyncEmailSenderService.sendEmail(email, title, content);
        }catch (MessagingException e){
            throw new SendCodeFailureException();
        }

        ConfirmCode confirmCode = ConfirmCode.of(email, generatedRandomCode);
        confirmCodeRepository.save(confirmCode);

        return EmailResponse.builder()
                .code(generatedRandomCode)
                .build();
    }

    public String generateRandomCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder confirmCode = new StringBuilder();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            confirmCode.append(characters.charAt(index));
        }

        return confirmCode.toString();
    }

    public void verifyConfirmCode(VerifyEmailRequest request){
        String email = request.getEmail();
        String inputConfirmCode = request.getConfirmCode();

        ConfirmCode confirmCode = confirmCodeRepository.findConfirmCodeByEmail(email)
                .orElseThrow(ConfirmCodeNotFoundException::new);

        if (!confirmCode.getConfirmCode().equals(inputConfirmCode)) {
            throw new NotMatchConfirmCodeException();
        }
    }

    public TemporaryPasswordResponse sendTemporaryPassword(EmailRequest request) {
        String email = request.getEmail();
        String temporaryPassword = generateRandomCode(8);
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        user.changePassword(temporaryPassword);
        userRepository.save(user);
        return TemporaryPasswordResponse.from(temporaryPassword);
    }
}
