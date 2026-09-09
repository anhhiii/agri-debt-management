package com.manage.debt_management.service;

import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.manage.debt_management.dto.PermissionSyncResultDTO;
import com.manage.debt_management.model.Permission;
import com.manage.debt_management.repository.PermissionRepository;

/**
 * Quét toàn bộ handler {@link RequestMappingInfo} của các controller trong package
 * {@code com.manage.debt_management.controller}, tạo mã dạng {@code path:METHOD} (giống catalog),
 * rồi chèn vào MongoDB nếu chưa có — không xóa và không ghi đè {@code description}/{@code module} đã lưu.
 */
@Service
public class PermissionCatalogSyncService {

    private static final Logger log = LoggerFactory.getLogger(PermissionCatalogSyncService.class);

    private static final String CONTROLLER_BASE = "com.manage.debt_management.controller";

    private static final EnumSet<RequestMethod> ALL_METHODS_FALLBACK =
            EnumSet.of(RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE,
                    RequestMethod.PATCH, RequestMethod.HEAD, RequestMethod.OPTIONS);

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private PermissionRepository permissionRepository;

    @Value("${app.permissions.sync-on-startup:true}")
    private boolean syncOnStartup;

    @EventListener(ApplicationReadyEvent.class)
    public void syncOnStartup() {
        if (!syncOnStartup) {
            log.debug("Bỏ qua đồng bộ permission catalog từ Spring MVC (app.permissions.sync-on-startup=false)");
            return;
        }
        PermissionSyncResultDTO r = syncMissingPermissionsFromMvc();
        log.info("Đồng bộ permission catalog từ Spring MVC: {} bản ghi mới, {} đã tồn tại", r.inserted(),
                r.skipped());
    }

    /**
     * Đọc toàn bộ mapping phù hợp, chèn permission thiếu.
     */
    public PermissionSyncResultDTO syncMissingPermissionsFromMvc() {
        Set<String> discovered = discoverPermissionCodes();
        int inserted = 0;
        int skipped = 0;
        for (String code : discovered) {
            if (permissionRepository.findByCode(code).isPresent()) {
                skipped++;
                continue;
            }
            String path = code.substring(0, code.lastIndexOf(':'));
            Permission p = Permission.builder()
                    .code(code)
                    .description("Tự động: " + code)
                    .module(moduleFromApiPath(path))
                    .build();
            permissionRepository.save(p);
            inserted++;
        }
        return new PermissionSyncResultDTO(inserted, skipped);
    }

    private Set<String> discoverPermissionCodes() {
        Set<String> codes = new TreeSet<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> e : requestMappingHandlerMapping.getHandlerMethods()
                .entrySet()) {
            HandlerMethod hm = e.getValue();
            if (!hm.getBeanType().getPackageName().startsWith(CONTROLLER_BASE)) {
                continue;
            }
            RequestMappingInfo info = e.getKey();
            Set<String> patterns = extractPatterns(info);
            Set<RequestMethod> methods = info.getMethodsCondition().getMethods();
            if (methods == null || methods.isEmpty()) {
                methods = ALL_METHODS_FALLBACK;
            }
            for (String pattern : patterns) {
                if (!pattern.startsWith("/api")) {
                    continue;
                }
                for (RequestMethod m : methods) {
                    codes.add(pattern + ":" + m.name());
                }
            }
        }
        return codes;
    }

    private static Set<String> extractPatterns(RequestMappingInfo info) {
        Set<String> patterns = new LinkedHashSet<>();
        if (info.getPathPatternsCondition() != null) {
            info.getPathPatternsCondition().getPatterns()
                    .forEach(p -> patterns.add(p.getPatternString()));
        }
        if (patterns.isEmpty() && info.getPatternsCondition() != null) {
            patterns.addAll(info.getPatternsCondition().getPatterns());
        }
        return patterns;
    }

    /**
     * Ví dụ {@code /api/v1/customers} → {@code customers}; fallback {@code API}.
     */
    public static String moduleFromApiPath(String path) {
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length; i++) {
            if ("v1".equals(parts[i]) && i + 1 < parts.length && !parts[i + 1].isEmpty()) {
                return parts[i + 1];
            }
        }
        if (parts.length > 1 && !parts[1].isEmpty()) {
            return parts[1];
        }
        return "API";
    }
}