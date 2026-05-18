package com.capstone.kkumteul.domain.voice.web.controller;

import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.domain.voice.exception.InvalidFileException;
import com.capstone.kkumteul.domain.voice.service.VoiceService;
import com.capstone.kkumteul.domain.voice.web.dto.TtsFileResponse;
import com.capstone.kkumteul.global.response.SuccessResponse;
import com.capstone.kkumteul.global.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@RestController
// not hard-fix configuration convention
@RequestMapping("/api/users/voice")
@RequiredArgsConstructor
public class VoiceController {

    private final VoiceService voiceService;
    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;

    @PostMapping
    public ResponseEntity<SuccessResponse<?>> sendTtsRequestMessage(
            @AuthUser User user,
            @RequestPart MultipartFile m4aFile
    ) {

        // File validation
        String m4aFilename = m4aFile.getOriginalFilename();
        
        // not null validation
        if(m4aFilename == null) {
            throw new InvalidFileException();
        }

        // is invalid or not m4a file validtaion
        if(m4aFile.isEmpty()
                || m4aFilename.isBlank()
        || !m4aFilename.toLowerCase().endsWith(".m4a"))
            throw new InvalidFileException();

        byte[] wavFile;

        try {
            wavFile = convertM4aToWav(m4aFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String originalFilename = m4aFilename.replace(".m4a", ".wav");
        
        voiceService.saveWav(wavFile, originalFilename, user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.created(user.getId()));
    }

    private byte[] convertM4aToWav(MultipartFile m4aFile) throws IOException {
        Path in = Files.createTempFile("audio-", "m4a");
        Path out = Files.createTempFile("audio-", "wav");

        try {
            Files.copy(m4aFile.getInputStream(), in, StandardCopyOption.REPLACE_EXISTING);

            FFmpegBuilder ffmpegBuilder = new FFmpegBuilder()
                    .setInput(in.toString())
                    .done()
                    .overrideOutputFiles(true)
                    .addOutput(out.toString())
                    .setFormat("wav")
                    .setAudioCodec("pcm_s16le")
                    .setAudioChannels(1)
                    .setAudioSampleRate(16_000)
                    .done();

            new FFmpegExecutor(ffmpeg, ffprobe)
                    .createJob(ffmpegBuilder)
                    .run();

            return Files.readAllBytes(out);
        } finally {
            Files.deleteIfExists(in);
            Files.deleteIfExists(out);
        }
    }

    @GetMapping("/{paragraphId}")
    public ResponseEntity<SuccessResponse<?>> getTtsFile(
            @AuthUser User user,
            @PathVariable Long paragraphId
    ) {
        TtsFileResponse response = voiceService.getTtsFile(user.getId(), paragraphId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.ok(response));
    }

    @PostMapping("/{fairytaleId}")
    public ResponseEntity<SuccessResponse<?>> postTtsFile(
            @AuthUser User user,
            @PathVariable Long fairytaleId
    ) {
        voiceService.createTtsFile(user.getId(), fairytaleId);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(SuccessResponse.accepted());
    }

    @GetMapping("/{paragraphId}/check")
    public ResponseEntity<SuccessResponse<?>> hasTtsHistory(
            @AuthUser User user,
            @PathVariable Long paragraphId
    ) {
        Boolean response = voiceService.hasTtsHistory(user.getId(), paragraphId);

        return ResponseEntity.status(response ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .body(SuccessResponse.ok(response));
    }

}
