package com.networkmonitor.service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.networkmonitor.model.DNSResult;

@Service
public class DNSService {

    public DNSResult resolve(String target) {
        DNSResult result = new DNSResult();
        result.setHostname(target);

        try {
            InetAddress[] addresses = InetAddress.getAllByName(target);
            List<String> values = new ArrayList<>();

            for (InetAddress address : addresses) {
                values.add(address.getHostAddress());
            }

            result.setAddresses(values);
            result.setResolved(!values.isEmpty());

        } catch (Exception e) {
            result.setResolved(false);
            result.setErrorMessage(e.getMessage());
        }

        return result;
    }
}
