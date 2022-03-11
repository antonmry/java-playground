package impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.spi.InetAddressResolver;
import java.net.spi.InetAddressResolver.LookupPolicy;
import java.net.spi.InetAddressResolverProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class SimpleResolverProviderImpl extends InetAddressResolverProvider {

    @Override
    public InetAddressResolver get(Configuration configuration) {
        System.out.println("The following provider will be used by current test:" + this.getClass().getCanonicalName());
        return new InetAddressResolver() {
            @Override
            public Stream<InetAddress> lookupByName(String host, LookupPolicy lookupPolicy) throws UnknownHostException {
              return Stream.of(InetAddress.getByName("127.0.0.1"));
            }

            @Override
            public String lookupByAddress(byte[] addr) throws UnknownHostException {
                return "localhost";
            }
        };
    }

    @Override
    public String name() {
        return "simpleInetAddressResolver";
    }
}

