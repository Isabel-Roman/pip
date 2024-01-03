/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package us.dit.pip;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.core.util.IdentityCoreConstants;
import org.wso2.carbon.identity.entitlement.pip.AbstractPIPAttributeFinder;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * This is a sample implementation of PIPAttributeFinder in WSO2 entitlement engine.
 * Here we return the hardcoded attribute values from getAttributeValues() method.
 * You can customize this sample to retrieve attribute values calling an external
 * database, calling the external API or with any other method.
 */
public class EnvironmentAttributeFinder extends AbstractPIPAttributeFinder {

    private static final Log LOG = LogFactory.getLog(EnvironmentAttributeFinder.class);
 //   private static final String USER_PERMISSION_CLAIM_URI = "https://sample.com/claims/permission";
    private static final String ENVIRONMENT_PURPOSE_CLAIM_URI = "https://dit.us/claims/purpose";
    private static final String AGE_CLAIM_URI = "http://wso2.org/claims/age";

    /**
     * The required properties should be defined against the registered module in the deployment.toml.
     * This method reads the properties and initiate the module.
     */
    @Override
    public void init(Properties properties) throws Exception {

        // Here you can write the logic to initialize your module. Any properties that
        // are defined for the custom module in the deployment.toml can be used here.
        // Example deployment.toml configuration.

        // [[identity.entitlement.extension]]
        // name="org.wso2.carbon.identity.entitlement.samples.service.pip.SampleAttributeFinder"
        // [identity.entitlement.extension.properties]
        // You can add required properties here.
    }

    @Override
    public Set<String> getAttributeValues(String subjectId, String resourceId, String actionId,
                                          String environmentId, String attributeId, String issuer) throws
            Exception {

        Set<String> attributeValues = new HashSet<>();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Retrieving attribute values of subjectId " + subjectId + " with attributeId " +
                    attributeId );
        }

        if (StringUtils.isEmpty(subjectId)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("subjectId value is null or empty. Returning empty attribute set");
            }
            
        }
        if (attributeId.equalsIgnoreCase(ENVIRONMENT_PURPOSE_CLAIM_URI))
        // This logic depends on the customization that you need to achieve. Here we return the
        // hardcoded attribute values. You can customize the logic with any other way you want.
        	attributeValues.add("PURPOSE");
        if (attributeId.equalsIgnoreCase(AGE_CLAIM_URI)&& StringUtils.isNotEmpty(subjectId)) {
        	subjectId = MultitenantUtils.getTenantAwareUsername(subjectId);	
        
        		 String claimValue = null;
                 try {
                     claimValue = CarbonContext.getThreadLocalCarbonContext().getUserRealm().
                             getUserStoreManager().getUserClaimValue(subjectId, "http://wso2.org/claims/dob", null);
                     if (LOG.isDebugEnabled()) {
                         LOG.debug("Claim \'" + claimValue + "\' retrieved for attributeId \'" + attributeId + "\' " +
                                 "for subjectId \'" + subjectId + "\'");
                     }
                     DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                     LocalDate fechaNac = LocalDate.parse(claimValue, fmt);
                     LocalDate ahora = LocalDate.now();

                     int years = Period.between(fechaNac, ahora).getYears();
                     
                     attributeValues.add(String.valueOf(years));
                     
                 } catch (UserStoreException e) {
                     if(e.getMessage().startsWith(IdentityCoreConstants.USER_NOT_FOUND)){
                         if(LOG.isDebugEnabled()){
                             LOG.debug("User: " + subjectId + " not found in user store");
                         }
                     } else {
                         throw e;
                     }
                         	  
        	}
        	
        }
        return attributeValues;
    }

    @Override
    public String getModuleName() {

        return "Environment Attributes Finder";
    }

    @Override
    public Set<String> getSupportedAttributes() {

        Set<String> supportedAttrs = new HashSet<>();
        supportedAttrs.add(ENVIRONMENT_PURPOSE_CLAIM_URI);
        supportedAttrs.add(AGE_CLAIM_URI);
        return supportedAttrs;
    }
}
