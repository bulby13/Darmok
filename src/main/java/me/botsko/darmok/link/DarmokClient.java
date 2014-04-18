package me.botsko.darmok.link;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import me.botsko.darmok.Darmok;
import me.botsko.darmok.commands.ChannelCommands;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class DarmokClient implements Runnable {
    
    public static PrintWriter out;
    public static String ident;
    
    private static BufferedReader in;
    private String hostname;
    private int port;
    private String password;
    private ChannelCommands commandHandler;
    
    
    /**
     * 
     * @param version
     * @param config
     */
    public DarmokClient( FileConfiguration config ){
        hostname = config.getString("darmok.link.client.hostname");
        port = config.getInt("darmok.link.client.port");
        password = config.getString("darmok.link.client.password");
        ident = config.getString("darmok.link.client.name");
//        this.commandHandler = new ChannelCommands(plugin);
//        this.playerListener = new DarmokPlayerListener(plugin);
    }

    
    /**
     * 
     */
    public void run(){
        while(true){
            try {
                Socket relay = new Socket(this.hostname, this.port);
                DarmokClient.out = new PrintWriter(relay.getOutputStream(), true);
                DarmokClient.in = new BufferedReader(new InputStreamReader(relay.getInputStream()));
                out.println("VERSION " + Darmok.plugin_version);
                out.println("AUTH " + ident + " " +  password);
                String line;
                while((line = in.readLine()) != null){
                    cmd_exec(line);
                }
            } catch (UnknownHostException e) {
                Darmok.log("We were unable to connect to the Darmok Chat link. Stack trace: " + e);
                return;
            } catch (IOException e) {
                Darmok.log("Darmok Chat link was broken. Reconnecting in 30 seconds.");
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    return;
                }
            }
        }
    }
    
    public void cmd_exec(String line){
        Darmok.debug("RECEIVED: " + line);
        String[] args = line.split(" ");
        if(args[0].equals("UPD")){
//            servers = Arrays.copyOfRange(args, 1, args.length);
            return;
        }
        Command c = null;
        try{
            c = Command.valueOf(args[0]);
        } catch(Exception e){
            return;
        }
        String cmd;
        String[] new_args;
        DarmokUser player;
        switch(c){
            case CMSG:
                player = new RemoteUser(args[1]);
                cmd = args[2];
                for(int i = 3; i < args.length; i++){
                    cmd += " " + args[i];
                }
//                this.playerListener.onPlayerCommandPreprocess(player, cmd);
                return;
            case CBAN:
                cmd = "ch";
                player = new RemoteUser(args[1]);
                new_args = new String[]{"ban", args[2].split("@")[0], args[3]};
                break;
            case CUNBAN:
                cmd = "ch";
                player = new RemoteUser(args[1]);
                new_args = new String[]{"unban", args[2].split("@")[0], args[3]};
                break;
            case EMSG:
                Player ply = Bukkit.getServer().getPlayer(args[1].split("@")[0]);
                if(ply != null){
                    String response = "";
                    for(int i=2;i<args.length;i++){
                        response += args[i] + " ";
                    }
                    ply.sendMessage(Darmok.messenger.playerError(response.trim()));
                }
                return;
            default:
                return;
        }
        System.out.println( "CMD_EXEC: " +cmd );
//        System.out.println(player + ", " + cmd + ", " + new_args);
//        for(String x : new_args){
//            System.out.println(x);
//        }
//        commandHandler.onCommand(player, plugin.getCommand(cmd), cmd, new_args);
    }

    /**
     * 
     * @author botskonet
     *
     */
    enum Command{
        UPD,
        CBAN,
        CUNBAN,
        CMSG,
        EMSG;
    }
}


/**
 * 
 * @author botskonet
 *
 */
interface Handler{
    public void handle(String[] args);
}