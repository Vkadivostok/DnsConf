package com.novibe.common.data_sources;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class HostsOverrideListsLoader extends ListLoader<HostsOverrideListsLoader.BypassRoute> {

    public record BypassRoute(String ip, String website) {
    }

    @Override
    protected Stream<BypassRoute> lineParser(String urlList) {
        return Pattern.compile("\\r?\\n").splitAsStream(urlList)
                .parallel()
                .map(String::strip)
                .filter(str -> !str.isBlank())
                .filter(line -> !line.startsWith("#"))
                .filter(line -> !HostsBlockListsLoader.isBlock(line))
                .map(this::mapLine)
                .filter(route -> route != null);
    }

    @Override
    protected String listType() {
        return "Override";
    }

    private BypassRoute mapLine(String line) {
        String[] parts = line.split("\\s+", 3);
        if (parts.length < 2) {
            return null;
        }
        String ip = parts[0];
        String website = parts[1];
        return new BypassRoute(ip, website);
    }

}
