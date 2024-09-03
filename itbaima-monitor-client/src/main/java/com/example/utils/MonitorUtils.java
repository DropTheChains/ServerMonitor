package com.example.utils;

import com.example.entity.BaseDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;


@Slf4j
@Component
public class MonitorUtils {

    private final SystemInfo info = new SystemInfo();

    private final Properties properties = System.getProperties();

    public BaseDetail monitorBaseDetail() {
        OperatingSystem os  = info.getOperatingSystem();
        HardwareAbstractionLayer hardware = info.getHardware();
        double memory = hardware.getMemory().getTotal() / 1024.0 /1024.0 /1024.0;
        double diskSize = Arrays.stream(File.listRoots()).mapToLong(File::getTotalSpace).sum() / 1024.0 /1024.0 /1024.0;
        String ip = Objects.requireNonNull(this.findNetworkInterface(hardware)).getIPv4addr()[0];
        return new BaseDetail()
                .setOsArch(properties.getProperty("os.arch"))
                .setOsName(os.getFamily())
                .setOsVersion(os.getVersionInfo().getVersion())
                .setOsBit(os.getBitness())
                .setCpuCore(hardware.getProcessor().getPhysicalProcessorCount())
                .setCpuName(hardware.getProcessor().getProcessorIdentifier().getName())
                .setMemory(memory)
                .setDisk(diskSize)
                .setIp(ip);
    }

    private NetworkIF findNetworkInterface(HardwareAbstractionLayer hardware) {
        try {
            for (NetworkIF networkIF : hardware.getNetworkIFs()) {
                String[] iPv4addr = networkIF.getIPv4addr();
                NetworkInterface ni  = networkIF.queryNetworkInterface();
                if (!ni.isLoopback() && !ni.isPointToPoint() && ni.isUp() && !ni.isVirtual()
                        && (ni.getName().startsWith("eth") || ni.getName().startsWith("en"))
                        && iPv4addr.length > 0){
                    return networkIF;
                }
            }
        }catch (IOException e){
            log.error("读取网络接口信息时出错",e);
        }
    return null;
    }
}
