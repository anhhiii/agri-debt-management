package com.manage.debt_management.security;

import java.util.List;

/**
 * Mã quyền dạng {@code fullPath:HTTP_METHOD} — trùng với catalog
 * {@link com.manage.debt_management.service.PermissionCatalogSyncService}
 * (pattern Spring MVC + tên method).
 * Dùng trong {@code @PreAuthorize("hasAuthority(T(...ApiPermissions).CONSTANT)")}.
 */
public final class ApiPermissions {

    private ApiPermissions() {
    }

    /* --- contracts --- */
    public static final String CONTRACTS_GET = "/api/v1/contracts:GET";
    public static final String CONTRACTS_POST = "/api/v1/contracts:POST";
    public static final String CONTRACTS_ID_GET = "/api/v1/contracts/{id}:GET";
    public static final String CONTRACTS_ID_DELETE = "/api/v1/contracts/{id}:DELETE";

    /* --- customers --- */
    public static final String CUSTOMERS_GET = "/api/v1/customers:GET";
    public static final String CUSTOMERS_POST = "/api/v1/customers:POST";
    public static final String CUSTOMERS_ID_GET = "/api/v1/customers/{id}:GET";
    public static final String CUSTOMERS_SEARCH_GET = "/api/v1/customers/search:GET";
    public static final String CUSTOMERS_ID_PUT = "/api/v1/customers/{id}:PUT";
    public static final String CUSTOMERS_ID_DELETE = "/api/v1/customers/{id}:DELETE";

    /* --- auth (JWT; login/refreshtoken vẫn permitAll ở filter chain) --- */
    public static final String AUTH_REGISTER_POST = "/api/v1/auth/register:POST";
    public static final String AUTH_ID_PUT = "/api/v1/auth/{id}:PUT";

    /* --- dashboard --- */
    public static final String DASHBOARD_STATS_GET = "/api/v1/dashboard/stats:GET";

    /* --- payments --- */
    public static final String PAYMENTS_COLLECT_POST = "/api/v1/payments/collect:POST";

    /* --- users --- */
    public static final String USERS_GET = "/api/v1/users:GET";
    public static final String USERS_ID_DELETE = "/api/v1/users/{id}:DELETE";

    /* --- roles --- */
    public static final String ROLES_GET = "/api/v1/roles:GET";
    public static final String ROLES_ID_GET = "/api/v1/roles/{id}:GET";
    public static final String ROLES_POST = "/api/v1/roles:POST";
    public static final String ROLES_ID_PUT = "/api/v1/roles/{id}:PUT";
    public static final String ROLES_ID_DELETE = "/api/v1/roles/{id}:DELETE";

    /* --- permissions catalog --- */
    public static final String PERMISSIONS_GET = "/api/v1/permissions:GET";
    public static final String PERMISSIONS_SYNC_POST = "/api/v1/permissions/sync:POST";

    /** Toàn bộ mã — gán cho vai trò ADMIN. */
    public static List<String> allCodesForAdmin() {
        return List.of(
                CONTRACTS_GET,
                CONTRACTS_POST,
                CONTRACTS_ID_GET,
                CONTRACTS_ID_DELETE,
                CUSTOMERS_GET,
                CUSTOMERS_POST,
                CUSTOMERS_ID_GET,
                CUSTOMERS_SEARCH_GET,
                CUSTOMERS_ID_PUT,
                CUSTOMERS_ID_DELETE,
                AUTH_REGISTER_POST,
                AUTH_ID_PUT,
                DASHBOARD_STATS_GET,
                PAYMENTS_COLLECT_POST,
                USERS_GET,
                USERS_ID_DELETE,
                ROLES_GET,
                ROLES_ID_GET,
                ROLES_POST,
                ROLES_ID_PUT,
                ROLES_ID_DELETE,
                PERMISSIONS_GET,
                PERMISSIONS_SYNC_POST);
    }

    /** Quyền mặc định nhân viên (bán hàng / thu tiền). */
    public static List<String> defaultCodesForStaff() {
        return List.of(
                CONTRACTS_GET,
                CONTRACTS_POST,
                CONTRACTS_ID_GET,
                PAYMENTS_COLLECT_POST,
                DASHBOARD_STATS_GET,
                CUSTOMERS_GET,
                CUSTOMERS_SEARCH_GET,
                CUSTOMERS_POST,
                CUSTOMERS_ID_GET,
                CUSTOMERS_ID_PUT);
    }
}