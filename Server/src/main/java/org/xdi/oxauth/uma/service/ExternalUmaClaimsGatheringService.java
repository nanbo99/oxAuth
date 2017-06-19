package org.xdi.oxauth.uma.service;

import org.xdi.model.custom.script.CustomScriptType;
import org.xdi.model.custom.script.conf.CustomScriptConfiguration;
import org.xdi.model.custom.script.type.uma.UmaClaimsGatheringType;
import org.xdi.oxauth.uma.authorization.UmaGatherContext;
import org.xdi.service.LookupService;
import org.xdi.service.custom.script.ExternalScriptService;
import org.xdi.util.StringHelper;

import javax.ejb.DependsOn;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuriyz on 06/18/2017.
 */
@ApplicationScoped
@DependsOn("appInitializer")
@Named
public class ExternalUmaClaimsGatheringService extends ExternalScriptService {

    @Inject
    private LookupService lookupService;

    protected Map<String, CustomScriptConfiguration> scriptInumMap;

    public ExternalUmaClaimsGatheringService() {
        super(CustomScriptType.UMA_CLAIMS_GATHERING);
    }

    @Override
    protected void reloadExternal() {
        this.scriptInumMap = buildExternalConfigurationsInumMap(this.customScriptConfigurations);
    }

    private Map<String, CustomScriptConfiguration> buildExternalConfigurationsInumMap(List<CustomScriptConfiguration> customScriptConfigurations) {
        Map<String, CustomScriptConfiguration> reloadedExternalConfigurations = new HashMap<String, CustomScriptConfiguration>(customScriptConfigurations.size());

        for (CustomScriptConfiguration customScriptConfiguration : customScriptConfigurations) {
            reloadedExternalConfigurations.put(customScriptConfiguration.getInum(), customScriptConfiguration);
        }

        return reloadedExternalConfigurations;
    }

    public CustomScriptConfiguration getScriptByDn(String scriptDn) {
        String authorizationPolicyInum = lookupService.getInumFromDn(scriptDn);

        return getScriptByInum(authorizationPolicyInum);
    }

    public CustomScriptConfiguration getScriptByInum(String inum) {
        if (StringHelper.isEmpty(inum)) {
            return null;
        }

        return this.scriptInumMap.get(inum);
    }

    private static UmaClaimsGatheringType gatherScript(CustomScriptConfiguration script) {
        return (UmaClaimsGatheringType) script.getExternalType();
    }

    public boolean gather(CustomScriptConfiguration script, int step, UmaGatherContext context) {
        try {
            log.debug("Executing python 'gather' method, script: " + script.getName());
            boolean result = gatherScript(script).gather(step, context);
            log.debug("python 'gather' result: " + result);
            return result;
        } catch (Exception ex) {
            log.error("Failed to execute python 'gather' method, script: " + script.getName() + ", message: " + ex.getMessage(), ex);
            return false;
        }
    }

    public int getNextStep(CustomScriptConfiguration script, int step, UmaGatherContext context) {
        try {
            log.debug("Executing python 'getNextStep' method, script: " + script.getName());
            int result = gatherScript(script).getNextStep(step, context);
            log.debug("python 'getNextStep' result: " + result);
            return result;
        } catch (Exception ex) {
            log.error("Failed to execute python 'getNextStep' method, script: " + script.getName() + ", message: " + ex.getMessage(), ex);
            return -1;
        }
    }

    public boolean prepareForStep(CustomScriptConfiguration script, int step, UmaGatherContext context) {
        try {
            log.debug("Executing python 'prepareForStep' method, script: " + script.getName());
            boolean result = gatherScript(script).prepareForStep(step, context);
            log.debug("python 'prepareForStep' result: " + result);
            return result;
        } catch (Exception ex) {
            log.error("Failed to execute python 'prepareForStep' method, script: " + script.getName() + ", message: " + ex.getMessage(), ex);
            return false;
        }
    }

    public int getStepsCount(CustomScriptConfiguration script, UmaGatherContext context) {
        try {
            log.debug("Executing python 'getStepsCount' method, script: " + script.getName());
            int result = gatherScript(script).getStepsCount(context);
            log.debug("python 'getStepsCount' result: " + result);
            return result;
        } catch (Exception ex) {
            log.error("Failed to execute python 'getStepsCount' method, script: " + script.getName() + ", message: " + ex.getMessage(), ex);
            return -1;
        }
    }

    public String getPageForStep(CustomScriptConfiguration script, int step, UmaGatherContext context) {
        try {
            log.debug("Executing python 'getPageForStep' method, script: " + script.getName());
            String result = gatherScript(script).getPageForStep(step, context);
            log.debug("python 'getPageForStep' result: " + result);
            return result;
        } catch (Exception ex) {
            log.error("Failed to execute python 'getPageForStep' method, script: " + script.getName() + ", message: " + ex.getMessage(), ex);
            return "";
        }
    }
}