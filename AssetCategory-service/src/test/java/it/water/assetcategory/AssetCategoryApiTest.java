package it.water.assetcategory;

import it.water.assetcategory.api.AssetCategoryApi;
import it.water.assetcategory.api.AssetCategoryRepository;
import it.water.assetcategory.api.AssetCategorySystemApi;
import it.water.assetcategory.model.AssetCategory;
import it.water.core.api.asset.AssetCategoryManager;
import it.water.core.api.bundle.Runtime;
import it.water.core.api.model.PaginableResult;
import it.water.core.api.model.Role;
import it.water.core.api.permission.PermissionManager;
import it.water.core.api.registry.ComponentRegistry;
import it.water.core.api.repository.query.Query;
import it.water.core.api.role.RoleManager;
import it.water.core.api.service.Service;
import it.water.core.api.user.UserManager;
import it.water.core.interceptors.annotations.Inject;
import it.water.core.model.exceptions.ValidationException;
import it.water.core.model.exceptions.WaterRuntimeException;
import it.water.core.permission.exceptions.UnauthorizedException;
import it.water.core.testing.utils.bundle.TestRuntimeInitializer;
import it.water.core.testing.utils.junit.WaterTestExtension;
import it.water.core.testing.utils.runtime.TestRuntimeUtils;
import it.water.repository.entity.model.exceptions.DuplicateEntityException;
import it.water.repository.entity.model.exceptions.NoResultException;
import lombok.Setter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Generated with Water Generator.
 * Test class for AssetCategory Services.
 */
@ExtendWith(WaterTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AssetCategoryApiTest implements Service {

    @Inject
    @Setter
    private ComponentRegistry componentRegistry;

    @Inject
    @Setter
    private AssetCategoryApi assetCategoryApi;

    @Inject
    @Setter
    private Runtime runtime;

    @Inject
    @Setter
    private AssetCategoryRepository assetCategoryRepository;

    @Inject
    @Setter
    private AssetCategoryManager assetCategoryManager;

    @Inject
    @Setter
    private PermissionManager permissionManager;

    @Inject
    @Setter
    private UserManager userManager;

    @Inject
    @Setter
    private RoleManager roleManager;

    @SuppressWarnings("unused")
    private it.water.core.api.model.User adminUser;
    private it.water.core.api.model.User managerUser;
    private it.water.core.api.model.User viewerUser;
    private it.water.core.api.model.User editorUser;

    private Role managerRole;
    private Role viewerRole;
    private Role editorRole;

    @BeforeAll
    void beforeAll() {
        managerRole = roleManager.getRole(AssetCategory.DEFAULT_MANAGER_ROLE);
        viewerRole = roleManager.getRole(AssetCategory.DEFAULT_VIEWER_ROLE);
        editorRole = roleManager.getRole(AssetCategory.DEFAULT_EDITOR_ROLE);
        Assertions.assertNotNull(managerRole);
        Assertions.assertNotNull(viewerRole);
        Assertions.assertNotNull(editorRole);

        adminUser = userManager.findUser("admin");
        managerUser = userManager.addUser("categoryManager", "name", "lastname", "manager@test.com", "TempPassword1_", "salt", false);
        viewerUser = userManager.addUser("categoryViewer", "name", "lastname", "viewer@test.com", "TempPassword1_", "salt", false);
        editorUser = userManager.addUser("categoryEditor", "name", "lastname", "editor@test.com", "TempPassword1_", "salt", false);

        roleManager.addRole(managerUser.getId(), managerRole);
        roleManager.addRole(viewerUser.getId(), viewerRole);
        roleManager.addRole(editorUser.getId(), editorRole);

        TestRuntimeUtils.impersonateAdmin(componentRegistry);
    }

    /**
     * Testing basic injection of basic component for AssetCategory entity.
     */
    @Test
    @Order(1)
    void componentsInstantiatedCorrectly() {
        this.assetCategoryApi = this.componentRegistry.findComponent(AssetCategoryApi.class, null);
        Assertions.assertNotNull(this.assetCategoryApi);
        Assertions.assertNotNull(this.componentRegistry.findComponent(AssetCategorySystemApi.class, null));
        this.assetCategoryRepository = this.componentRegistry.findComponent(AssetCategoryRepository.class, null);
        Assertions.assertNotNull(this.assetCategoryRepository);
        this.assetCategoryManager = this.componentRegistry.findComponent(AssetCategoryManager.class, null);
        Assertions.assertNotNull(this.assetCategoryManager);
    }

    /**
     * Testing simple save and version increment
     */
    @Test
    @Order(2)
    void saveOk() {
        AssetCategory entity = createAssetCategory(0);
        entity = this.assetCategoryApi.save(entity);
        Assertions.assertEquals(1, entity.getEntityVersion());
        Assertions.assertTrue(entity.getId() > 0);
        Assertions.assertEquals("Category0", entity.getName());
    }

    /**
     * Testing update logic
     */
    @Test
    @Order(3)
    void updateShouldWork() {
        Query q = this.assetCategoryRepository.getQueryBuilderInstance().createQueryFilter("name=Category0");
        AssetCategory entity = this.assetCategoryApi.find(q);
        Assertions.assertNotNull(entity);
        String newName = entity.getName() + "Updated";
        entity.setName(newName);
        entity = this.assetCategoryApi.update(entity);
        Assertions.assertEquals(newName, entity.getName());
        Assertions.assertEquals(2, entity.getEntityVersion());
    }

    /**
     * Testing update failure with wrong version
     */
    @Test
    @Order(4)
    void updateShouldFailWithWrongVersion() {
        Query q = this.assetCategoryRepository.getQueryBuilderInstance().createQueryFilter("name=Category0Updated");
        AssetCategory errorEntity = this.assetCategoryApi.find(q);
        Assertions.assertEquals("Category0Updated", errorEntity.getName());
        Assertions.assertEquals(2, errorEntity.getEntityVersion());
        errorEntity.setEntityVersion(1);
        Assertions.assertThrows(WaterRuntimeException.class, () -> this.assetCategoryApi.update(errorEntity));
    }

    /**
     * Testing finding all entries with no pagination
     */
    @Test
    @Order(5)
    void findAllShouldWork() {
        PaginableResult<AssetCategory> all = this.assetCategoryApi.findAll(null, -1, -1, null);
        Assertions.assertTrue(all.getResults().size() >= 1);
    }

    /**
     * Testing finding all entries with pagination
     */
    @Test
    @Order(6)
    void findAllPaginatedShouldWork() {
        for (int i = 2; i < 11; i++) {
            AssetCategory u = createAssetCategory(i);
            this.assetCategoryApi.save(u);
        }
        PaginableResult<AssetCategory> paginated = this.assetCategoryApi.findAll(null, 5, 1, null);
        Assertions.assertEquals(5, paginated.getResults().size());
        Assertions.assertEquals(1, paginated.getCurrentPage());
    }

    /**
     * Testing AssetCategoryManager - adding categories to resources
     */
    @Test
    @Order(7)
    void assetCategoryManagerAddShouldWork() {
        AssetCategory category = createAssetCategory(200);
        category = this.assetCategoryApi.save(category);

        // Add resource to category
        String resourceName = "it.water.test.TestEntity";
        long resourceId = 999L;

        this.assetCategoryManager.addAssetCategory(resourceName, resourceId, category.getId());

        // Find categories for resource
        long[] categories = this.assetCategoryManager.findAssetCategories(resourceName, resourceId);
        Assertions.assertEquals(1, categories.length);
        Assertions.assertEquals(category.getId(), categories[0]);
    }

    /**
     * Testing AssetCategoryManager - removing categories from resources
     */
    @Test
    @Order(8)
    void assetCategoryManagerRemoveShouldWork() {
        Query q = this.assetCategoryRepository.getQueryBuilderInstance().createQueryFilter("name=Category200");
        AssetCategory category = this.assetCategoryApi.find(q);

        String resourceName = "it.water.test.TestEntity";
        long resourceId = 999L;

        this.assetCategoryManager.removeAssetCategory(resourceName, resourceId, category.getId());

        long[] categories = this.assetCategoryManager.findAssetCategories(resourceName, resourceId);
        Assertions.assertEquals(0, categories.length);
    }

    /**
     * Testing failure on validation failure
     */
    @Test
    @Order(9)
    void saveShouldFailOnValidationFailure() {
        AssetCategory newEntity = createAssetCategory(300);
        newEntity.setName("<script>alert('xss')</script>");
        Assertions.assertThrows(ValidationException.class, () -> this.assetCategoryApi.save(newEntity));
    }

    /**
     * Testing manager role can do everything
     */
    @Test
    @Order(10)
    void managerCanDoEverything() {
        TestRuntimeInitializer.getInstance().impersonate(managerUser, runtime);
        final AssetCategory entity = createAssetCategory(400);
        AssetCategory savedEntity = Assertions.assertDoesNotThrow(() -> this.assetCategoryApi.save(entity));
        savedEntity.setName("ManagerUpdatedCategory");
        Assertions.assertDoesNotThrow(() -> this.assetCategoryApi.update(savedEntity));
        Assertions.assertDoesNotThrow(() -> this.assetCategoryApi.find(savedEntity.getId()));
        Assertions.assertDoesNotThrow(() -> this.assetCategoryApi.remove(savedEntity.getId()));
    }

    /**
     * Testing viewer cannot save, update, or remove
     */
    @Test
    @Order(11)
    void viewerCannotSaveOrUpdateOrRemove() {
        TestRuntimeInitializer.getInstance().impersonate(viewerUser, runtime);
        final AssetCategory entity = createAssetCategory(500);
        Assertions.assertThrows(UnauthorizedException.class, () -> this.assetCategoryApi.save(entity));
        Assertions.assertEquals(0, this.assetCategoryApi.findAll(null, -1, -1, null).getResults().size());
    }

    /**
     * Testing editor cannot remove
     */
    @Test
    @Order(12)
    void editorCannotRemove() {
        TestRuntimeInitializer.getInstance().impersonate(editorUser, runtime);
        final AssetCategory entity = createAssetCategory(600);
        AssetCategory savedEntity = Assertions.assertDoesNotThrow(() -> this.assetCategoryApi.save(entity));
        savedEntity.setName("EditorUpdatedCategory");
        Assertions.assertDoesNotThrow(() -> this.assetCategoryApi.update(savedEntity));
        Assertions.assertDoesNotThrow(() -> this.assetCategoryApi.find(savedEntity.getId()));
        long savedEntityId = savedEntity.getId();
        Assertions.assertThrows(UnauthorizedException.class, () -> this.assetCategoryApi.remove(savedEntityId));
    }

    /**
     * Testing owned resource access by owner only
     */
    @Test
    @Order(13)
    void ownedResourceShouldBeAccessedOnlyByOwner() {
        TestRuntimeInitializer.getInstance().impersonate(editorUser, runtime);
        final AssetCategory entity = createAssetCategory(700);
        AssetCategory savedEntity = Assertions.assertDoesNotThrow(() -> this.assetCategoryApi.save(entity));
        Assertions.assertDoesNotThrow(() -> this.assetCategoryApi.find(savedEntity.getId()));

        TestRuntimeInitializer.getInstance().impersonate(managerUser, runtime);
        long savedEntityId = savedEntity.getId();
        Assertions.assertThrows(NoResultException.class, () -> this.assetCategoryApi.find(savedEntityId));
    }

    @Test
    @Order(14)
    void categoryWithParent() {
        AssetCategory parentCategory = createAssetCategory(1000);
        parentCategory = this.assetCategoryApi.save(parentCategory);
        AssetCategory firstChildCategory = createAssetCategory(1001);
        firstChildCategory.setParent(parentCategory);
        firstChildCategory = this.assetCategoryApi.save(firstChildCategory);
        Assertions.assertNotNull(firstChildCategory.getParent());
        Assertions.assertEquals(parentCategory.getId(), firstChildCategory.getParent().getId());
    }

    @Test
    @Order(15)
    void categoriesSameParent() {
        AssetCategory parentCategory = createAssetCategory(1002);
        parentCategory = this.assetCategoryApi.save(parentCategory);
        AssetCategory firstChildCategory = createAssetCategory(1003);
        firstChildCategory.setParent(parentCategory);
        firstChildCategory = this.assetCategoryApi.save(firstChildCategory);
        AssetCategory secondChildCategory = createAssetCategory(1004);
        secondChildCategory.setParent(parentCategory);
        secondChildCategory = this.assetCategoryApi.save(secondChildCategory);
        Assertions.assertEquals(firstChildCategory.getParent().getId(), secondChildCategory.getParent().getId());
    }

    @Test
    @Order(16)
    void categoriesSameNameDifferentParent() {
        AssetCategory parentCategory = createAssetCategory(1005);
        parentCategory = this.assetCategoryApi.save(parentCategory);
        AssetCategory firstChildCategory = createAssetCategory(1006);
        firstChildCategory.setParent(parentCategory);
        firstChildCategory.setName("firstChildCategory");
        firstChildCategory = this.assetCategoryApi.save(firstChildCategory);
        AssetCategory secondChildCategory = createAssetCategory(1007);
        secondChildCategory.setName("firstChildCategory");
        //secondChildCategory.setParent(parentCategory);
        secondChildCategory = this.assetCategoryApi.save(secondChildCategory);
        Assertions.assertEquals(firstChildCategory.getName(), secondChildCategory.getName());
        Assertions.assertNotNull(firstChildCategory.getParent());
        Assertions.assertNull(secondChildCategory.getParent());
    }

    @Test
    @Order(17)
    void categoriesSameNameSameParent() {
        AssetCategory parentCategory = createAssetCategory(1008);
        parentCategory = this.assetCategoryApi.save(parentCategory);
        AssetCategory firstChildCategory = createAssetCategory(1009);
        firstChildCategory.setParent(parentCategory);
        firstChildCategory.setName("firstChildCategory");
        firstChildCategory = this.assetCategoryApi.save(firstChildCategory);
        AssetCategory secondChildCategory = createAssetCategory(1010);
        secondChildCategory.setName("firstChildCategory");
        secondChildCategory.setParent(parentCategory);
        //secondChildCategory = this.assetCategoryApi.save(secondChildCategory);
        Assertions.assertEquals(firstChildCategory.getName(), secondChildCategory.getName());
        Assertions.assertEquals(firstChildCategory.getParent().getId(), secondChildCategory.getParent().getId());
        Assertions.assertThrows(DuplicateEntityException.class, () -> this.assetCategoryApi.save(secondChildCategory));
    }

    @Test
    @Order(18)
    void findAssetCategoryResourceShouldWork() {
        TestRuntimeUtils.impersonateAdmin(componentRegistry);
        AssetCategory category = createAssetCategory(1100);
        category = this.assetCategoryApi.save(category);

        String resourceName = "it.water.test.FindResourceEntity";
        long resourceId = 11000L;
        this.assetCategoryManager.addAssetCategory(resourceName, resourceId, category.getId());

        it.water.core.api.model.AssetCategoryResource resource =
                this.assetCategoryManager.findAssetCategoryResource(resourceName, resourceId, category.getId());
        Assertions.assertNotNull(resource);
        Assertions.assertEquals(resourceName, resource.getResourceName());
        Assertions.assertEquals(resourceId, resource.getResourceId());
        Assertions.assertEquals(category.getId(), resource.getCategoryId());
    }

    @Test
    @Order(19)
    void findAssetCategoryResourceShouldReturnNullWhenAssociationDoesNotExist() {
        TestRuntimeUtils.impersonateAdmin(componentRegistry);
        AssetCategory category = createAssetCategory(1101);
        category = this.assetCategoryApi.save(category);

        it.water.core.api.model.AssetCategoryResource resource =
                this.assetCategoryManager.findAssetCategoryResource("it.water.test.MissingEntity", 999999L, category.getId());
        Assertions.assertNull(resource);
    }

    @Test
    @Order(20)
    void addAssetCategoryShouldBeIdempotent() {
        TestRuntimeUtils.impersonateAdmin(componentRegistry);
        AssetCategory category = createAssetCategory(1102);
        category = this.assetCategoryApi.save(category);

        String resourceName = "it.water.test.IdempotentEntity";
        long resourceId = 12000L;
        this.assetCategoryManager.addAssetCategory(resourceName, resourceId, category.getId());
        this.assetCategoryManager.addAssetCategory(resourceName, resourceId, category.getId());

        long[] categories = this.assetCategoryManager.findAssetCategories(resourceName, resourceId);
        Assertions.assertEquals(1, categories.length);
        Assertions.assertEquals(category.getId(), categories[0]);
    }

    @Test
    @Order(21)
    void addAndRemoveAssetCategoriesShouldWorkWithMultipleIds() {
        TestRuntimeUtils.impersonateAdmin(componentRegistry);
        AssetCategory first = this.assetCategoryApi.save(createAssetCategory(1103));
        AssetCategory second = this.assetCategoryApi.save(createAssetCategory(1104));

        String resourceName = "it.water.test.MultiEntity";
        long resourceId = 13000L;
        this.assetCategoryManager.addAssetCategories(resourceName, resourceId, new long[]{first.getId(), second.getId()});
        long[] categoriesAfterAdd = this.assetCategoryManager.findAssetCategories(resourceName, resourceId);
        Assertions.assertEquals(2, categoriesAfterAdd.length);

        this.assetCategoryManager.removeAssetCategories(resourceName, resourceId, new long[]{first.getId(), second.getId()});
        long[] categoriesAfterRemove = this.assetCategoryManager.findAssetCategories(resourceName, resourceId);
        Assertions.assertEquals(0, categoriesAfterRemove.length);
    }

    @Test
    @Order(22)
    void removingParentCategoryShouldCascadeOnChildren() {
        TestRuntimeUtils.impersonateAdmin(componentRegistry);
        AssetCategory parent = this.assetCategoryApi.save(createAssetCategory(1105));

        AssetCategory child = createAssetCategory(1106);
        child.setParent(parent);
        child = this.assetCategoryApi.save(child);

        this.assetCategoryApi.remove(parent.getId());
        long childId = child.getId();
        Assertions.assertThrows(NoResultException.class, () -> this.assetCategoryApi.find(childId));
    }

    @Test
    @Order(23)
    void removingResourceAssociationShouldDeleteOrphanAssociation() {
        TestRuntimeUtils.impersonateAdmin(componentRegistry);
        AssetCategory category = createAssetCategory(1107);
        category = this.assetCategoryApi.save(category);

        String resourceName = "it.water.test.OrphanEntity";
        long resourceId = 14000L;
        this.assetCategoryManager.addAssetCategory(resourceName, resourceId, category.getId());
        this.assetCategoryManager.removeAssetCategory(resourceName, resourceId, category.getId());

        it.water.core.api.model.AssetCategoryResource resource =
                this.assetCategoryManager.findAssetCategoryResource(resourceName, resourceId, category.getId());
        Assertions.assertNull(resource);
    }







    /**
     * Cleanup - remove all test entities
     */
    @Test
    @Order(99)
    void cleanup() {
        TestRuntimeUtils.impersonateAdmin(componentRegistry);
        PaginableResult<AssetCategory> all = this.assetCategoryApi.findAll(null, -1, -1, null);
        all.getResults().forEach(entity -> {
            try {
                this.assetCategoryApi.remove(entity.getId());
            } catch (Exception e) {
                // Ignore errors
            }
        });
    }

    private AssetCategory createAssetCategory(int seed) {
        // Use seed as a unique identifier, but set owner to a valid user id
        AssetCategory category = new AssetCategory("Category" + seed, (long) (seed + 1));
        return category;
    }
}
