package it.water.assetcategory.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AssetCategoryModelTest {

    @Test
    void requiredConstructorAndCollections() {
        AssetCategory category = new AssetCategory("category", 100L);

        Assertions.assertEquals("category", category.getName());
        Assertions.assertEquals(100L, category.getOwnerUserId());
        Assertions.assertNotNull(category.getResources());
        Assertions.assertTrue(category.getResources().isEmpty());
        Assertions.assertNotNull(category.getInnerAssets());
        Assertions.assertTrue(category.getInnerAssets().isEmpty());
    }
}
