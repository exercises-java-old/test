package se.lexicon.test.integration;


import com.so4it.gs.rpc.Component;
import com.so4it.gs.rpc.ServiceBindingType;
import se.lexicon.market.component.service.MarketComponentServiceProvider;
import se.lexicon.order.component.service.OrderComponentServiceProvider;

@Component(
        name = "checkout",
        serviceProviders = {
                OrderComponentServiceProvider.class,
                MarketComponentServiceProvider.class

        },
        defaultServiceType = ServiceBindingType.GS_REMOTING
)
public class IntegrationComponentDescriptor {
}
