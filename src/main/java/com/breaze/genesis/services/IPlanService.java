package com.breaze.genesis.services;

import com.breaze.genesis.dto.plan.requests.CreatePlanRequest;
import com.breaze.genesis.dto.plan.responses.CreatePlanResponse;
import com.breaze.genesis.dto.plan.requests.ListPlansRequest;
import com.breaze.genesis.dto.plan.requests.DeletePlanRequest;
import com.breaze.genesis.dto.plan.requests.UpdatePlanRequest;
import com.breaze.genesis.dto.plan.requests.UpdatePlanStatusRequest;
import com.breaze.genesis.dto.plan.responses.DeletePlanResponse;
import com.breaze.genesis.dto.plan.responses.ListPlansResponse;
import com.breaze.genesis.dto.plan.responses.UpdatePlanResponse;
import com.breaze.genesis.dto.plan.responses.UpdatePlanStatusResponse;

/**
 * Contrato de aplicacion para operaciones del dominio de planes.
 *
 * @version 1.0.0
 * @author donpedromz
 */
public interface IPlanService {

    CreatePlanResponse createPlan(CreatePlanRequest request);

    /**
     * Obtiene el listado paginado de planes.
     *
     * @param request criterios de paginacion y ordenamiento
     * @return planes paginados
     */
    ListPlansResponse getPlans(ListPlansRequest request);

    /**
     * Actualiza un plan y crea una nueva version de vigencia.
     *
     * @param planId identificador del plan
     * @param request datos de actualizacion de plan/version
     * @return detalle de la version creada
     */
    UpdatePlanResponse updatePlan(Long planId, UpdatePlanRequest request);

    /**
     * Elimina un plan segun reglas de negocio del dominio.
     *
     * @param request identificador del plan a eliminar
     * @return resultado de la eliminacion
     */
    DeletePlanResponse deletePlan(DeletePlanRequest request);

    /**
     * Activa o desactiva un plan en base a su ultima version.
     *
     * @param request indicador de activacion/desactivacion
     * @return estado actualizado
     */
    UpdatePlanStatusResponse updatePlanStatus(Long planId, UpdatePlanStatusRequest request);
}
