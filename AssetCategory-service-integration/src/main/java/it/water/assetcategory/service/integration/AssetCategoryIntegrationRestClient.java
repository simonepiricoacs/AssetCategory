package it.water.assetcategory.service.integration;

import it.water.core.api.model.AssetCategoryResource;
import it.water.core.api.service.integration.AssetCategoryIntegrationClient;
import it.water.core.api.service.integration.EntityRemoteIntegrationClient;
import it.water.core.api.service.integration.discovery.DiscoverableServiceInfo;
import it.water.core.interceptors.annotations.FrameworkComponent;

@FrameworkComponent
public class AssetCategoryIntegrationRestClient implements EntityRemoteIntegrationClient, AssetCategoryIntegrationClient {


    @Override
    public void setup(DiscoverableServiceInfo serviceInfo) {
    }

    @Override
    public AssetCategoryResource findAssetCategoryResource(String resourceName, long resourceId, long categoryId) {
        //todo rest call
        return null;
    }

    @Override
    public void addAssetCategory(String resourceName, long resourceId, long categoryId) {
        //todo rest call
    }

    @Override
    public void addAssetCategories(String resourceName, long resourceId, long[] categoriesId) {
        //todo rest call
    }

    @Override
    public long[] findAssetCategories(String resourceName, long resourceId) {
        return new long[0];
    }

    @Override
    public void removeAssetCategory(String resourceName, long resourceId, long categoryId) {
        //todo rest call
    }

    @Override
    public void removeAssetCategories(String resourceName, long resourceId, long[] categoriesId) {
        //todo rest call
    }
}
