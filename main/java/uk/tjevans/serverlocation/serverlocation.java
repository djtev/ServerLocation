package uk.tjevans.serverlocation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Mod(modid = "Server Location", version = "0.1", acceptedMinecraftVersions = "[1.8]")
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
        for(int i = 4; i < split.length; i++)
        {
            server+=(" " + split[i]);
        }
	}

    @SubscribeEvent
  public void onRenderChatGui(GuiScreenEvent.DrawScreenEvent.Post e) {
    if (!isOnHypixel || !(e.gui instanceof GuiChat)){
      return;
    }
    e.gui.drawCenteredString(MINECRAFT.fontRendererObj, EnumChatFormatting.WHITE + 
        "Instance: " + server, e.gui.width - 40, 7, 0xFF);
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
  
  @SubscribeEvent
    public void joinWorld(FMLNetworkEvent.ClientConnectedToServerEvent e)
    {
	  Minecraft.getMinecraft().thePlayer.sendChatMessage("/whereami");
	}
}