package com.sdps.module.bpm.service.definition;

import static com.sdps.common.exception.util.ServiceExceptionUtil.exception;
import static com.sdps.common.util.collection.CollectionUtils.convertMap;
import static com.sdps.module.bpm.enums.ErrorCodeConstants.FORM_NOT_EXISTS;
import static com.sdps.module.bpm.enums.ErrorCodeConstants.MODEL_DEPLOY_FAIL_FORM_NOT_CONFIG;
import static com.sdps.module.bpm.enums.ErrorCodeConstants.MODEL_DEPLOY_FAIL_TASK_INFO_EQUALS;
import static com.sdps.module.bpm.enums.ErrorCodeConstants.MODEL_KEY_EXISTS;
import static com.sdps.module.bpm.enums.ErrorCodeConstants.MODEL_KEY_VALID;
import static com.sdps.module.bpm.enums.ErrorCodeConstants.MODEL_NOT_EXISTS;
import static com.sdps.module.bpm.enums.ErrorCodeConstants.PROCESS_DEFINITION_NOT_EXISTS;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.common.engine.impl.util.io.BytesStreamSource;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ModelQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import com.sdps.common.pojo.PageResult;
import com.sdps.common.util.collection.CollectionUtils;
import com.sdps.common.util.json.JsonUtils;
import com.sdps.common.util.object.PageUtils;
import com.sdps.common.util.validation.ValidationUtils;
import com.sdps.module.bpm.controller.admin.definition.vo.model.BpmModelCreateReqVO;
import com.sdps.module.bpm.controller.admin.definition.vo.model.BpmModelPageItemRespVO;
import com.sdps.module.bpm.controller.admin.definition.vo.model.BpmModelPageReqVO;
import com.sdps.module.bpm.controller.admin.definition.vo.model.BpmModelRespVO;
import com.sdps.module.bpm.controller.admin.definition.vo.model.BpmModelUpdateReqVO;
import com.sdps.module.bpm.convert.definition.BpmModelConvert;
import com.sdps.module.bpm.dal.dataobject.definition.BpmFormDO;
import com.sdps.module.bpm.enums.definition.BpmModelFormTypeEnum;
import com.sdps.module.bpm.service.definition.dto.BpmModelMetaInfoRespDTO;
import com.sdps.module.bpm.service.definition.dto.BpmProcessDefinitionCreateReqDTO;


/**
 * Flowable??????????????????
 * ???????????? Flowable {@link Model} ?????????
 *
 * @author yunlongn
 * @author ????????????
 * @author jason
 */
@Service
@Validated
public class BpmModelServiceImpl implements BpmModelService {

    @Resource
    private RepositoryService repositoryService;
    @Resource
    private BpmProcessDefinitionService processDefinitionService;
    @Resource
    private BpmFormService bpmFormService;
    @Resource
    private BpmTaskAssignRuleService taskAssignRuleService;

    @Override
    public PageResult<BpmModelPageItemRespVO> getModelPage(BpmModelPageReqVO pageVO) {
        ModelQuery modelQuery = repositoryService.createModelQuery();
        if (StrUtil.isNotBlank(pageVO.getKey())) {
            modelQuery.modelKey(pageVO.getKey());
        }
        if (StrUtil.isNotBlank(pageVO.getName())) {
            modelQuery.modelNameLike("%" + pageVO.getName() + "%"); // ????????????
        }
        if (StrUtil.isNotBlank(pageVO.getCategory())) {
            modelQuery.modelCategory(pageVO.getCategory());
        }
        // ????????????
        List<Model> models = modelQuery.orderByCreateTime().desc()
                .listPage(PageUtils.getStart(pageVO), pageVO.getPageSize());

        // ?????? Form Map
        Set<Long> formIds = CollectionUtils.convertSet(models, model -> {
            BpmModelMetaInfoRespDTO metaInfo = JsonUtils.parseObject(model.getMetaInfo(), BpmModelMetaInfoRespDTO.class);
            return metaInfo != null ? metaInfo.getFormId() : null;
        });
        Map<Long, BpmFormDO> formMap = bpmFormService.getFormMap(formIds);

        // ?????? Deployment Map
        Set<String> deploymentIds = new HashSet<>();
        models.forEach(model -> CollectionUtils.addIfNotNull(deploymentIds, model.getDeploymentId()));
        Map<String, Deployment> deploymentMap = processDefinitionService.getDeploymentMap(deploymentIds);
        // ?????? ProcessDefinition Map
        List<ProcessDefinition> processDefinitions = processDefinitionService.getProcessDefinitionListByDeploymentIds(deploymentIds);
        Map<String, ProcessDefinition> processDefinitionMap = convertMap(processDefinitions, ProcessDefinition::getDeploymentId);

        // ????????????
        long modelCount = modelQuery.count();
        return new PageResult<>(BpmModelConvert.INSTANCE.convertList(models, formMap, deploymentMap, processDefinitionMap), modelCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createModel(@Valid BpmModelCreateReqVO createReqVO, String bpmnXml) {
        checkKeyNCName(createReqVO.getKey());
        // ??????????????????????????????
        Model keyModel = getModelByKey(createReqVO.getKey());
        if (keyModel != null) {
            throw exception(MODEL_KEY_EXISTS, createReqVO.getKey());
        }

        // ??????????????????
        Model model = repositoryService.newModel();
        BpmModelConvert.INSTANCE.copy(model, createReqVO);
        // ??????????????????
        repositoryService.saveModel(model);
        // ?????? BPMN XML
        saveModelBpmnXml(model, bpmnXml);
        return model.getId();
    }

    private Model getModelByKey(String key) {
        return repositoryService.createModelQuery().modelKey(key).singleResult();
    }

    @Override
    public BpmModelRespVO getModel(String id) {
        Model model = repositoryService.getModel(id);
        if (model == null) {
            return null;
        }
        BpmModelRespVO modelRespVO = BpmModelConvert.INSTANCE.convert(model);
        // ?????? bpmn XML
        byte[] bpmnBytes = repositoryService.getModelEditorSource(id);
        modelRespVO.setBpmnXml(StrUtil.utf8Str(bpmnBytes));
        return modelRespVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // ?????????????????????????????????????????????
    public void updateModel(@Valid BpmModelUpdateReqVO updateReqVO) {
        // ????????????????????????
        Model model = repositoryService.getModel(updateReqVO.getId());
        if (model == null) {
            throw exception(MODEL_NOT_EXISTS);
        }

        // ??????????????????
        BpmModelConvert.INSTANCE.copy(model, updateReqVO);
        // ????????????
        repositoryService.saveModel(model);
        // ?????? BPMN XML
        saveModelBpmnXml(model, updateReqVO.getBpmnXml());
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // ?????????????????????????????????????????????
    public void deployModel(String id) {
        // 1.1 ????????????????????????
        Model model = repositoryService.getModel(id);
        if (ObjectUtils.isEmpty(model)) {
            throw exception(MODEL_NOT_EXISTS);
        }
        // 1.2 ???????????????
        // TODO ?????????????????????????????????????????????????????????????????????????????????????????????????????????
        byte[] bpmnBytes = repositoryService.getModelEditorSource(model.getId());
        if (bpmnBytes == null) {
            throw exception(MODEL_NOT_EXISTS);
        }
        // 1.3 ??????????????????
        BpmFormDO form = checkFormConfig(model.getMetaInfo());
        // 1.4 ?????????????????????????????????
        taskAssignRuleService.checkTaskAssignRuleAllConfig(id);

        // 1.5 ?????????????????????????????????????????????????????????????????????
        BpmProcessDefinitionCreateReqDTO definitionCreateReqDTO = BpmModelConvert.INSTANCE.convert2(model, form);
        definitionCreateReqDTO.setBpmnBytes(bpmnBytes);
        if (processDefinitionService.isProcessDefinitionEquals(definitionCreateReqDTO)) { // ???????????????????????????
            ProcessDefinition oldProcessDefinition = processDefinitionService.getProcessDefinitionByDeploymentId(model.getDeploymentId());
            if (oldProcessDefinition != null && taskAssignRuleService.isTaskAssignRulesEquals(model.getId(), oldProcessDefinition.getId())) {
                throw exception(MODEL_DEPLOY_FAIL_TASK_INFO_EQUALS);
            }
        }

        // 2.1 ??????????????????
        String definitionId = processDefinitionService.createProcessDefinition(definitionCreateReqDTO);

        // 2.2 ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
        updateProcessDefinitionSuspended(model.getDeploymentId());

        // 2.3 ?????? model ??? deploymentId???????????????
        ProcessDefinition definition = processDefinitionService.getProcessDefinition(definitionId);
        model.setDeploymentId(definition.getDeploymentId());
        repositoryService.saveModel(model);

        // 2.4 ????????????????????????
        taskAssignRuleService.copyTaskAssignRules(id, definition.getId());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModel(String id) {
        // ????????????????????????
        Model model = repositoryService.getModel(id);
        if (model == null) {
            throw exception(MODEL_NOT_EXISTS);
        }
        // ????????????
        repositoryService.deleteModel(id);
        // ??????????????????
        updateProcessDefinitionSuspended(model.getDeploymentId());
    }

    @Override
    public void updateModelState(String id, Integer state) {
        // ????????????????????????
        Model model = repositoryService.getModel(id);
        if (model == null) {
            throw exception(MODEL_NOT_EXISTS);
        }
        // ????????????????????????
        ProcessDefinition definition = processDefinitionService.getProcessDefinitionByDeploymentId(model.getDeploymentId());
        if (definition == null) {
            throw exception(PROCESS_DEFINITION_NOT_EXISTS);
        }

        // ????????????
        processDefinitionService.updateProcessDefinitionState(definition.getId(), state);
    }

    @Override
    public BpmnModel getBpmnModel(String id) {
        byte[] bpmnBytes = repositoryService.getModelEditorSource(id);
        if (ArrayUtil.isEmpty(bpmnBytes)) {
            return null;
        }
        BpmnXMLConverter converter = new BpmnXMLConverter();
        return converter.convertToBpmnModel(new BytesStreamSource(bpmnBytes), true, true);
    }

    private void checkKeyNCName(String key) {
        if (!ValidationUtils.isXmlNCName(key)) {
            throw exception(MODEL_KEY_VALID);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param metaInfoStr ???????????? metaInfo ??????
     * @return ????????????
     */
    private BpmFormDO checkFormConfig(String metaInfoStr) {
        BpmModelMetaInfoRespDTO metaInfo = JsonUtils.parseObject(metaInfoStr, BpmModelMetaInfoRespDTO.class);
        if (metaInfo == null || metaInfo.getFormType() == null) {
            throw exception(MODEL_DEPLOY_FAIL_FORM_NOT_CONFIG);
        }
        // ??????????????????
        if (Objects.equals(metaInfo.getFormType(), BpmModelFormTypeEnum.NORMAL.getType())) {
            BpmFormDO form = bpmFormService.getForm(metaInfo.getFormId());
            if (form == null) {
                throw exception(FORM_NOT_EXISTS);
            }
            return form;
        }
        return null;
    }

    private void saveModelBpmnXml(Model model, String bpmnXml) {
        if (StrUtil.isEmpty(bpmnXml)) {
            return;
        }
        repositoryService.addModelEditorSource(model.getId(), StrUtil.utf8Bytes(bpmnXml));
    }

    /**
     * ?????? deploymentId ???????????????????????? ????????????deploymentId ???????????????????????????
     *
     * @param deploymentId ????????????Id.
     */
    private void updateProcessDefinitionSuspended(String deploymentId) {
        if (StrUtil.isEmpty(deploymentId)) {
            return;
        }
        ProcessDefinition oldDefinition = processDefinitionService.getProcessDefinitionByDeploymentId(deploymentId);
        if (oldDefinition == null) {
            return;
        }
        processDefinitionService.updateProcessDefinitionState(oldDefinition.getId(), SuspensionState.SUSPENDED.getStateCode());
    }


}
