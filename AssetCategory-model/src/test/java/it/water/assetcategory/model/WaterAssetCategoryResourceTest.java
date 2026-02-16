package it.water.assetcategory.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class WaterAssetCategoryResourceTest {

    @Test
    void constructorAndCategoryId() {
        AssetCategory category = Mockito.mock(AssetCategory.class);
        Mockito.when(category.getId()).thenReturn(42L);

        WaterAssetCategoryResource resource = new WaterAssetCategoryResource("resource", 7L, category);

        Assertions.assertEquals("resource", resource.getResourceName());
        Assertions.assertEquals(7L, resource.getResourceId());
        Assertions.assertSame(category, resource.getCategory());
        Assertions.assertEquals(42L, resource.getCategoryId());
    }

    @Test
    void categoryIdIsZeroWhenCategoryIsNull() {
        WaterAssetCategoryResource resource = new WaterAssetCategoryResource("resource", 7L, null);
        Assertions.assertEquals(0L, resource.getCategoryId());
    }
}
