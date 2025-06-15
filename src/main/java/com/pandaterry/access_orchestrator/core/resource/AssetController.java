package com.pandaterry.access_orchestrator.core.resource;

import com.pandaterry.access_orchestrator.core.asset.Asset;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pandaterry.access_orchestrator.core.asset.AssetId;
import com.pandaterry.access_orchestrator.core.resource.service.AssetService;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {
    private final AssetService assetService;

    @PostMapping
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) {
        return ResponseEntity.ok(assetService.create(asset));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAsset(@PathVariable String id) {
        Asset asset = assetService.get(new AssetId(id));
        return asset != null ? ResponseEntity.ok(asset) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets() {
        return ResponseEntity.ok(assetService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(@PathVariable String id, @RequestBody Asset asset) {
        AssetId assetId = new AssetId(id);
        if (assetService.get(assetId) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(assetService.update(assetId, asset));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable String id) {
        assetService.delete(new AssetId(id));
        return ResponseEntity.ok().build();
    }
}