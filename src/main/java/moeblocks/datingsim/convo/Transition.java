package moeblocks.datingsim.convo;

import moeblocks.datingsim.convo.enums.Response;
import moeblocks.entity.AbstractNPCEntity;

import java.util.function.Function;

public class Transition {
    private final Response response;
    private final Function<AbstractNPCEntity, Scene> function;
    
    public Transition(Response response, Function<AbstractNPCEntity, Scene> function) {
        this.response = response;
        this.function = function;
    }
    
    public Response getResponse() {
        return this.response;
    }
    
    public Scene getScene(AbstractNPCEntity npc) {
        return this.function.apply(npc);
    }
}
