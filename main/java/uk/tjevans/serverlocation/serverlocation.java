package uk.tjevans.serverlocation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

@Mod(modid = "server location", version = "0.1", acceptedMinecraftVersions = "[1.8]")
public class serverlocation{

    private boolean isOnHypixel = false;
    private String server = "NULL";
    private Thread thread = null;
    private int timeOut = 30000;
    private boolean askedForCommand = false;

    @EventHandler
    public void init(FMLInitializationEvent event){
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(timeOut);
                        issueLocationCommand();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void checkChat(ClientChatReceivedEvent e){
        if(e.type!=0){
            return;
        }
        String chat = e.message.getUnformattedText().replaceAll("\u00A7.", "");
        if(chat.contains(":") || !chat.contains("You are currently on server") || !chat.contains("An internal error occurred")){
            return;
        }
        else if(chat.contains("An internal error occurred")){
            server = "Limbo?";
            e.setCanceled(true);
        }else {
            String[] split = chat.split(" ");
            server = split[5];
        }
        if(askedForCommand) {
            e.setCanceled(true);
            askedForCommand = false;
        }
    }

    @SubscribeEvent
    public void RenderGameOverlayEvent(RenderGameOverlayEvent e) {
        if(e.type != RenderGameOverlayEvent.ElementType.TEXT){
            return;
        }
        if (!isOnHypixel || (!Minecraft.getMinecraft().inGameHasFocus && !(Minecraft.getMinecraft().currentScreen != null && (Minecraft.getMinecraft().currentScreen instanceof GuiChat)))){
            return;
        }
        drawInstanceOnScreen();
    }


    private void drawInstanceOnScreen(){
    	Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Hypixel", 10, 5, 0xC838FC);
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Instance: " + server, 10, 15, 0xC838FC);

    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        final ServerData data = Minecraft.getMinecraft().getCurrentServerData();
        if (data != null && data.serverIP.contains("hypixel.net")) {
            isOnHypixel = true;
            startServerCheckThread();
        }
    }

    private void startServerCheckThread(){
        thread.start();
    }

    private void stopServercheckThread(){
        thread.interrupt();
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        isOnHypixel = false;
        stopServercheckThread();
    }

    public void issueLocationCommand() {
        askedForCommand = true;
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/whereami");
    }
}
