package me.heartalborada.bilidownloader;

import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;

public class test {

    public static void main(String[] args) {
        boolean found = new NativeDiscovery().discover();
        System.out.println(found);
    }
}

