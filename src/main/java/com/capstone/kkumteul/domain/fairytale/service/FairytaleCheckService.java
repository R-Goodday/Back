package com.capstone.kkumteul.domain.fairytale.service;

public interface FairytaleCheckService {

    void markVocabDone(Long fairytaleId, int page);

    void markImageDone(Long fairytaleId, int page);

    boolean isBothDone(Long fairytaleId, int page);

    void markTotalPages(Long fairytaleId, int totalPages);

    void markTtsFileDone(Long fairytaleId, int pageNo, String ttsUrl);
}
