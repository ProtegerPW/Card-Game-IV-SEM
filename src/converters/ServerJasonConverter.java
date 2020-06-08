package converters;

import com.company.GameServer;

public class ServerJasonConverter extends JsonConverter<GameServer> {

    public ServerJasonConverter(String jsonFilename) {
        super(jsonFilename);
    }
}

