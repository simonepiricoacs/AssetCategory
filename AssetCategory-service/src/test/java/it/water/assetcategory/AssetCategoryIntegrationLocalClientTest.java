package it.water.assetcategory;

import it.water.assetcategory.service.integration.AssetCategoryIntegrationLocalClient;
import it.water.core.api.asset.AssetCategoryManager;
import it.water.core.api.model.AssetCategoryResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class AssetCategoryIntegrationLocalClientTest {

    @Test
    void nullManagerBranches() {
        AssetCategoryIntegrationLocalClient client = new AssetCategoryIntegrationLocalClient();

        Assertions.assertNull(client.findAssetCategoryResource("res", 1L, 2L));
        Assertions.assertArrayEquals(new long[0], client.findAssetCategories("res", 1L));

        Assertions.assertDoesNotThrow(() -> client.addAssetCategory("res", 1L, 2L));
        Assertions.assertDoesNotThrow(() -> client.addAssetCategories("res", 1L, new long[]{2L, 3L}));
        Assertions.assertDoesNotThrow(() -> client.removeAssetCategory("res", 1L, 2L));
        Assertions.assertDoesNotThrow(() -> client.removeAssetCategories("res", 1L, new long[]{2L, 3L}));
    }

    @Test
    void delegateCallsWhenManagerPresent() {
        AssetCategoryIntegrationLocalClient client = new AssetCategoryIntegrationLocalClient();
        AssetCategoryManager manager = Mockito.mock(AssetCategoryManager.class);
        AssetCategoryResource expected = Mockito.mock(AssetCategoryResource.class);
        long[] expectedIds = new long[]{10L, 11L};

        Mockito.when(manager.findAssetCategoryResource("res", 1L, 2L)).thenReturn(expected);
        Mockito.when(manager.findAssetCategories("res", 1L)).thenReturn(expectedIds);

        client.setAssetCategoryManager(manager);

        Assertions.assertSame(expected, client.findAssetCategoryResource("res", 1L, 2L));
        Assertions.assertArrayEquals(expectedIds, client.findAssetCategories("res", 1L));

        client.addAssetCategory("res", 1L, 2L);
        client.addAssetCategories("res", 1L, new long[]{2L, 3L});
        client.removeAssetCategory("res", 1L, 2L);
        client.removeAssetCategories("res", 1L, new long[]{2L, 3L});

        Mockito.verify(manager).findAssetCategoryResource("res", 1L, 2L);
        Mockito.verify(manager).findAssetCategories("res", 1L);
        Mockito.verify(manager).addAssetCategory("res", 1L, 2L);
        Mockito.verify(manager).addAssetCategories(Mockito.eq("res"), Mockito.eq(1L), Mockito.any(long[].class));
        Mockito.verify(manager).removeAssetCategory("res", 1L, 2L);
        Mockito.verify(manager).removeAssetCategories(Mockito.eq("res"), Mockito.eq(1L), Mockito.any(long[].class));
    }
}
