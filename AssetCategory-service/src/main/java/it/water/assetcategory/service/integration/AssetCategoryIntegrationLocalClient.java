package it.water.assetcategory.service.integration;

import it.water.core.api.asset.AssetCategoryManager;
import it.water.core.api.model.AssetCategoryResource;
import it.water.core.api.service.integration.AssetCategoryIntegrationClient;
import it.water.core.interceptors.annotations.FrameworkComponent;
import it.water.core.interceptors.annotations.Inject;
import lombok.Setter;

/**
 * Local implementation of AssetCategoryIntegrationClient.
 * Used when AssetCategory module is in the same container as the caller.
 * Delegates all operations to AssetCategoryManager (implemented by AssetCategorySystemServiceImpl).
 *
 * This follows the same pattern as UserIntegrationLocalClient.
 */
@FrameworkComponent(priority = 1, services = AssetCategoryIntegrationClient.class)
public class AssetCategoryIntegrationLocalClient implements AssetCategoryIntegrationClient {

    @Inject
    @Setter
    private AssetCategoryManager assetCategoryManager;

    @Override
    public AssetCategoryResource findAssetCategoryResource(String resourceName, long resourceId, long categoryId) {
        if (assetCategoryManager == null) return null;
        return assetCategoryManager.findAssetCategoryResource(resourceName, resourceId, categoryId);
    }

    @Override
    public void addAssetCategory(String resourceName, long resourceId, long categoryId) {
        if (assetCategoryManager != null) {
            assetCategoryManager.addAssetCategory(resourceName, resourceId, categoryId);
        }
    }

    @Override
    public void addAssetCategories(String resourceName, long resourceId, long[] categoriesId) {
        if (assetCategoryManager != null) {
            assetCategoryManager.addAssetCategories(resourceName, resourceId, categoriesId);
        }
    }

    @Override
    public long[] findAssetCategories(String resourceName, long resourceId) {
        if (assetCategoryManager == null) return new long[0];
        return assetCategoryManager.findAssetCategories(resourceName, resourceId);
    }

    @Override
    public void removeAssetCategory(String resourceName, long resourceId, long categoryId) {
        if (assetCategoryManager != null) {
            assetCategoryManager.removeAssetCategory(resourceName, resourceId, categoryId);
        }
    }

    @Override
    public void removeAssetCategories(String resourceName, long resourceId, long[] categoriesId) {
        if (assetCategoryManager != null) {
            assetCategoryManager.removeAssetCategories(resourceName, resourceId, categoriesId);
        }
    }
}
