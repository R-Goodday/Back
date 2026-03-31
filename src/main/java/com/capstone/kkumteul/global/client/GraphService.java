package com.capstone.kkumteul.global.client;

import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import com.capstone.kkumteul.domain.game.entity.EdgeChoice;
import com.capstone.kkumteul.domain.game.entity.GraphEdge;
import com.capstone.kkumteul.domain.game.entity.GraphNode;
import com.capstone.kkumteul.domain.game.entity.NodeCategory;
import com.capstone.kkumteul.domain.game.repository.EdgeChoiceRepository;
import com.capstone.kkumteul.domain.game.repository.GraphEdgeRepository;
import com.capstone.kkumteul.domain.game.repository.GraphNodeRepository;
import com.capstone.kkumteul.global.client.dto.GraphExtractRequest;
import com.capstone.kkumteul.global.client.dto.GraphExtractResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphService {

    private final GraphNodeRepository graphNodeRepository;
    private final GraphEdgeRepository graphEdgeRepository;
    private final EdgeChoiceRepository edgeChoiceRepository;
    private final RestTemplate restTemplate;

    @Value("${fastapi.base-url:http://localhost:8000}")
    private String fastApiBaseUrl;

    @Transactional
    public void extractAndSave(Fairytale fairytale, String content) {
        GraphExtractRequest request = new GraphExtractRequest(fairytale.getId(), content);

        GraphExtractResponse response = restTemplate.postForObject(
                fastApiBaseUrl + "/graph/extract",
                request,
                GraphExtractResponse.class
        );

        if (response == null || response.getNodes() == null) {
            throw new RuntimeException("FastAPI 그래프 추출 응답이 비어있습니다.");
        }

        // 1. graph_nodes 저장 + temp_id → real PK 매핑
        Map<Integer, GraphNode> tempIdToNode = new HashMap<>();

        for (GraphExtractResponse.NodeDto nodeDto : response.getNodes()) {
            GraphNode node = GraphNode.builder()
                    .fairytale(fairytale)
                    .word(nodeDto.getWord())
                    .category(NodeCategory.fromLabel(nodeDto.getCategory()))
                    .build();
            graphNodeRepository.save(node);
            tempIdToNode.put(nodeDto.getTempId(), node);
        }

        // 2. graph_edges + edge_choices 저장
        for (GraphExtractResponse.EdgeDto edgeDto : response.getEdges()) {
            GraphNode fromNode = tempIdToNode.get(edgeDto.getFromTempId());
            GraphNode toNode = tempIdToNode.get(edgeDto.getToTempId());

            GraphEdge edge = GraphEdge.builder()
                    .fromNode(fromNode)
                    .toNode(toNode)
                    .description(edgeDto.getDescription())
                    .build();
            graphEdgeRepository.save(edge);

            for (GraphExtractResponse.ChoiceDto choiceDto : edgeDto.getChoices()) {
                EdgeChoice choice = EdgeChoice.builder()
                        .edge(edge)
                        .content(choiceDto.getContent())
                        .isAnswer(choiceDto.getIsAnswer() != null && choiceDto.getIsAnswer())
                        .build();
                edgeChoiceRepository.save(choice);
            }
        }

        log.info("그래프 추출 완료: fairytaleId={}, nodes={}, edges={}",
                fairytale.getId(), response.getNodes().size(), response.getEdges().size());
    }
}
