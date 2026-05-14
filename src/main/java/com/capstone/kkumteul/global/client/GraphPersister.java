package com.capstone.kkumteul.global.client;

import com.capstone.kkumteul.domain.fairytale.entity.Fairytale;
import com.capstone.kkumteul.domain.game.entity.EdgeChoice;
import com.capstone.kkumteul.domain.game.entity.GraphEdge;
import com.capstone.kkumteul.domain.game.entity.GraphNode;
import com.capstone.kkumteul.domain.game.entity.NodeCategory;
import com.capstone.kkumteul.domain.game.exception.InvalidGraphPayloadException;
import com.capstone.kkumteul.domain.game.repository.EdgeChoiceRepository;
import com.capstone.kkumteul.domain.game.repository.GraphEdgeRepository;
import com.capstone.kkumteul.domain.game.repository.GraphNodeRepository;
import com.capstone.kkumteul.global.client.dto.GraphExtractResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 지식그래프 추출 응답을 DB 에 저장하는 빈.
 *
 * <p>{@link GraphService} 와 별도 빈으로 분리되어 있는 이유는 Spring AOP self-invocation 함정을 피하기 위함이다.
 * {@code GraphService} 내부에서 {@code @Transactional} 메서드를 호출하면 프록시를 거치지 않아 트랜잭션이 적용되지 않는다.
 * 외부 I/O 는 {@code GraphService} 가, DB 저장은 본 빈이 담당한다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GraphPersister {

    private final GraphNodeRepository graphNodeRepository;
    private final GraphEdgeRepository graphEdgeRepository;
    private final EdgeChoiceRepository edgeChoiceRepository;

    @Transactional
    public void persist(Fairytale fairytale, GraphExtractResponse response) {
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
            if (fromNode == null || toNode == null) {
                throw new InvalidGraphPayloadException();
            }

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

        log.info("그래프 저장 완료: fairytaleId={}, nodes={}, edges={}",
                fairytale.getId(), response.getNodes().size(), response.getEdges().size());
    }
}
