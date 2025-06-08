package com.pandaterry.access_orchestrator.core.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {
    private final Map<String, Asset> assets = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) {
        assets.put(asset.getId(), asset);
        return ResponseEntity.ok(asset);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAsset(@PathVariable String id) {
        Asset asset = assets.get(id);
        return asset != null ? ResponseEntity.ok(asset) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets() {
        return ResponseEntity.ok(List.copyOf(assets.values()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(@PathVariable String id, @RequestBody Asset asset) {
        if (!assets.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        assets.put(id, asset);
        return ResponseEntity.ok(asset);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable String id) {
        assets.remove(id);
        return ResponseEntity.ok().build();
    }
}