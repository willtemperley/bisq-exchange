package io.bisq.api.service;

import com.google.inject.Inject;
import io.bisq.api.BitsquareProxy;
import io.bisq.common.crypto.KeyRing;
import io.bisq.core.btc.wallet.BtcWalletService;
import io.bisq.core.offer.OfferBookService;
import io.bisq.core.offer.OpenOfferManager;
import io.bisq.core.provider.price.PriceFeedService;
import io.bisq.core.trade.TradeManager;
import io.bisq.core.user.User;
import io.bisq.network.p2p.P2PService;
import io.dropwizard.Application;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DropwizardApplication extends Application<ApiConfiguration> {
    @Inject
    BtcWalletService walletService;

    @Inject
    TradeManager tradeManager;

    @Inject
    OpenOfferManager openOfferManager;

    @Inject
    OfferBookService offerBookService;

    @Inject
    P2PService p2PService;

    @Inject
    KeyRing keyRing;

    @Inject
    PriceFeedService priceFeedService;

    @Inject
    User user;

    public static void main(String[] args) throws Exception {
        new DropwizardApplication().run(args);
    }

    @Override
    public String getName() {
        return "Bitsquare API";
    }

    @Override
    public void initialize(Bootstrap<ApiConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new ResourceConfigurationSourceProvider());
    }

    @Override
    public void run(ApiConfiguration configuration,
                    Environment environment) {
//        environment.getObjectMapper().configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        BitsquareProxy bitsquareProxy = new BitsquareProxy(walletService, tradeManager, openOfferManager,
                offerBookService, p2PService, keyRing, priceFeedService, user);
        final ApiResource resource = new ApiResource(
                configuration.getTemplate(),
                configuration.getDefaultName(),
                bitsquareProxy
        );
        environment.jersey().register(resource);
    }

}