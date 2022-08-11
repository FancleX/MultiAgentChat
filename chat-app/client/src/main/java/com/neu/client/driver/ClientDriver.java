package com.neu.client.driver;

import com.neu.client.sharableResource.SharableResource;
import com.neu.client.ui.UI;

public class ClientDriver {

    public ClientDriver(String hostname, int port) {
        SharableResource.init(hostname, port);
        new Thread(new UI()).start();
    }
}
