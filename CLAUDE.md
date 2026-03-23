# AssetCategory Module — Hierarchical Resource Categorization

## Purpose
Provides hierarchical category management for Water Framework resources. Entities can be tagged with categories organized in a tree structure (parent/child). Categories are `SharedEntity` resources, meaning they support both ownership and sharing with other users. Does NOT manage tags (that is the `AssetTag` module) and does NOT enforce business rules on which resource types can be categorized.

## Sub-modules

| Sub-module | Runtime | Key Classes |
|---|---|---|
| `AssetCategory-api` | All | `AssetCategoryApi`, `AssetCategorySystemApi`, `AssetCategoryRestApi`, `AssetCategoryRepository` |
| `AssetCategory-model` | All | `AssetCategory`, `WaterAssetCategoryResource`, `AssetCategoryResource` |
| `AssetCategory-service` | Water/OSGi | Service impl, repository, REST controller |
| `AssetCategory-service-spring` | Spring Boot | Spring MVC REST controllers |
| `AssetCategory-service-integration` | All | Integration client for cross-service resolution |

## AssetCategory Entity

```java
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "parent_id"}))
@AccessControl(
    availableActions = {CrudActions.SAVE, CrudActions.UPDATE, CrudActions.FIND, CrudActions.FIND_ALL, CrudActions.REMOVE},
    rolesPermissions = {
        @DefaultRoleAccess(roleName = "assetCategoryManager", actions = {CrudActions.class}),
        @DefaultRoleAccess(roleName = "assetCategoryViewer",  actions = {CrudActions.FIND, CrudActions.FIND_ALL}),
        @DefaultRoleAccess(roleName = "assetCategoryEditor",  actions = {CrudActions.SAVE, CrudActions.UPDATE, CrudActions.FIND, CrudActions.FIND_ALL})
    }
)
public class AssetCategory extends AbstractJpaExpandableEntity implements ProtectedEntity, SharedEntity {
    @NotEmpty @NoMalitiusCode
    @Column(length = 255)
    private String name;                        // Category name (unique per parent)

    @NonNull
    private Long ownerUserId;                   // Owner user ID

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private AssetCategory parent;               // Parent category (null = root)

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private Set<AssetCategory> innerAssets;     // Children (deleted when parent deleted)

    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private Set<WaterAssetCategoryResource> resources; // Resources in this category
}
```

## WaterAssetCategoryResource Entity

Join table mapping arbitrary resources to categories:

```java
@Entity
public class WaterAssetCategoryResource extends AbstractJpaEntity
    implements ProtectedEntity, AssetCategoryResource {

    private String resourceName;    // Fully-qualified class name of the resource
    private long resourceId;        // Resource primary key

    @ManyToOne
    @JoinColumn(name = "category_id")
    private AssetCategory category; // Category this resource belongs to
}
```

## Key Operations

### AssetCategoryApi (permission-checked)
```java
// Inherits from BaseEntityApi<AssetCategory>:
AssetCategory save(AssetCategory entity);
AssetCategory update(AssetCategory entity);
AssetCategory find(long id);
PaginableResult<AssetCategory> findAll(int delta, int page, Query filter, QueryOrder order);
void remove(long id);
```

### AssetCategorySystemApi (bypasses permissions)
Same CRUD methods, callable without a logged-in user context.
Used internally when populating category trees for other resources.

## Key Flow

```
Client
  └─► AssetCategoryRestControllerImpl (@FrameworkRestController)
       └─► AssetCategoryServiceImpl (@FrameworkComponent)
            └─► AssetCategorySystemServiceImpl
                 └─► AssetCategoryRepository (JPA)
                      └─► AssetCategory table
```

## REST Endpoints

| Method | Path | Permission | Description |
|---|---|---|---|
| `POST` | `/assetcategories` | assetCategoryManager | Create category |
| `PUT` | `/assetcategories` | assetCategoryManager / assetCategoryEditor | Update category |
| `GET` | `/assetcategories/{id}` | assetCategoryViewer | Find by ID |
| `GET` | `/assetcategories` | assetCategoryViewer | Find all (paginated) |
| `DELETE` | `/assetcategories/{id}` | assetCategoryManager | Delete category (cascades to children) |

All endpoints require `@LoggedIn`. Responses use `@JsonView(WaterJsonView.Public.class)`.

## Default Roles

| Role | Allowed Actions |
|---|---|
| `assetCategoryManager` | SAVE, UPDATE, FIND, FIND_ALL, REMOVE |
| `assetCategoryViewer` | FIND, FIND_ALL |
| `assetCategoryEditor` | SAVE, UPDATE, FIND, FIND_ALL |

## Hierarchical Tree Behavior
- `parent = null` → root category
- Deleting a category cascades to all `innerAssets` (children) via `CascadeType.REMOVE`
- `name` must be unique within the same parent: `UNIQUE(name, parent_id)`
- Resources assigned to a deleted category are also removed (cascade via `WaterAssetCategoryResource`)

## Dependencies
- `it.water.repository.jpa:JpaRepository-api` — `AbstractJpaExpandableEntity`
- `it.water.core:Core-permission` — `@AccessControl`, `CrudActions`, `SharedEntity`
- `it.water.rest:Rest-api` — `RestApi`, `@LoggedIn`
- `jakarta.persistence:jakarta.persistence-api` — JPA 3.0 annotations

## Testing
- Unit tests: `WaterTestExtension` — test CRUD + tree structure + permission scenarios
- REST tests: **Karate only** (never JUnit direct calls to `AssetCategoryRestController`)
- Create test categories with `save()` in `@BeforeAll`, test hierarchical cascades
- Impersonate admin with `TestRuntimeUtils.impersonateAdmin(componentRegistry)`

## Code Generation Rules
- Never add business logic in `AssetCategoryServiceImpl` — all logic goes in `AssetCategorySystemServiceImpl`
- `innerAssets` loaded lazily by default — avoid N+1 queries when listing root categories
- `resources` is `FetchType.EAGER` — keep resource lists small
- `SharedEntity` impl: `ownerUserId` is set automatically from the logged-in user context
- To assign a resource to a category, create a `WaterAssetCategoryResource` instance (not done via `AssetCategoryApi`)
- REST controllers tested **exclusively via Karate**
