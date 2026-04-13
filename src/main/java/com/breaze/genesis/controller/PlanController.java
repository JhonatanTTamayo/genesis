package com.breaze.genesis.controller;

import com.breaze.genesis.dto.plan.requests.CreatePlanRequest;
import com.breaze.genesis.dto.plan.requests.ListPlansRequest;
import com.breaze.genesis.dto.plan.requests.DeletePlanRequest;
import com.breaze.genesis.dto.plan.requests.UpdatePlanRequest;
import com.breaze.genesis.dto.plan.requests.UpdatePlanStatusRequest;
import com.breaze.genesis.dto.plan.responses.CreatePlanResponse;
import com.breaze.genesis.dto.plan.responses.DeletePlanResponse;
import com.breaze.genesis.dto.plan.responses.ListPlansResponse;
import com.breaze.genesis.dto.plan.responses.UpdatePlanResponse;
import com.breaze.genesis.dto.plan.responses.UpdatePlanStatusResponse;
import com.breaze.genesis.services.IPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
/**
 * Controlador REST para la administracion y consulta del catalogo de planes.
 *
 * @version 1.0.0
 * @author donpedromz
 */
public class PlanController {

    private final IPlanService planService;

    /**
     * Lista los planes disponibles con paginacion.
     *
     * @param request parametros de paginacion y ordenamiento
     * @return respuesta paginada de planes
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ListPlansResponse> getPlans(@Valid @ModelAttribute ListPlansRequest request) {
        return ResponseEntity.ok(planService.getPlans(request));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreatePlanResponse> createPlan(@Valid @RequestBody CreatePlanRequest request) {
        CreatePlanResponse response = planService.createPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualiza completamente un plan y crea una nueva version vigente desde el momento actual.
     *
     * @param id identificador del plan
     * @param request datos de actualizacion y vigencia de la nueva version
     * @return resultado de la actualizacion del plan
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UpdatePlanResponse> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePlanRequest request
    ) {
        return ResponseEntity.ok(planService.updatePlan(id, request));
    }

    /**
     * Elimina un plan si no tiene suscripciones activas ni historial asociado.
     *
     * @param id identificador del plan a eliminar
     * @return resultado de la eliminacion
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeletePlanResponse> deletePlan(@PathVariable Long id) {
        DeletePlanRequest request = DeletePlanRequest.builder()
                .planId(id)
                .build();
        return ResponseEntity.ok(planService.deletePlan(request));
    }

    /**
     * Activa o desactiva un plan actualizando su ultima version vigente.
     *
     * @param id identificador del plan
     * @param request datos de estado de activacion/desactivacion
     * @return resultado del cambio de estado
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UpdatePlanStatusResponse> updatePlanStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePlanStatusRequest request) {
        return ResponseEntity.ok(planService.updatePlanStatus(id, request));
    }
}
