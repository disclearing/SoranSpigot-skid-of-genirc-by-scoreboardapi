package net.minecraft.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.IOException; // CraftBukkit
import java.util.concurrent.ExecutorService;

public class PacketPlayInChat extends Packet {

    private String message;

    public PacketPlayInChat() {}

    public PacketPlayInChat(String s) {
        if (s.length() > 100) {
            s = s.substring(0, 100);
        }

        this.message = s;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException { // CraftBukkit - added throws
        this.message = packetdataserializer.c(100);
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException { // CraftBukkit - added throws
        packetdataserializer.a(this.message);
    }

    public void a(PacketPlayInListener packetplayinlistener) {
        packetplayinlistener.a(this);
    }

    public String b() {
        return String.format("message=\'%s\'", new Object[] { this.message});
    }

    public String c() {
        return this.message;
    }

    // CraftBukkit start - make chat async
    @Override
    public boolean a() {
        return !this.message.startsWith("/");
    }
    // CraftBukkit end

    // Spigot Start
    private static final ExecutorService executors = java.util.concurrent.Executors.newCachedThreadPool(
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat( "Async Chat Thread - #%d" ).build()
    );

    public void handle(final PacketListener packetlistener) {
        if (this.a()) {
            executors.submit(() -> PacketPlayInChat.this.a((PacketPlayInListener) packetlistener));
            return;
        }
        // Spigot End
        this.a((PacketPlayInListener) packetlistener);
    }
}
