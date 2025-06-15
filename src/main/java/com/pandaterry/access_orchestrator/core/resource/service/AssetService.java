package com.pandaterry.access_orchestrator.core.resource.service;

import com.pandaterry.access_orchestrator.core.asset.AssetId;
import com.pandaterry.access_orchestrator.core.asset.Asset;

import java.util.List;

public interface AssetService {
    Asset create(Asset asset);

    Asset get(AssetId id);

    List<Asset> getAll();

    Asset update(AssetId id, Asset asset);

    void delete(AssetId id);
}
