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
            return new FFmpeg("/usr/bin/ffmpeg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public FFprobe ffprobe() {
        try {
            return new FFprobe("/usr/bin/ffprobe");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
