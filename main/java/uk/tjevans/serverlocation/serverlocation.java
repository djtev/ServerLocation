package uk.tjevans.serverlocation;

import net.minecraft.client.Minecraft;
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
public class serverlocation
{

  private static final Minecraft MINECRAFT = Minecraft.getMinecraft();
  private boolean isOnHypixel;
  public String server;

  @EventHandler
  public void init(FMLInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(this);
    FMLCommonHandler.instance().bus().register(this);
  }
  
  @SubscribeEvent
    public void checkChat(ClientChatReceivedEvent e)
	{
		 if(e.type!=0)
        {
            return;
        }
        String chat = e.message.getUnformattedText().replaceAll("\u00A7.", "");
        if(!chat.contains("You are currently on server"))
        {
            return;
        }
        String[] split = chat.split(" ");
        server = split[4];
	}

    @SubscribeEvent
  public void RenderGameOverlayEvent(RenderGameOverlayEvent e) {
    if (!isOnHypixel || !MINECRAFT.inGameHasFocus){
      return;
    }
	MINECRAFT.fontRendererObj.drawStringWithShadow("Instance: " + server, 10, 10, 0xC838FC);
	}
  
  @SubscribeEvent
  public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent e) {
    final ServerData data = MINECRAFT.getCurrentServerData();
    if (data != null && data.serverIP.contains("hypixel.net")) {
      isOnHypixel = true;
    }
  }

  @SubscribeEvent
  public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
    isOnHypixel = false;
  }
  
  @EventHandler
  public void onServerConnect() {
	  MINECRAFT.thePlayer.sendChatMessage("/whereami");
	}
}
