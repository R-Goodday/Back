package com.capstone.kkumteul.global.config;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class FFmpegDI {

    @Bean
    public FFmpeg ffmpeg() {
        try {
            return new FFmpeg("/opt/homebrew/bin/ffmpeg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public FFprobe ffprobe() {
        try {
            return new FFprobe("/opt/homebrew/bin/ffprobe");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
