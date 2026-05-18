package com.capstone.kkumteul.global.config;

import net.bramp.ffmpeg.FFmpeg;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class FFmpegDI {

    @Bean
    public FFmpeg ffmpeg() {
        try {
            return new FFmpeg("/usr/bin/ffmpeg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
