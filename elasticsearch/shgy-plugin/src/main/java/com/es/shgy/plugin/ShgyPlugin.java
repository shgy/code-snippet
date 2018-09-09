package com.es.shgy.plugin;

import com.es.shgy.action.bitcnt.BitCntAction;
import com.es.shgy.action.bitcnt.TransportBitCntAction;
import com.es.shgy.rest.action.HelloRestAction;
import org.elasticsearch.action.ActionModule;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestModule;

public class ShgyPlugin extends Plugin {
    @Override
    public String name() {
        return "shgy-plugin";
    }

    @Override
    public String description() {
        return "shgy plugin for bitmap";
    }



    public void onModule(ActionModule module){

        module.registerAction(BitCntAction.INSTANCE, TransportBitCntAction.class);
    }
    public void onModule(RestModule module){
        module.addRestAction(HelloRestAction.class);
    }
}
