package se.lexicon.test.integration;

import com.so4it.api.test.common.ApiFrameworkCommonTest;
import com.so4it.common.bean.MapBeanContext;
import com.so4it.common.jmx.MBeanRegistry;
import com.so4it.common.jmx.MBeanRegistryFactory;
import com.so4it.common.util.PortUtil;
import com.so4it.configuration.core.DynamicConfiguration;
import com.so4it.configuration.test.common.TestConfigurationSource;
import com.so4it.configuration.test.common.TestConfigurationSourceTestRule;
import com.so4it.gs.rpc.test.common.ServiceBeanStateRegistry;
import com.so4it.gs.rpc.test.common.ServiceBindingRule;
import com.so4it.gs.rpc.test.common.ServiceFrameworkCommonTest;
import com.so4it.registry.core.api.ApiRegistryClient;
import com.so4it.registry.core.service.ServiceRegistryClient;
import com.so4it.registry.test.common.FakeApiRegistry;
import com.so4it.registry.test.common.FakeServiceRegistry;
import com.so4it.test.gs.rule.GigaSpaceEmbeddedLusTestRule;
import com.so4it.test.springframework.SpringContextRule;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Suite;
import se.lexicon.market.component.service.MarketComponentServiceProvider;
import se.lexicon.order.component.service.OrderComponentServiceProvider;

/**
 * @author Magnus Poromaa {@literal <mailto:magnus.poromaa@so4it.com/>}
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        OrderApiClientClientIntegrationTest.class
})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IntegrationTestSuite {

    private static final int LUS_PORT = PortUtil.nextFreePort();

    public static final ApiRegistryClient API_REGISTRY = new FakeApiRegistry();

    public static final TestConfigurationSource CONFIGURATION_SOURCE = new TestConfigurationSource();

    public static final DynamicConfiguration DYNAMIC_CONFIGURATION = ApiFrameworkCommonTest.createDynamicConfiguration(PortUtil.nextFreePort(), CONFIGURATION_SOURCE, ServiceFrameworkCommonTest.createConfigurationSource());

    private static final ServiceRegistryClient SERVICE_REGISTRY = new FakeServiceRegistry();

    private static final ServiceBeanStateRegistry SERVICE_BEAN_STATE_REGISTRY = new ServiceBeanStateRegistry();

    private static GigaSpaceEmbeddedLusTestRule GIGA_SPACE_TEST_RULE;

    private static SpringContextRule COMPONENT_TEST_RULE;

    private static SpringContextRule API_TEST_RULE;

    private static TestConfigurationSourceTestRule CONFIGURATION_SOURCE_TEST_RULE;

    private static ServiceBindingRule SERVICE_BINDING_RULE;

    @ClassRule
    public static final RuleChain SUITE_RULE_CHAIN = RuleChain
            .outerRule(getGigaSpacesRule())
            .around(getExportRule())
            .around(getImportRule())
            .around(getConfigurationSourceRule())
            .around(getServiceBindingRule());

    public static SpringContextRule getExportRule() {
        if (COMPONENT_TEST_RULE == null) {
            COMPONENT_TEST_RULE = new SpringContextRule();
            COMPONENT_TEST_RULE.addXmlConfiguration("fault-tolerance-common.xml");
            COMPONENT_TEST_RULE.addXmlConfiguration("metric-springframework.xml");

            //Add order component
            COMPONENT_TEST_RULE.addXmlConfiguration("order-component-dao.xml");
            COMPONENT_TEST_RULE.addXmlConfiguration("order-component-service.xml");

            //Add market component
            COMPONENT_TEST_RULE.addXmlConfiguration("market-component-dao.xml");
            COMPONENT_TEST_RULE.addXmlConfiguration("market-component-service.xml");

            //Add the API clients we are using in the components
            COMPONENT_TEST_RULE.addXmlConfiguration("market-api-client.xml");
            COMPONENT_TEST_RULE.addXmlConfiguration("order-api-client.xml");
            COMPONENT_TEST_RULE.addXmlConfiguration("integration-test-api-import.xml");

            //Theses are just here to enable testing multiple components in a single spring context
            COMPONENT_TEST_RULE.addXmlConfiguration("integration-test-space.xml");
            COMPONENT_TEST_RULE.addXmlConfiguration("integration-test-service-export.xml");

            //Add the framework stuff needed to stitch everything together
            COMPONENT_TEST_RULE.addBean(MBeanRegistry.DEFAULT_BEAN_NAME, MBeanRegistryFactory.getDefaultRegistry());
            COMPONENT_TEST_RULE.addBean(ApiRegistryClient.DEFAULT_API_BEAN_NAME, API_REGISTRY);
            COMPONENT_TEST_RULE.addBean(ServiceRegistryClient.DEFAULT_API_BEAN_NAME, SERVICE_REGISTRY);
            COMPONENT_TEST_RULE.addBean(DynamicConfiguration.DEFAULT_BEAN_NAME, DYNAMIC_CONFIGURATION);
            COMPONENT_TEST_RULE.addBean(MapBeanContext.DEFAULT_BEAN_NAME, new MapBeanContext());
            COMPONENT_TEST_RULE.addBean(ServiceBeanStateRegistry.DEFAULT_BEAN_NAME, SERVICE_BEAN_STATE_REGISTRY);
        }
        return COMPONENT_TEST_RULE;
    }

    public static SpringContextRule getImportRule() {
        if (API_TEST_RULE == null) {
            API_TEST_RULE = new SpringContextRule();
            API_TEST_RULE.addXmlConfiguration("fault-tolerance-common.xml");
            API_TEST_RULE.addXmlConfiguration("fault-tolerance-common.xml");
            API_TEST_RULE.addXmlConfiguration("metric-springframework.xml");

            API_TEST_RULE.addXmlConfiguration("order-component-client.xml");
            API_TEST_RULE.addXmlConfiguration("market-component-client.xml");
            API_TEST_RULE.addXmlConfiguration("integration-test-service-import.xml");


            API_TEST_RULE.addXmlConfiguration("order-api-server.xml");
            API_TEST_RULE.addXmlConfiguration("market-api-server.xml");
            API_TEST_RULE.addXmlConfiguration("integration-test-api-export.xml");


            API_TEST_RULE.addBean(MBeanRegistry.DEFAULT_BEAN_NAME, MBeanRegistryFactory.getDefaultRegistry());
            API_TEST_RULE.addBean(ApiRegistryClient.DEFAULT_API_BEAN_NAME, API_REGISTRY);
            API_TEST_RULE.addBean(ServiceRegistryClient.DEFAULT_API_BEAN_NAME, SERVICE_REGISTRY);
            API_TEST_RULE.addBean(DynamicConfiguration.DEFAULT_BEAN_NAME, DYNAMIC_CONFIGURATION);
            API_TEST_RULE.addBean(MapBeanContext.DEFAULT_BEAN_NAME, new MapBeanContext());
            API_TEST_RULE.addBean(ServiceBeanStateRegistry.DEFAULT_BEAN_NAME, SERVICE_BEAN_STATE_REGISTRY);
        }
        return API_TEST_RULE;
    }

    public static GigaSpaceEmbeddedLusTestRule getGigaSpacesRule() {
        if (GIGA_SPACE_TEST_RULE == null) {
            GIGA_SPACE_TEST_RULE = new GigaSpaceEmbeddedLusTestRule(LUS_PORT);
        }
        return GIGA_SPACE_TEST_RULE;
    }


    public static TestConfigurationSourceTestRule getConfigurationSourceRule() {
        if (CONFIGURATION_SOURCE_TEST_RULE == null) {
            CONFIGURATION_SOURCE_TEST_RULE = new TestConfigurationSourceTestRule(CONFIGURATION_SOURCE);
        }
        return CONFIGURATION_SOURCE_TEST_RULE;
    }

    public static ServiceBindingRule getServiceBindingRule() {
        if (SERVICE_BINDING_RULE == null) {
            SERVICE_BINDING_RULE = new ServiceBindingRule(SERVICE_BEAN_STATE_REGISTRY);
            SERVICE_BINDING_RULE.addServiceProvider(MarketComponentServiceProvider.class);
            SERVICE_BINDING_RULE.addServiceProvider(OrderComponentServiceProvider.class);
        }
        return SERVICE_BINDING_RULE;
    }

    public static ApiRegistryClient getApiRegistry() {
        return API_REGISTRY;
    }

    public static DynamicConfiguration getDynamicConfiguration() {
        return DYNAMIC_CONFIGURATION;
    }
}
