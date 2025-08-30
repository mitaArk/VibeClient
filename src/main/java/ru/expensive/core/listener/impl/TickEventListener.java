package ru.expensive.core.listener.impl;

import ru.expensive.api.event.EventHandler;
import ru.expensive.core.Expensive;
import ru.expensive.core.listener.Listener;
import ru.expensive.implement.events.player.PostTickEvent;
import ru.expensive.implement.events.player.TickEvent;
import ru.expensive.implement.features.modules.combat.AuraModule;

public class TickEventListener implements Listener {
    @EventHandler
    public void onTick(TickEvent tickEvent) {
        Expensive.getInstance().getAttackPerpetrator().tick();

    }
}
