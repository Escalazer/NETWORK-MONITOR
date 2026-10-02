package com.networkmonitor.service;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.springframework.stereotype.Service;

import com.networkmonitor.model.NetworkInterfaceInfo;

@Service
public class NetworkInterfaceService {

    public List<NetworkInterfaceInfo> getLocalInterfaces() {
        List<NetworkInterfaceInfo> results = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> interfaces =
                    NetworkInterface.getNetworkInterfaces();

            while (interfaces != null && interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                    continue;
                }

                byte[] mac = networkInterface.getHardwareAddress();
                String macAddress = formatMac(mac);

                Enumeration<InetAddress> addresses =
                        networkInterface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();

                    if (!(address instanceof Inet4Address)) {
                        continue;
                    }

                    NetworkInterfaceInfo info = new NetworkInterfaceInfo();
                    info.setName(networkInterface.getName());
                    info.setDisplayName(networkInterface.getDisplayName());
                    info.setIpAddress(address.getHostAddress());
                    info.setMacAddress(macAddress);
                    info.setUp(true);

                    results.add(info);
                }
            }

        } catch (Exception ignored) {
            // Return the interfaces collected so far.
        }

        return results;
    }

    private String formatMac(byte[] mac) {
        if (mac == null || mac.length == 0) {
            return "N/A";
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < mac.length; i++) {
            if (i > 0) {
                builder.append(":");
            }
            builder.append(String.format("%02X", mac[i]));
        }

        return builder.toString();
    }
}
