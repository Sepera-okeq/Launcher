package ru.gravit.launchserver.websocket.json.auth;

import io.netty.channel.ChannelHandlerContext;
import ru.gravit.launcher.events.request.ErrorRequestEvent;
import ru.gravit.launcher.events.request.SetProfileRequestEvent;
import ru.gravit.launcher.profiles.ClientProfile;
import ru.gravit.launchserver.LaunchServer;
import ru.gravit.launchserver.socket.Client;
import ru.gravit.launchserver.websocket.WebSocketService;
import ru.gravit.launchserver.websocket.json.JsonResponseInterface;
import ru.gravit.launchserver.websocket.json.SimpleResponse;

import java.util.Collection;

public class SetProfileResponse extends SimpleResponse {
    public String client;

    @Override
    public String getType() {
        return "setProfile";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client client) throws Exception {
        if (!client.isAuth) {
            sendError("Access denied");
            return;
        }
        Collection<ClientProfile> profiles = LaunchServer.server.getProfiles();
        for (ClientProfile p : profiles) {
            if (p.getTitle().equals(this.client)) {
                if (!p.isWhitelistContains(client.username)) {
                    sendError(LaunchServer.server.config.whitelistRejectString);
                    return;
                }
                client.profile = p;
                sendResult(new SetProfileRequestEvent(p));
                return;
            }
        }
        sendError("Profile not found");
    }
}
