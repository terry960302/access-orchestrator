package com.pandaterry.access_orchestrator.core.resource.service;

import com.pandaterry.access_orchestrator.core.asset.AssetId;
import com.pandaterry.access_orchestrator.core.asset.Asset;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class InMemoryAssetService implements AssetService {
    private final Map<AssetId, Asset> assets = new ConcurrentHashMap<>();

    @Override
    public Asset create(Asset asset) {
        assets.put(asset.getId(), asset);
        return asset;
    }

    @Override
    public Asset get(AssetId id) {
        return assets.get(id);
    }

    @Override
    public List<Asset> getAll() {
        return List.copyOf(assets.values());
    }

    @Override
    public Asset update(AssetId id, Asset asset) {
        assets.put(id, asset);
        return asset;
    }

    @Override
    public void delete(AssetId id) {
        assets.remove(id);
    }
}
