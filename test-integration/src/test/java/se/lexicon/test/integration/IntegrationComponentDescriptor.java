package se.lexicon.test.integration;


import com.so4it.gs.rpc.Component;
import com.so4it.gs.rpc.ServiceBindingType;
import se.lexicon.market.component.service.MarketComponentServiceProvider;
import se.lexicon.order.component.service.OrderComponentServiceProvider;


/**
 * To be able to run the test with multiple components deployed we created this test component descriptor
 * that includes providers from multiple components.
 */

@Component(
        name = "test",
        serviceProviders = {
                OrderComponentServiceProvider.class,
                MarketComponentServiceProvider.class

        },
        defaultServiceType = ServiceBindingType.GS_REMOTING
)
public class IntegrationComponentDescriptor {
}
