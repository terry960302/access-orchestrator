package com.pandaterry.access_orchestrator.core.resource.service;

import com.pandaterry.access_orchestrator.core.asset.AssetId;
import com.pandaterry.access_orchestrator.core.resource.SubjectId;
import com.pandaterry.access_orchestrator.core.asset.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryAssetServiceTest {
    private InMemoryAssetService service;

    @BeforeEach
    void setUp() {
        service = new InMemoryAssetService();
    }

    @Test
    @DisplayName("에셋을 저장하면 조회할 수 있어야 한다")
    void createAndGet_ShouldReturnAsset() {
        Asset asset = Asset.builder()
                .id(new AssetId("asset1"))
                .name("name")
                .type("type")
                .url("url")
                .ownerId(new SubjectId("owner"))
                .state("NEW")
                .metadata(Map.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        service.create(asset);

        Asset result = service.get(new AssetId("asset1"));
        assertThat(result).isEqualTo(asset);
    }

    @Test
    @DisplayName("에셋을 수정하면 변경 내용이 반영되어야 한다")
    void update_ShouldReplaceAsset() {
        Asset asset = Asset.builder().id(new AssetId("asset1")).build();
        service.create(asset);

        Asset updated = Asset.builder().id(new AssetId("asset1")).name("new").build();
        service.update(new AssetId("asset1"), updated);

        assertThat(service.get(new AssetId("asset1")).getName()).isEqualTo("new");
    }

    @Test
    @DisplayName("에셋을 삭제하면 조회되지 않아야 한다")
    void delete_ShouldRemoveAsset() {
        Asset asset = Asset.builder().id(new AssetId("asset1")).build();
        service.create(asset);

        service.delete(new AssetId("asset1"));

        assertThat(service.get(new AssetId("asset1"))).isNull();
    }
}
